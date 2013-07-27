package org.tmu.clustering;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import org.apache.commons.math3.stat.clustering.Cluster;
import org.apache.commons.math3.stat.clustering.KMeansPlusPlusClusterer;
import org.tmu.util.Point;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Saeed
 * Date: 7/27/13
 * Time: 3:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class MasterPointClusterer {

    public static boolean verbose=true;
    public static Random random=new Random();
    public static void setRandom(Random rand){
        random=rand;
    }

    public static List<Point> KMeans(final List<Point> points, final int k,final int maxIterations){
        ArrayList<Point> centers=new ArrayList<Point>();
        SimpleKMeansClusterer<Point> kmeans=new SimpleKMeansClusterer<Point>(random);
        List<Cluster<Point>> clusters= kmeans.cluster(points,k,maxIterations);
        for(Cluster<Point> cluster:clusters)
            centers.add(cluster.getCenter());
        return centers;
    }

    public static List<Point> KMeansPP(final List<Point> points, final int k,final int maxIterations){
        ArrayList<Point> centers=new ArrayList<Point>();
        KMeansPlusPlusClusterer<Point> kmeans=new KMeansPlusPlusClusterer<Point>(random);
        List<Cluster<Point>> clusters= kmeans.cluster(points,k,maxIterations);
        for(Cluster<Point> cluster:clusters)
            centers.add(cluster.getCenter());
        return centers;
    }

    public static List<Point> InMemDKMeans(final List<Point> points, final int k,final int chunk_size){
        ArrayList<Point> intermediate_centers=new ArrayList<Point>();
        KMeansPlusPlusClusterer<Point> kmpp=new KMeansPlusPlusClusterer<Point>(random);

        Stopwatch watch=new Stopwatch().start();

        for(List<Point> chunk:Lists.partition(points,chunk_size)){
            List<Cluster<Point>> res=kmpp.cluster(chunk,k,(int)Math.log(k)+1);
            for(Cluster<Point> p:res)
                intermediate_centers.add(p.getCenter());
        }

        if(verbose)
            System.out.printf("First pass finished in %s.\n",watch.toString());

        KMeansPlusPlusClusterer<Point> final_kmpp=new KMeansPlusPlusClusterer<Point>(random);
        ArrayList<Point> final_centers=new ArrayList<Point>();

        if(verbose)
            System.out.println("Doing k-means++ on intermediate centers....  \tsize:" + intermediate_centers.size() + "\titerations:" + ((int) Math.log(intermediate_centers.size())));
        List<Cluster<Point>> final_clusters=null;
        final_clusters=final_kmpp.cluster(intermediate_centers, k, (int) Math.log(intermediate_centers.size()));
        for(Cluster<Point> cluster:final_clusters)
            final_centers.add(cluster.getCenter());
        return final_centers;
    }

    public static List<Point> InMemParallelDKMeans(final List<Point> points, final int k,final int chunk_size) throws InterruptedException {
        Stopwatch watch=new Stopwatch().start();
        ImprovedStreamClusterer<Point> smeans=new ImprovedStreamClusterer<Point>(k,(int)Math.log(chunk_size));
        smeans.Start();

        for(List<Point> chunk:Lists.partition(points,chunk_size)){
            smeans.AddChunk(chunk);
        }

        smeans.InputIsDone();
        smeans.WaitTillDone();

        Collection<Point> intermediate_centers=smeans.GetIntermediateCenters();

        if(verbose)
            System.out.printf("First pass finished in %s.\n",watch.toString());

        KMeansPlusPlusClusterer<Point> final_kmpp=new KMeansPlusPlusClusterer<Point>(random);
        ArrayList<Point> final_centers=new ArrayList<Point>();

        if(verbose)
            System.out.println("Doing k-means++ on intermediate centers....  \tsize:" + intermediate_centers.size() + "\titerations:" + ((int) Math.log(intermediate_centers.size())));
        List<Cluster<Point>> final_clusters=null;
        final_clusters=final_kmpp.cluster(intermediate_centers, k, (int) Math.log(intermediate_centers.size()));
        for(Cluster<Point> cluster:final_clusters)
            final_centers.add(cluster.getCenter());
        return final_centers;
   }



}
