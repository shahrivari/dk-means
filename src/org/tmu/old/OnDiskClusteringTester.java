package org.tmu.old;

import com.google.common.base.Stopwatch;
import org.tmu.old.DKMeansClusterer;
import org.tmu.util.BinaryFormatWriter;
import org.tmu.util.CSVReader;
import org.tmu.util.RandomPointGenerator;

import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Saeed
 * Date: 11/12/12
 * Time: 12:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class OnDiskClusteringTester {

    public static void generateBinaryDatasets(String dataset_dir) throws IOException {
        System.out.println("Clearing Directory...");
        for(File f:new File(dataset_dir).listFiles())
            f.delete();

        Stopwatch watch=new Stopwatch().start();
        for(int k:new int[]{2,3,5,8,13,21,34,55}){
            for(int count:new int[]{50*1000*1000}){
                watch.reset().start();
                System.out.println("Generating " + count / 1000 / 1000 + "M item dataset with " + k + " centers...");
                RandomPointGenerator.GenerateDisjointClustersToFile(new BinaryFormatWriter(dataset_dir+"/BIN_NORM_"+Integer.toString(k)+"_"+Integer.toString(count / 1000 / 1000)+"M.bin")
                ,k,count,5,new Random());
                System.out.println("Took: "+watch.stop());
            }
        }



        for(int k:new int[]{20}){
            for(int count:new int[]{100*1000*1000,200*1000*1000,300*1000*1000,400*1000*1000,500*1000*1000,1000*1000*1000}){
                watch.reset().start();
                System.out.println("Generating " + count / 1000 / 1000 + "M item dataset with " + k + " centers...");
                RandomPointGenerator.GenerateDisjointClustersToFile(new BinaryFormatWriter(dataset_dir+"/BIN_NORM_"+Integer.toString(k)+"_"+Integer.toString(count / 1000 / 1000)+"M.bin")
                        ,k,count,5,new Random());
                System.out.println("Took: "+watch.stop());
            }
        }

        System.out.println("Synthesized datasets created!");
    }

    public static void convertBinaryToCSV(String src_dir,String dest_dir) throws IOException, InterruptedException {
        File src=new File(src_dir);
        Stopwatch watch=new Stopwatch();
        for(File file_name:src.listFiles()){
            String dst=dest_dir+"/"+file_name.getName();
            if(dst.contains("."))
                dst=dst.substring(0,dst.lastIndexOf('.'));
            dst=dst+".csv";
            System.out.println("Converting " + file_name.getPath() + " ....");
            watch.reset().start();
            CSVReader.convertBinaryToCSV(file_name.getPath(), dst);
            System.out.println("Took: "+watch.stop());
        }
    }

    public static void doTest(String file_name, int cluster_count, int thread_count) throws IOException, InterruptedException {
        Stopwatch watch=new Stopwatch().start();
        DKMeansClusterer.clusterFile(file_name,cluster_count,thread_count);
        System.out.println("Took: " + watch.stop());
    }
}
