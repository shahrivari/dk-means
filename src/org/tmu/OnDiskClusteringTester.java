package org.tmu;

import com.google.common.base.Stopwatch;
import org.tmu.util.BinaryFormatWriter;
import org.tmu.util.RandomPointGenerator;

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
        Stopwatch watch=new Stopwatch().start();
        for(int k:new int[]{2,3,5,8,21,34}){
            for(int count:new int[]{10*1000*1000}){
                watch.reset().start();
                System.out.println("Generating " + count / 1000 / 1000 + "M item dataset with " + k + " centers...");
                RandomPointGenerator.GenerateDisjointClustersToFile(new BinaryFormatWriter(dataset_dir+"/BIN_NORM_"+Integer.toString(k)+"_"+Integer.toString(count / 1000 / 1000)+"M.bin")
                ,k,count,5,new Random());
                System.out.println("Took: "+watch.stop());
            }
        }

        for(int k:new int[]{13}){
            for(int count:new int[]{1000*1000,10*1000*1000,100*1000*1000,200*1000*1000}){
                watch.reset().start();
                System.out.println("Generating " + count / 1000 / 1000 + "M item dataset with " + k + " centers...");
                RandomPointGenerator.GenerateDisjointClustersToFile(new BinaryFormatWriter(dataset_dir+"/BIN_NORM_"+Integer.toString(k)+"_"+Integer.toString(count / 1000 / 1000)+"M.bin")
                        ,k,count,5,new Random());
                System.out.println("Took: "+watch.stop());
            }
        }

        System.out.println("Synthesized datasets created!");
    }
}
