package org.tmu;

import org.apache.commons.math3.stat.clustering.Cluster;
import org.apache.commons.math3.stat.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.tmu.clustering.DKMeansClusterer;
import org.tmu.clustering.SimpleKMeansClusterer;
import com.google.common.base.Stopwatch;
import org.tmu.util.CSVReader;
import org.tmu.util.Point;
import org.tmu.util.RandomPointGenerator;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Saeed
 * Date: 10/27/12
 * Time: 4:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class InMemClusteringTester {
    static List<Point> points;
    static KMeansPlusPlusClusterer<Point> kmpp = new KMeansPlusPlusClusterer<Point>(new Random());
    static SimpleKMeansClusterer<Point> km = new SimpleKMeansClusterer<Point>(new Random());


    static ArrayList<Double> millis = new ArrayList<Double>();
    static ArrayList<Double> SSEs = new ArrayList<Double>();
    static ArrayList<Double> ICDs = new ArrayList<Double>();


    public static void doTestFromFile(final String file_name, int cluster_count, int kmeans_max_iterations, int test_loop) throws IOException, InterruptedException {
        Stopwatch watch = new Stopwatch().start();
        points = CSVReader.readWholeFile(file_name);
        System.out.println("Reading csv file took: " + watch.stop());
        System.out.println("=========================================== Cluster Count: " + cluster_count);
        List<Cluster<Point>> clusters;

        clearCounters();
        for (int i = 0; i < test_loop; i++) {
            watch.reset().start();
            clusters = kmpp.cluster(points, cluster_count, kmeans_max_iterations);
            millis.add((double) watch.stop().elapsedMillis());
            SSEs.add(computeSSE(clusters));
            ICDs.add(computeIntraCenterDistance(clusters));
        }
        showInfo("K-Means++");

        clearCounters();
        for (int i = 0; i < test_loop; i++) {
            watch.reset().start();
            clusters = km.cluster(points, cluster_count, kmeans_max_iterations);
            millis.add((double) watch.stop().elapsedMillis());
            SSEs.add(computeSSE(clusters));
            ICDs.add(computeIntraCenterDistance(clusters));
        }
        showInfo("K-Means");

        clearCounters();
        for (int i = 0; i < test_loop; i++) {
            watch.reset().start();
            clusters = DKMeansClusterer.cluster(points, cluster_count);
            millis.add((double) watch.stop().elapsedMillis());
            SSEs.add(computeSSE(clusters));
            ICDs.add(computeIntraCenterDistance(clusters));
        }
        showInfo("DK-Means");
    }

    private static void clearCounters(){
        millis.clear();SSEs.clear();ICDs.clear();
    }
    private static void showInfo(String algorithm){
        DecimalFormat format = new DecimalFormat("#.###");
        StandardDeviation standardDeviation = new StandardDeviation();
        Mean mean = new Mean();
        System.out.println(algorithm+ ":\t" + format.format(mean.evaluate(toArray(millis))) + ":" +
                format.format(standardDeviation.evaluate(toArray(millis))) + "\tSSE:" +
                format.format(mean.evaluate(toArray(SSEs))) + ":" +
                format.format(standardDeviation.evaluate(toArray(SSEs))) + "\tICD:" +
                format.format(mean.evaluate(toArray(ICDs))) + ":" +
                format.format(standardDeviation.evaluate(toArray(ICDs))));

    }

    private static void printCenter(List<Cluster<Point>> clusters) {
        for (Cluster<Point> cluster : clusters)
            System.out.println(cluster.getCenter());
    }

    private static double computeSSE(List<Cluster<Point>> clusters) {
        List<Point> centers = new ArrayList<Point>(clusters.size());
        for (Cluster<Point> cluster : clusters)
            centers.add(cluster.getCenter());
        double sse = 0;
        for (Point p : points)
            sse += Math.sqrt(p.distanceFrom(p.findNearest(centers)));

        return sse;
    }

    private static double computeIntraCenterDistance(List<Cluster<Point>> clusters) {
        List<Point> centers = new ArrayList<Point>(clusters.size());
        for (Cluster<Point> cluster : clusters)
            centers.add(cluster.getCenter());

        double icd = 0;
        for (Point p : centers)
            for (int x = 0; x < centers.size(); x++)
                icd += Math.sqrt(p.distanceFrom(centers.get(x)));

        return icd;
    }

    private static double[] toArray(ArrayList<Double> ar) {
        double[] res = new double[ar.size()];
        for (int i = 0; i < ar.size(); i++)
            res[i] = ar.get(i);
        return res;
    }

    public static void generateSynthesizedDatasets(String dataset_dir) throws IOException {
        Stopwatch watch=new Stopwatch().start();
        for(Integer i:new Integer[]{2,7,13,20}){
            watch.reset().start();
            System.out.println("Generating Dataset with " + i+ " clusters...");
            RandomPointGenerator.GenerateDisjointClustersToFile(new FileWriter(dataset_dir+"/NORM-"+i.toString()+".csv"),i,1000*1000,5,new Random());
            System.out.println("Took: "+watch.stop());
        }
        System.out.println("Synthesized datasets created!");
    }

}
