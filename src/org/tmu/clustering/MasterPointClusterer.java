package org.tmu.clustering;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import org.apache.commons.math3.stat.clustering.Cluster;
import org.apache.commons.math3.stat.clustering.KMeansPlusPlusClusterer;
import org.tmu.util.BinaryFormatReader;
import org.tmu.util.CSVReader;
import org.tmu.util.DataSetInfo;
import org.tmu.util.Point;

import java.io.IOException;
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

    public static List<Point> KMeans(final Collection<Point> points, final int k,final int maxIterations){
        ArrayList<Point> centers=new ArrayList<Point>();
        SimpleKMeansClusterer<Point> kmeans=new SimpleKMeansClusterer<Point>(random);
        List<Cluster<Point>> clusters= kmeans.cluster(points,k,maxIterations);
        for(Cluster<Point> cluster:clusters)
            centers.add(cluster.getCenter());
        return centers;
    }

    public static List<Point> KMeansPP(final Collection<Point> points, final int k,final int maxIterations){
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

        for(List<Point> chunk:Lists.partition(points,chunk_size))
            intermediate_centers.addAll(KMeansPP(chunk,k,DataSetInfo.estimateIteration(chunk)));

        if(verbose){
            System.out.printf("First pass finished in %s.\n",watch.toString());
            System.out.println("Doing k-means++ on intermediate centers....  \tsize:" + intermediate_centers.size() + "\titerations:" + DataSetInfo.estimateIteration(intermediate_centers));
        }

        return KMeansPP(intermediate_centers,k,DataSetInfo.estimateChunkSize(intermediate_centers));
    }

    public static List<Point> InMemParallelDKMeans(final List<Point> points, final int k,final int chunk_size) throws InterruptedException {
        Stopwatch watch=new Stopwatch().start();

        ImprovedStreamClusterer<Point> smeans=new ImprovedStreamClusterer<Point>(k,DataSetInfo.estimateIteration(chunk_size),Runtime.getRuntime().availableProcessors());
        smeans.Start();

        for(List<Point> chunk:Lists.partition(points,chunk_size)){
            smeans.AddChunk(chunk);
        }

        smeans.InputIsDone();
        smeans.WaitTillDone();

        Collection<Point> intermediate_centers=smeans.GetIntermediateCenters();

        if(verbose){
            System.out.printf("First pass finished in %s.\n",watch.toString());
            System.out.println("Doing k-means++ on intermediate centers....  \tsize:" + intermediate_centers.size() + "\titerations:" + DataSetInfo.estimateIteration(intermediate_centers));
        }

        return KMeansPP(intermediate_centers,k,DataSetInfo.estimateChunkSize(intermediate_centers));
   }

    public static int verbose_step=1000*1000;

    public static List<Point> DKMeansCSVFile(final String file_name, final int k, int chunk_size, int chunk_iteration_count, int thread_count) throws IOException, InterruptedException {
        Stopwatch watch=new Stopwatch().start();
        CSVReader csvReader=new CSVReader(file_name);
        ImprovedStreamClusterer<Point> smeans=new ImprovedStreamClusterer<Point>(k,chunk_iteration_count,thread_count);
        smeans.Start();

        List<Point> points;
        int last_delta_read=0;
        do{
            points=csvReader.ReadSomePoint(chunk_size);
            if(points==null)
                break;
            last_delta_read+=points.size();
            if(last_delta_read>=verbose_step){
                last_delta_read=0;
                System.out.printf("Total read points: %,d\n",csvReader.getReadCount());
            }

            smeans.AddChunk(points);
        }while (points.size()>0);

        smeans.InputIsDone();
        smeans.WaitTillDone();
        Collection<Point> intermediate_centers=smeans.GetIntermediateCenters();

        if(verbose){
            System.out.printf("First pass finished in %s.\n",watch.toString());
            System.out.println("Doing k-means++ on intermediate centers....  \tsize:" + intermediate_centers.size() + "\titerations:" + DataSetInfo.estimateIteration(intermediate_centers));
        }

        if(intermediate_centers.size()<1000*1000)
            return KMeansPP(intermediate_centers,k,DataSetInfo.estimateIteration(intermediate_centers));
        else
            return InMemParallelDKMeans(new ArrayList<Point>(intermediate_centers),k,DataSetInfo.estimateChunkSize(intermediate_centers));
    }

    public static Collection<Point> DKMeansBinaryFile(String file_name, int k, int chunk_size, int chunk_iteration_count, int thread_count) throws IOException, InterruptedException {
        Stopwatch watch=new Stopwatch().start();
        BinaryFormatReader reader=new BinaryFormatReader(file_name);
        ImprovedStreamClusterer<Point> smeans=new ImprovedStreamClusterer<Point>(k,chunk_iteration_count,thread_count);
        smeans.Start();

        Collection<Point> points;
        int last_delta_read=0;
        long read_count=0;
        do{
            points=reader.readSomePoint(chunk_size);
            if(points==null)
                break;
            last_delta_read+=points.size();
            read_count+=points.size();
            if(last_delta_read>=verbose_step){
                last_delta_read=0;
                System.out.printf("Total read points: %,d\n",read_count);
            }
            smeans.AddChunk(points);
        }while (points.size()>0);

        smeans.InputIsDone();
        smeans.WaitTillDone();
        Collection<Point> intermediate_centers=smeans.GetIntermediateCenters();

        if(verbose){
            System.out.printf("First pass finished in %s.\n",watch.toString());
            System.out.println("Doing k-means++ on intermediate centers....  \tsize:" + intermediate_centers.size() + "\titerations:" + DataSetInfo.estimateIteration(intermediate_centers));
        }

        if(intermediate_centers.size()<1000*1000)
            return KMeansPP(intermediate_centers,k,DataSetInfo.estimateIteration(intermediate_centers));
        else
            return InMemParallelDKMeans(new ArrayList<Point>(intermediate_centers),k,DataSetInfo.estimateChunkSize(intermediate_centers));
    }




}
