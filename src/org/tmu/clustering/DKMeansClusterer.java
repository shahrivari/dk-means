package org.tmu.clustering;

import com.google.common.base.Stopwatch;
import org.apache.commons.math3.stat.clustering.Cluster;
import org.apache.commons.math3.stat.clustering.KMeansPlusPlusClusterer;
import org.tmu.util.BinaryFormatReader;
import org.tmu.util.CSVReader;
import org.tmu.util.DataSetInfo;
import org.tmu.util.Point;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Saeed
 * Date: 10/20/12
 * Time: 8:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class DKMeansClusterer  {

    public static Collection<Point> clusterBinaryFile(String file_name, int cluster_count, int thread_count, int chunk_size, int chunk_iteration_count) throws IOException, InterruptedException {
        BinaryFormatReader reader=new BinaryFormatReader(file_name);
        ImprovedStreamClusterer<Point> smeans=new ImprovedStreamClusterer<Point>(cluster_count,chunk_iteration_count,thread_count);
        smeans.Start();

        Collection<Point> points;
        do{
            points=reader.readSomePoint(chunk_size);
            if(points==null)
                break;
            smeans.AddChunk(points);
        }while (points.size()>0);

        smeans.InputIsDone();

        smeans.WaitTillDone();

        List<Point> centers=new ArrayList<Point>(smeans.GetIntermediateCenters());

        Stopwatch watch=new Stopwatch().start();
        KMeansPlusPlusClusterer<Point> final_kmpp=new KMeansPlusPlusClusterer<Point>(new Random());
        ArrayList<Point> final_centers=new ArrayList<Point>();
        System.out.print("Doing k-means++ on intermediate centers....  \tsize:" + centers.size() + "\titerations:" + ((int)Math.log(centers.size())));
        List<Cluster<Point>> final_clusters=null;
        if(centers.size()<1000*1000)
            final_clusters=final_kmpp.cluster(centers, cluster_count, (int) Math.log(centers.size()));
        else
            final_clusters=DKMeansClusterer.cluster(centers, cluster_count);// .cluster(centers, cluster_count, (int) Math.log(centers.size()));

        for(Cluster<Point> cluster:final_clusters)
            final_centers.add(cluster.getCenter());
        System.out.println("\tTook: "+watch.stop());
        return final_centers;
    }

    public static Collection<Point> clusterCSVFile(String file_name, int cluster_count, int thread_count, int chunk_size, int chunk_iteration_count) throws IOException, InterruptedException {
        CSVReader csvReader=new CSVReader(file_name);
        ImprovedStreamClusterer<Point> smeans=new ImprovedStreamClusterer<Point>(cluster_count,chunk_iteration_count);
        smeans.Start();

        Collection<Point> points;
        do{
            points=csvReader.ReadSomePoint(chunk_size);
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

    public static  List<Cluster<Point>> cluster(List<Point> points, int cluster_count, int thread_count, int chunk_size, int chunk_iteration_count) throws IOException, InterruptedException {
        ImprovedStreamClusterer<Point> smeans=new ImprovedStreamClusterer<Point>(cluster_count,chunk_iteration_count);
        smeans.Start();

        for(int i=0;i<points.size();i+=chunk_size){
            if(i+chunk_size<=points.size())
                smeans.AddChunk(points.subList(i,i+chunk_size));
        }
        if(points.size()%chunk_size!=0)
            smeans.AddChunk(points.subList(points.size()/chunk_size*chunk_size,points.size()));

        smeans.InputIsDone();

        smeans.WaitTillDone();

        Collection<Point> centers=smeans.GetIntermediateCenters();

        KMeansPlusPlusClusterer<Point> final_kmpp=new KMeansPlusPlusClusterer<Point>(new Random());
        ArrayList<Point> final_centers=new ArrayList<Point>();
        List<Cluster<Point>> final_clusters=final_kmpp.cluster(centers, cluster_count, 10);
        for(Cluster<Point> cluster:final_clusters)
            final_centers.add(cluster.getCenter());
        List<Cluster<Point>> clusterList=new ArrayList<Cluster<Point>>();

        for(int i=0;i<final_centers.size();i++)
            clusterList.add(new Cluster<Point>(final_centers.get(i)));

        for(Point p:points){
            Point nc=p.findNearest(final_centers);
            for(int i=0;i<clusterList.size();i++)
                if(final_centers.get(i).distanceFrom(nc)==0)
                    clusterList.get(i).addPoint(p);
        }

        return clusterList;
    }


    public static Collection<Point> clusterFile(String file_name, int cluster_count, int thread_count) throws IOException, InterruptedException {
        if(file_name.endsWith(".csv")){
            int point_count=DataSetInfo.estimatePointCountCSV(file_name);
            int point_size=DataSetInfo.pointSizeCSV(file_name);
            int chunkSize=DataSetInfo.estimateChunkSize(point_count,point_size);
            System.out.println("Clustering file:"+file_name);
            System.out.println("Setting:  \testimated items:"+point_count+"\tchunk size:"+chunkSize+	"\titem size:"+point_size+"\tcluster count: "+cluster_count+"\tthread count: "+thread_count);
            return clusterCSVFile(file_name, cluster_count, thread_count, chunkSize, (int)Math.log(chunkSize));
        }
        else{
            int point_count=DataSetInfo.estimatePointCountBinary(file_name);
            int point_size=DataSetInfo.pointSizeBinary(file_name);
            int chunkSize=DataSetInfo.estimateChunkSize(point_count,point_size);
            System.out.println("Clustering file:"+file_name);
            System.out.println("Setting:  \testimated items:"+point_count+"\tchunk size:"+chunkSize+	"\titem size:"+point_size+"\tcluster count: "+cluster_count+"\tthread count: "+thread_count);
            return clusterBinaryFile(file_name, cluster_count, thread_count, chunkSize, (int)Math.log(chunkSize));
        }
    }

    public static Collection<Point> clusterFile(String file_name, int cluster_count) throws IOException, InterruptedException {
        return clusterFile(file_name,cluster_count,Runtime.getRuntime().availableProcessors());
    }

    public static  List<Cluster<Point>> cluster(List<Point> points, int cluster_count) throws IOException, InterruptedException {
        int chunkSize= DataSetInfo.estimateChunkSize(points);
        int innerIter=DataSetInfo.estimateIteration(points.size());
        return cluster(points,cluster_count,Runtime.getRuntime().availableProcessors(),chunkSize,innerIter);
    }

}
