package org.tmu.util;

import com.google.common.base.Stopwatch;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Saeed
 * Date: 10/13/12
 * Time: 11:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class CSVReader {
    BufferedReader reader;

    public CSVReader(String file_name) throws FileNotFoundException {
        FileReader fileReader =new FileReader(file_name);
        reader=new BufferedReader(fileReader);
    }

    private String readNextNonEmptyLine() throws IOException {
        String line="";
        do
        {
            line= reader.readLine();
            if(line==null) return null;
            line=line.trim();
        }while(line.isEmpty());
        return line;
    }

    public Point ReadNextPoint() throws IOException {
        String line= readNextNonEmptyLine();
        if(line==null)return null;
        String[] tokens=line.split(",");
        double [] point=new double[tokens.length];
        for(int i=0;i<point.length;i++)
            point[i]=Double.parseDouble(tokens[i]);
        return new Point(point);
    }

    public Collection<Point> ReadSomePointInParallel(int count, int threads_count)throws IOException
    {
        int chunk_size=1024*50;
        //List<List<String>> chunks=new ArrayList<List<String>>();
        //final ConcurrentLinkedQueue<Point> queue=new ConcurrentLinkedQueue<Point>();
        ParallelProducerConsumer<List<String>,Point> producerConsumer=new ParallelProducerConsumer<List<String>, Point>() {
            @Override
            void processItem(List<String> input) {
                //System.out.println("HEre");
                List<Point> points=new ArrayList<Point>(input.size());
                for(String line:input){
                    String[] tokens=line.split(",");
                    double [] point=new double[tokens.length];
                    for(int i=0;i<point.length;i++)
                        point[i]=Double.parseDouble(tokens[i]);
                    points.add(new Point(point));
                }
                resultsQ.addAll(points);
                //System.out.println("Added some: "+points.size());
            }
        };

        int read_lines=0;
        //producerConsumer.Start();
        Stopwatch watch=new Stopwatch().start();
        while (read_lines<count)
        {
            List<String> lines=new ArrayList<String>(chunk_size);
            for(int i=0;i<chunk_size;i++)
            {
                String line= readNextNonEmptyLine();
                if(line==null)
                    break;
                lines.add(line);
            }
            if(lines.size()==0)
                break;
            //chunks.add(lines);
            producerConsumer.AddInput(lines);
            read_lines+=lines.size();
        }

        System.out.println("Read "+read_lines+" lines in "+watch);
        if(read_lines==0)
            return null;


        //producerConsumer.AddAllInput(chunks);
        producerConsumer.InputIsDone();
        producerConsumer.Start();
        try {
            return producerConsumer.GetResults();
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }
    }


}
