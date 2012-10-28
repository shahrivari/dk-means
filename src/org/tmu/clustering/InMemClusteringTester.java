package org.tmu.clustering;

import org.apache.commons.math3.stat.clustering.Cluster;
import org.apache.commons.math3.stat.clustering.Clusterable;
import org.apache.commons.math3.stat.clustering.KMeansPlusPlusClusterer;
import org.tmu.util.CSVReader;
import org.tmu.util.Point;
import com.google.common.base.Stopwatch;

import java.io.IOException;
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
    static KMeansPlusPlusClusterer<Point> kmpp=new KMeansPlusPlusClusterer<Point>(new Random());
    static SimpleKMeansClusterer<Point>  km=new SimpleKMeansClusterer<Point>(new Random());


    public static void doTest(final String file_name,int cluster_count,int iter_count) throws IOException, InterruptedException {
        Stopwatch watch=new Stopwatch().start();
        points= CSVReader.readWholeFile(file_name);

        System.out.println("Reading file took: " + watch.stop());
        System.out.println("===========================================");
        watch.reset().start();

        List<Cluster<Point>> clusters=kmpp.cluster(points,cluster_count,iter_count);
        System.out.println("KMeans++ took: "+watch.stop());
        System.out.println("Centers :");
        printCenter(clusters);
        System.out.println("SSE is: " + computeSSE(clusters));
        System.out.println("ICD is: "+computeIntraCenterDistance(clusters));
        System.out.println("===========================================");
        watch.reset().start();

        clusters=km.cluster(points,cluster_count,iter_count);
        System.out.println("KMeans took: "+watch.stop());
        System.out.println("Centers :");
        printCenter(clusters);
        System.out.println("SSE is: "+computeSSE(clusters));
        System.out.println("ICD is: "+computeIntraCenterDistance(clusters));
        System.out.println("===========================================");
        watch.reset().start();

        clusters=DKMeansClusterer.cluster(points,cluster_count,8,1024,10);
        System.out.println("DKMeans took: "+watch.stop());
        System.out.println("Centers :");
        printCenter(clusters);
        System.out.println("SSE is: "+computeSSE(clusters));
        System.out.println("ICD is: "+computeIntraCenterDistance(clusters));
        System.out.println("===========================================");
        watch.reset().start();

    }

    private static void printCenter(List<Cluster<Point>> clusters){
        for(Cluster<Point> cluster:clusters)
            System.out.println(cluster.getCenter());
    }
    private static double computeSSE(List<Cluster<Point>> clusters){
        List<Point> centers=new ArrayList<Point>(clusters.size());
        for(Cluster<Point> cluster:clusters)
            centers.add(cluster.getCenter());
        double sse=0;
        for(Point p:points)
            sse+=Math.sqrt(p.distanceFrom(p.findNearest(centers)));

        return sse;
    }

    private static double computeIntraCenterDistance(List<Cluster<Point>> clusters){
        List<Point> centers=new ArrayList<Point>(clusters.size());
        for(Cluster<Point> cluster:clusters)
            centers.add(cluster.getCenter());

        double icd=0;
        for(Point p:centers)
            for(int x=0;x<centers.size();x++)
                icd+=Math.sqrt(p.distanceFrom(centers.get(x)));

        return icd;
    }
}
