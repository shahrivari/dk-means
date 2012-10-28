package org.tmu.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Saeed
 * Date: 10/9/12
 * Time: 8:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class RandomPointGenerator {

    static public List<Point> GenerateSphere(Point center, int point_count, Random random)
    {
        if(random==null)random=new Random();
        List<Point> result=new ArrayList<Point>(point_count);

        for(int i=0;i<point_count;i++)
        {
            double[] elements=new double[center.size()];
            for(int j=0;j<center.size();j++)
                elements[j]=random.nextDouble()+center.getElement(j);
            result.add(new Point(elements));
        }

        return result;
    }

    static public List<Point> GenerateDisjointClusters(Point first_center, int cluster_count,int point_count, Random random)
    {
        List<Point> points= new ArrayList<Point>();
        int count=point_count/cluster_count;
        for(int j=0;j<cluster_count;j++){
            for(int i=0;i<first_center.size();i++)
                first_center.setElement(i,first_center.getElement(i)+10*j);
            points.addAll(RandomPointGenerator.GenerateSphere(first_center,count,random));
        }
        return points;
    }

    static public void GenerateSphereToFile(FileWriter writer,Point center, int point_count, Random random) throws IOException {
        if(random==null)random=new Random();
        StringBuilder builder=new StringBuilder();
        for(int i=0;i<point_count;i++)
        {
            double[] elements=new double[center.size()];
            for(int j=0;j<center.size();j++)
                elements[j]=random.nextDouble()+center.getElement(j);
            String s=new Point(elements).toString();
            builder.append(s.substring(1, s.length() - 1)).append("\n");
            if(builder.length()>64*1024)
            {
                writer.write(builder.toString());
                builder.setLength(0);
            }
        }
        if(builder.length()>0)
            writer.write(builder.toString());
        writer.flush();
    }

    static public void GenerateSphereToFile(BinaryFormatWriter writer,Point center, int point_count, Random random) throws IOException {
        if(random==null)random=new Random();
        for(int i=0;i<point_count;i++)
        {
            double[] elements=new double[center.size()];
            for(int j=0;j<center.size();j++)
                elements[j]=random.nextDouble()+center.getElement(j);
            writer.writePoint(elements);
        }
        writer.flush();
    }

    static public void GenerateDisjointClustersToFile(FileWriter writer, Point first_center,int cluster_count, int point_count,Random random)
            throws IOException {
        int count=point_count/cluster_count;
        for(int j=0;j<cluster_count;j++)
        {
            for(int i=0;i<first_center.size();i++)
                first_center.setElement(i,first_center.getElement(i)+10*j);
            RandomPointGenerator.GenerateSphereToFile(writer, first_center, count, random);
        }
        writer.flush();
    }

    static public void GenerateDisjointClustersToFile(BinaryFormatWriter writer, Point first_center,int cluster_count, int point_count,Random random)
            throws IOException {
        int count=point_count/cluster_count;
        for(int j=0;j<cluster_count;j++)
        {
            for(int i=0;i<first_center.size();i++)
                first_center.setElement(i,first_center.getElement(i)+10*j);
            RandomPointGenerator.GenerateSphereToFile(writer, first_center, count, random);
        }
        writer.flush();
    }

}
