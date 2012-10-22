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
    FileReader fileReader;

    ParallelProducerConsumer<List<String>,Point> producerConsumer=new ParallelProducerConsumer<List<String>, Point>() {
        @Override
        protected void processItem(List<String> input) {
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

    public void close() throws IOException {
        reader.close();
        fileReader.close();
    }


    public CSVReader(String file_name) throws FileNotFoundException {
        fileReader =new FileReader(file_name);
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


    public Collection<Point> ReadSomePoint(int count)throws IOException{
        Stopwatch watch=new Stopwatch().start();
        List<Point> points=new ArrayList<Point>(count);
        Point point;
        for(int i=0;i<count;i++){
            point=ReadNextPoint();
            if(point==null)
                break;
            points.add(point);
        }
        if(points.size()==0)
            return null;
        //System.out.println("Read "+points.size()+" in "+watch.stop());
        return points;
    }

    public static Stopwatch TimeSequentialFileRead(String file_name) throws IOException {
        CSVReader csvReader=new CSVReader(file_name);
        Stopwatch watch=new Stopwatch().start();
        Point point;
        while (true){
            point=csvReader.ReadNextPoint();
            if(point==null)
                break;
        }
        csvReader.close();
        watch.stop();
        return watch;
    }


    public Collection<Point> ReadSomePointInParallel(int count, int chunk_size, int threads_count) throws IOException, InterruptedException {
        if(count<1024)
            return ReadSomePoint(count);

        int read_lines=0;
        producerConsumer.Start();
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
            producerConsumer.AddInput(lines);
            read_lines+=lines.size();
        }

        //System.out.println("Read "+read_lines+" lines in "+watch);
        if(read_lines==0)
            return null;


        producerConsumer.InputIsDone();
        producerConsumer.Start();
        producerConsumer.WaitTillDone();
        try {
            return producerConsumer.GetResults();
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }
    }


}
