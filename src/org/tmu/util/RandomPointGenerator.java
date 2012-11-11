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

    static private List<Point> generateCenters(int center_count, int point_size, Random random){
        List<Point> centers=new ArrayList<Point>();
        for(int j=0;j<center_count;j++)
        {
            Point center=new Point(point_size);
            for(int i=0;i<point_size;i++)
                center.setElement(i,random.nextInt(20));
            centers.add(center);
        }
        return centers;
    }

    static public List<Point> GenerateDisjointClusters(int cluster_count,int point_count, int point_size, Random random)
    {
        ArrayList<Point> points=new ArrayList<Point>(point_count);
        List<Point> centers=generateCenters(cluster_count,point_size,random);
        for(int i=0;i<point_count;i++)
        {
            Point center=centers.get(random.nextInt(centers.size()));
            Point p=Point.generateRandom(center,random);
            points.add(p);
        }
        return points;
    }

    static public void GenerateDisjointClustersToFile(BinaryFormatWriter writer, int cluster_count, int point_count, int point_size,Random random)
            throws IOException {
        List<Point> centers=generateCenters(cluster_count,point_size,random);
        for(int i=0;i<point_count;i++)
        {
            Point center=centers.get(random.nextInt(centers.size()));
            Point p=Point.generateRandom(center,random);
            writer.writePoint(p);
        }
        writer.flush();
    }


    static public void GenerateDisjointClustersToFile(FileWriter writer, int cluster_count, int point_count, int point_size,Random random)
            throws IOException {
        List<Point> centers=generateCenters(cluster_count,point_size,random);

        StringBuilder builder=new StringBuilder();
        for (Point center:centers){
            String s=center.toString();
            builder.append(s.substring(1, s.length() - 1)).append("\n");
        }


        for(int i=0;i<point_count;i++)
        {
            Point center=centers.get(random.nextInt(centers.size()));
            String s=Point.generateRandom(center,random).toString();
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

}
