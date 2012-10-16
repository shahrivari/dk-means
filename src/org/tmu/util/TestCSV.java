package org.tmu.util;

import com.google.common.base.Stopwatch;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Saeed
 * Date: 10/16/12
 * Time: 3:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestCSV {
    final List<String> lines=new ArrayList<String>(1000*10);



    public void ReadLines(String file_name,int count) throws IOException {
        Stopwatch watch=new Stopwatch().start();
        FileReader fileReader =new FileReader(file_name);
        BufferedReader reader=new BufferedReader(fileReader);
        String line="";
        while (lines.size()<count)
        {
            do
            {
                line= reader.readLine();
                if(line==null) return ;
                line=line.trim();
            }while(line.isEmpty());
            lines.add(line);
        }
        System.out.println("read "+lines.size()+" lines in "+watch);
    }

    public Collection<Point> SerialParse()
    {
        Stopwatch watch=new Stopwatch().start();
        List<Point>  points=new ArrayList<Point>(1000*10);
        for(String line:lines)
        {
            String[] tokens=line.split(",");
            double [] point=new double[tokens.length];
            for(int i=0;i<point.length;i++)
                point[i]=Double.parseDouble(tokens[i]);
            points.add(new Point(point));
        }
        System.out.println("Serial: read "+points.size()+" point in "+watch);
        return points;
    }

    public Collection<Point> ParallelParse() throws InterruptedException {
        Stopwatch watch=new Stopwatch().start();
        int threadCount=Runtime.getRuntime().availableProcessors();
        final ArrayList<Point[]> results=new ArrayList<Point[]>();
        ArrayList<Thread> threads=new ArrayList<Thread>();

        final int chunk_size=lines.size()/threadCount;
        for(int i = 0; i<threadCount; i++)
        {
            final int low=i *chunk_size;
            final int high=(i +1)*chunk_size;
            Runnable run=new Runnable() {
                @Override
                public void run() {
                    Point[] internal_list=new Point[high-low];
                    for(int x=low ;x<high; x++)
                    {
                        String line=lines.get(x);
                        String[] tokens=line.split(",");
                        double [] point=new double[tokens.length];
                        for(int j=0;j<point.length;j++)
                            point[j]=Double.parseDouble(tokens[j]);
                        internal_list[x-low]=new Point(point);
                    }
                    results.add(internal_list);
                }
            };
            //System.out.println(low+","+high);
            threads.add(new Thread(run));
        }

        for(Thread t: threads)
            t.start();

        for(Thread t: threads)
            t.join();

        int final_size=0;
        for(Point[] list:results)
            final_size+=list.length;

        ArrayList<Point> points=new ArrayList<Point>(final_size);
        for(Point[] list:results)
            for(Point p:list)
                points.add(p);

        System.out.println("Parallel: read "+points.size()+" point in "+watch);
        return points;
    }



}
