package org.tmu;

import com.google.common.base.Stopwatch;
import org.apache.commons.math3.stat.clustering.Cluster;
import org.apache.commons.math3.stat.clustering.KMeansPlusPlusClusterer;
import org.tmu.clustering.StreamClusterer;
import org.tmu.util.CSVReader;
import org.tmu.util.PointGeometry;
import org.tmu.util.Point;
import org.tmu.util.RandomPointGenerator;


import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Saeed
 * Date: 10/9/12
 * Time: 5:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Stopwatch watch=new Stopwatch().start();
        FileWriter writer=new FileWriter("c:\\akbar.csv");
        RandomPointGenerator.GenerateDisjointClustersToFile(writer,new Point(new double[]{0,0,0}),5,1000*100,new Random(1234));
        writer.close();
        System.out.println(watch.elapsedMillis());
        watch.reset().start();
        CSVReader reader= new CSVReader("c:\\akbar.csv");
        List<Point> points=new ArrayList<Point>();

        Point p1=null;
        while ((p1=reader.ReadNextPoint())!=null)
            points.add(p1);
        System.out.println(watch.elapsedMillis());

        watch.reset().start();
        reader= new CSVReader("c:\\akbar.csv");


        Collection<Point> p;
        while ((p=reader.ReadSomePointInParallel(1024,4))!=null)
            points.addAll(p);

        System.out.println(watch.elapsedMillis());
        System.exit(0);

        //int count=1000000;
        //List<Point> points= RandomPointGenerator.GenerateDisjointClusters(new Point(new double[]{10,10,10}),3,count,new Random(123));
        //Point center= PointGeometry.ComputeCenter(points);

        //SimpleKMeansClusterer<Point> kmeans=new SimpleKMeansClusterer<Point>(new Random(123));

        int num_points=0;
        StreamClusterer<Point> smeans=new StreamClusterer<Point>(3,new Random(123));

        System.out.println("Waiting....");

        int chunk_size=1000;
        for(int i=0;i<points.size()-chunk_size;i=i+chunk_size)
        {
            List<Point> chunk=points.subList(i, i + chunk_size);
            smeans.AddChunk(chunk);
            num_points+=chunk.size();
        }
        smeans.AddChunk(new ArrayList<Point>());

        long t0=System.currentTimeMillis();
        //List<Cluster<Point>> res=kmeans.cluster(points,3,7);
        System.out.println("Num clusts: " + smeans.getIntermediateCenters().size());

        long t1=System.currentTimeMillis()-t0;
        System.out.println(t1+"     "+num_points);

        //System.in.read();

        KMeansPlusPlusClusterer<Point> kmeans=new KMeansPlusPlusClusterer<Point>(new Random(123));

        t0=System.currentTimeMillis();
        List<Cluster<Point>> res=kmeans.cluster(points,3,7);
        num_points=points.size();
        t1=System.currentTimeMillis()-t0;
        System.out.println(t1+"     "+num_points);

    }
}
