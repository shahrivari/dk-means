package org.tmu.clustering;

import org.apache.commons.math3.stat.clustering.Cluster;
import org.apache.commons.math3.stat.clustering.Clusterable;
import org.apache.commons.math3.stat.clustering.KMeansPlusPlusClusterer;
import org.tmu.util.CSVReader;
import org.tmu.util.Point;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Saeed
 * Date: 10/20/12
 * Time: 8:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class DKMeansClusterer  {

    public static Collection<Point> cluster(String file_name,int cluster_count, int thread_count, int chunk_size, int chunk_iteration_count) throws IOException, InterruptedException {
        CSVReader csvReader=new CSVReader(file_name);
        ImprovedStreamClusterer<Point> smeans=new ImprovedStreamClusterer<Point>(cluster_count,chunk_iteration_count);
        smeans.Start();

        Collection<Point> points;
        do{
            points=csvReader.ReadSomePoint(chunk_size);
            //points=csvReader.ReadSomePointInParallel(1024*100,2);
            if(points==null)
                break;
            smeans.AddChunk(points);
        }while (points.size()>0);

        smeans.InputIsDone();

        smeans.WaitTillDone();

        Collection<Point> centers=smeans.GetIntermediateCenters();

        KMeansPlusPlusClusterer<Point> final_kmpp=new KMeansPlusPlusClusterer<Point>(new Random());
        ArrayList<Point> final_centers=new ArrayList<Point>();
        List<Cluster<Point>> final_clusters=final_kmpp.cluster(centers, cluster_count, (int) Math.log(centers.size()));
        for(Cluster<Point> cluster:final_clusters)
            final_centers.add(cluster.getCenter());
        return final_centers;
    }

    public static Collection<Point> cluster(String file_name,int cluster_count) throws IOException, InterruptedException {
        return cluster(file_name,cluster_count,Runtime.getRuntime().availableProcessors(),1024,10);
    }


}
