package org.tmu;

import org.apache.commons.math3.stat.clustering.Cluster;
import org.apache.commons.math3.stat.clustering.KMeansPlusPlusClusterer;
import org.tmu.clustering.FastKMeansPlusPlusClusterer;
import org.tmu.clustering.SimpleKMeansClusterer;
import org.tmu.clustering.StreamClusterer;
import org.tmu.util.PointGeometry;
import org.tmu.util.Point;
import org.tmu.util.RandomPointGenerator;


import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Saeed
 * Date: 10/9/12
 * Time: 5:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class Main {
    public static void main(String[] args) throws IOException {

        List<Point> points= new ArrayList<Point>();
        int count=333333;
        points.addAll(RandomPointGenerator.GenerateSphere(new Point(new double[]{10,10,10}),count,new Random(123)));
        points.addAll(RandomPointGenerator.GenerateSphere(new Point(new double[]{0,0,0}),count,new Random(123)));
        points.addAll(RandomPointGenerator.GenerateSphere(new Point(new double[]{20,20,20}),count,new Random(123)));


        Point center= PointGeometry.ComputeCenter(points);
        /*
        FileWriter writer=new FileWriter("C:\\a.csv");
        for(Point p:points)
            writer.write(p.toString().substring(1,p.toString().length()-1)+"\n");
        writer.close();
        */

        //SimpleKMeansClusterer<Point> kmeans=new SimpleKMeansClusterer<Point>(new Random(123));

        //KMeansPlusPlusClusterer<Point> kmeans=new KMeansPlusPlusClusterer<Point>(new Random(123));
        StreamClusterer<Point> smeans=new StreamClusterer<Point>(3,new Random(123));

        for(int i=0;i<points.size()-10000;i=i+10000)
            smeans.addChunk(points.subList(i,i+10000));
        smeans.addChunk(new ArrayList<Point>());

        System.out.println("Waiting....");
        smeans.getIntermediateCenters();

        long t0=System.currentTimeMillis();
        //List<Cluster<Point>> res=kmeans.cluster(points,3,7);
        long t1=System.currentTimeMillis()-t0;
        System.out.println(t1);
        //System.in.read();



    }
}
