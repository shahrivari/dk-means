package org.tmu.util;

import org.apache.commons.math3.stat.clustering.Cluster;
import org.apache.commons.math3.stat.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.tmu.clustering.DKMeansClusterer;
import org.tmu.clustering.SimpleKMeansClusterer;
import com.google.common.base.Stopwatch;

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


    public static void doTestFromFile(final Collection<Point> points, int max_cluster_count, int kmeans_iter, int test_loop) throws IOException, InterruptedException {

    }
    public static void doTestFromFile(final String file_name, int max_cluster_count, int kmeans_iter, int test_loop) throws IOException, InterruptedException {
        Stopwatch watch = new Stopwatch().start();
        points = CSVReader.readWholeFile(file_name);

        System.out.println("Reading file took: " + watch.stop());

        for (int j = max_cluster_count; j <= max_cluster_count; j++) {
            System.out.println("=========================================== Cluster Count: "+j);
            List<Cluster<Point>> clusters;

            ArrayList<Double> millis = new ArrayList<Double>();
            ArrayList<Double> SSEs = new ArrayList<Double>();
            ArrayList<Double> ICDs = new ArrayList<Double>();
            DecimalFormat format = new DecimalFormat("#.###");
            StandardDeviation standardDeviation = new StandardDeviation();
            Mean mean = new Mean();

            for (int i = 0; i < test_loop; i++) {
                watch.reset().start();
                clusters = kmpp.cluster(points, j, kmeans_iter);
                millis.add((double) watch.stop().elapsedMillis());
                SSEs.add(computeSSE(clusters));
                ICDs.add(computeIntraCenterDistance(clusters));
//            System.out.print("KMeans++ took: " + watch.stop());
//            System.out.println("Centers :");
//            printCenter(clusters);
//            System.out.print("SSE is: " + computeSSE(clusters));
//            System.out.println("ICD is: " + computeIntraCenterDistance(clusters));
//            System.out.println("===========================================");
            }

            System.out.println("KMeans++\t" + format.format(mean.evaluate(toArray(millis))) + ":" +
                    format.format(standardDeviation.evaluate(toArray(millis))) + "\tSSE:" +
                    format.format(mean.evaluate(toArray(SSEs))) + ":" +
                    format.format(standardDeviation.evaluate(toArray(SSEs))) + "\tICD:" +
                    format.format(mean.evaluate(toArray(ICDs))) + ":" +
                    format.format(standardDeviation.evaluate(toArray(ICDs))));


            millis.clear();
            SSEs.clear();
            ICDs.clear();
            for (int i = 0; i < test_loop; i++) {
                watch.reset().start();
                clusters = km.cluster(points, j, kmeans_iter);
                millis.add((double) watch.stop().elapsedMillis());
                SSEs.add(computeSSE(clusters));
                ICDs.add(computeIntraCenterDistance(clusters));
            }
            System.out.println("KMeans  \t" + format.format(mean.evaluate(toArray(millis))) + ":" +
                    format.format(standardDeviation.evaluate(toArray(millis))) + "\tSSE:" +
                    format.format(mean.evaluate(toArray(SSEs))) + ":" +
                    format.format(standardDeviation.evaluate(toArray(SSEs))) + "\tICD:" +
                    format.format(mean.evaluate(toArray(ICDs))) + ":" +
                    format.format(standardDeviation.evaluate(toArray(ICDs))));

            millis.clear();
            SSEs.clear();
            ICDs.clear();
            for (int i = 0; i < test_loop; i++) {
                watch.reset().start();
                clusters = DKMeansClusterer.cluster(points, j);
                millis.add((double) watch.stop().elapsedMillis());
                SSEs.add(computeSSE(clusters));
                ICDs.add(computeIntraCenterDistance(clusters));
            }
            System.out.println("DK-Means\t" + format.format(mean.evaluate(toArray(millis))) + ":" +
                    format.format(standardDeviation.evaluate(toArray(millis))) + "\tSSE:" +
                    format.format(mean.evaluate(toArray(SSEs))) + ":" +
                    format.format(standardDeviation.evaluate(toArray(SSEs))) + "\tICD:" +
                    format.format(mean.evaluate(toArray(ICDs))) + ":" +
                    format.format(standardDeviation.evaluate(toArray(ICDs))));

        }
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
}
