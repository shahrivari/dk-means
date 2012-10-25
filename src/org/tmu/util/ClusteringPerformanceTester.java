package org.tmu.util;

import com.google.common.base.Stopwatch;
import org.apache.commons.math3.stat.clustering.KMeansPlusPlusClusterer;

import java.util.Collection;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Saeed
 * Date: 10/25/12
 * Time: 4:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClusteringPerformanceTester {

    static int iter=3;

    public static void test(int count,int dimension, int cluster_count , int iteration){
        Point center=new Point(dimension);
        for(int j=0;j<center.size();j++)
            center.setElement(j,0);

        for (int i=0;i<iter;i++){
            KMeansPlusPlusClusterer<Point> kMeansPlusPlusClusterer=new KMeansPlusPlusClusterer<Point>(new Random());
            Collection<Point> points=RandomPointGenerator.GenerateDisjointClusters(center, cluster_count, count, new Random());
            Stopwatch watch=new Stopwatch().start();
            kMeansPlusPlusClusterer.cluster(points,cluster_count,iteration);
            System.out.println(watch);
        }
    }

    public static void main(String[] args){
        int[] counts=new int[]{1000,10000,100000,1000000,10000000,20000000,30000000};
        for(int i:counts){
            System.out.println("============ Count: "+i);
            test(i,7,7,10);
        }
    }

}
