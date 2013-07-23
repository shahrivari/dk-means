package org.tmu;

import com.google.common.base.Stopwatch;
import org.tmu.clustering.DKMeansClusterer;
import org.tmu.util.*;


import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Saeed
 * Date: 10/9/12
 * Time: 5:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        String file_name=args[0];
        Stopwatch watch=new Stopwatch().start();

        CSVReader.TimeSequentialFileRead("Z:\\cut.txt");

        System.out.println(watch);
        System.exit(0);





        String bin_dir="z:/alaki";
        //String bin_dir=file_name;
        //OnDiskClusteringTester.generateBinaryDatasets(bin_dir);

        //OnDiskClusteringTester.convertBinaryToCSV("z:/alaki","x:/alaki");

        for(File f:new File(bin_dir).listFiles());
            //OnDiskClusteringTester.doTest(f.getPath(),Integer.parseInt(f.getName().substring(9,f.getName().lastIndexOf("_"))),Runtime.getRuntime().availableProcessors());


        for(int i=Integer.parseInt(args[0]);i>0;i--){
            Runtime.getRuntime().gc();
            File f=new File(bin_dir+"/BIN_NORM_20_1000M.bin");
            OnDiskClusteringTester.doTest(f.getPath(),Integer.parseInt(f.getName().substring(9,f.getName().lastIndexOf("_"))),i);
            System.exit(0);
        }

        System.exit(0);


//        BinaryFormatWriter writer=new BinaryFormatWriter("z:\\binary.dat");
//        RandomPointGenerator.GenerateSphereToFile(writer,new Point(new double[]{0,0,0,0,0}),2*25*1024*1024,new Random());
//        BinaryFormatReader.TimeSequentialFileRead("z:\\binary.dat");
//        System.out.println(watch);
//        System.exit(0);

//        System.out.println("Sequental Read:"+CSVReader.TimeSequentialFileRead(file_name));
//
//        for(int s:new int[]{2,7,13,20}){
//            System.out.println("##################### Clusters" + s);
//            RandomPointGenerator.GenerateDisjointClustersToFile(new FileWriter("z:\\NORM-"+Integer.toString(s)),s,1000*1000,5,new Random());
//        }

        //RandomPointGenerator.GenerateDisjointClustersToFile(new FileWriter("z:\\big.csv"),10,100*1000*1000,5,new Random());
        //System.exit(0);
        watch.reset().start();
        DKMeansClusterer dkMeansClusterer1=new DKMeansClusterer();
        Collection<Point> res=dkMeansClusterer1.clusterFile("z:\\big.bin", 10);
        System.out.println(watch);
        for(Point p:res)
            System.out.println(p);
        System.exit(0);

        for(int s:new int[]{2,7,13,20}){
            System.out.println("##################### NORM-" + s);
            InMemClusteringTester.doTestFromFile("z:\\NORM-"+Integer.toString(s), s, 20, 9);
        }

        System.exit(0);

        for(String s:new String[]{"Z:\\kddcup.csv","Z:\\skin.csv","Z:\\household.csv"}){
            System.out.println("##################### " + s);
            InMemClusteringTester.doTestFromFile(s, 5, 20, 5);
        }
        System.exit(0);

        watch.reset().start();
        DKMeansClusterer dkMeansClusterer=new DKMeansClusterer();
        Collection<Point> res1=dkMeansClusterer.clusterCSVFile("Z:\\skin.csv", 23, 4, 1024, 20);
        System.out.println(watch);
        for(Point p:res1)
            System.out.println(p);
        return;
//        TestCSV test=new TestCSV();
//        test.ReadLines(file_name,1000*1000*10);
//        test.SerialParse();
//        test.ParallelParse();
//
//        System.exit(0);
//        FileWriter writer=new FileWriter(file_name);
//        Point p=new Point(32);
//        for(int i=0;i<32;i++)
//            p.setElement(i,0);
//        RandomPointGenerator.GenerateDisjointClustersToFile(writer,p,5,1000*1000*10,new Random(1234));
//        writer.close();
//        System.out.println(watch.elapsedMillis());
//        watch.reset().start();
//
//        CSVReader reader= new CSVReader(file_name);
//        List<Point> points=new ArrayList<Point>();
//
//        int count=1000*1000*10;
//
//        Point p1=null;
//        for(int i=0;i<count;i++)
//        {
//            p1=reader.ReadNextPoint();
//            if(p1!=null)
//                points.add(p1);
//        }
//        System.out.println(points.size() +" took  "+ watch.elapsedMillis());
//
//
//        watch.reset().start();
//        points.clear();
//        reader= new CSVReader(file_name);
//
//        System.in.read();
//        System.exit(0);

//        Collection<Point> p;
//        //while ((p=reader.ReadSomePointInParallel(500*1000,4))!=null&&points.size()<1000*500)
//        int chunk_size=500*1000;
////        for(int i=0;i<count/chunk_size;i++){
////            p=reader.ReadSomePointInParallel(chunk_size,4);
////            points.addAll(p);
////        }
//        System.out.println(points.size() +" took  "+ watch.elapsedMillis());
//
        //System.out.println(watch.elapsedMillis());
        //System.exit(0);

//        int count=1000*1000*10;
//        int num_clusters=5;
//        int max_iter=7;
//        Stopwatch watch=new Stopwatch().start();
//        List<Point> points=null;
//
//        for(count=1000*1000*40;count<=50*1000*1000;count=count*2){
//            points= RandomPointGenerator.GenerateDisjointClusters(new Point(new double[]{10,10,10}),num_clusters,count,new Random(123));
//            System.out.println("Random Generation took: "+watch);
//            for(int y=0;y<1;y++)
//        {
//           watch.reset().start();
//
//            KMeansPlusPlusClusterer<Point> kmpp=new KMeansPlusPlusClusterer<Point>(new Random(123));
//            kmpp.clusterBinaryFile(points,num_clusters,max_iter);
//            System.out.println("Serial kmeans++ took: "+watch);
//            System.out.println("Points: "+points.size());
//            points=null;
//            Runtime.getRuntime().gc();
//            watch.reset().start();
//
//            int num_points=0;
//            ImprovedStreamClusterer<Point> smeans=new ImprovedStreamClusterer<Point>(num_clusters);
//            //StreamClusterer<Point> smeans=new StreamClusterer<Point>(num_clusters,new Random());
//            smeans.Start();
//
//            int chunk_size=10000;
//            points= RandomPointGenerator.GenerateDisjointClusters(new Point(new double[]{10,10,10}),num_clusters,chunk_size,new Random(123));
//            for(int i=0;i<count/chunk_size;i++)
//            {
//                smeans.AddChunk(points);
//                num_points+=points.size();
//            }
//            smeans.InputIsDone();
//            //smeans.AddChunk(new ArrayList<Point>());
//
//            smeans.WaitTillDone();
//            //smeans.getIntermediateCenters();
//            //smeans.GetIntermediateCenters();
//            System.out.println("Parallel kmeans++ took: "+watch);
//            System.out.println("Points: "+num_points);
//            System.out.println("==========================================");
//        }
//        }
    }
}
