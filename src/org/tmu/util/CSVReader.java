package org.tmu.util;

import com.google.common.base.Stopwatch;

import java.io.*;
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

    ParallelStreamConsumer<List<String>, Point> producerConsumer = null;

    public void close() throws IOException {
        if (producerConsumer != null)
            producerConsumer.Stop();
        reader.close();
        fileReader.close();
    }

    @Override
    protected void finalize() throws IOException {
        close();
    }


    public CSVReader(String file_name) throws FileNotFoundException {
        fileReader = new FileReader(file_name);
        reader = new BufferedReader(fileReader);
    }

    public void StartPool(int thread_count, int queue_limit) {
        producerConsumer = new ParallelStreamConsumer<List<String>, Point>(thread_count, queue_limit) {
            @Override
            protected void processItem(List<String> input) {
                //System.out.println("HEre");
                List<Point> points = new ArrayList<Point>(input.size());
                for (String line : input) {
                    String[] tokens = line.split("\\s|,|;");
                    double[] point = new double[tokens.length];
                    for (int i = 0; i < point.length; i++)
                        point[i] = Double.parseDouble(tokens[i]);
                    points.add(new Point(point));
                }
                resultsQ.addAll(points);
                //System.out.println("Added some: "+points.size());
            }
        };
    }

    public void StartPool() {
        StartPool(Runtime.getRuntime().availableProcessors(), 1024);
    }

    public void StopPool() {
        producerConsumer.Stop();
    }


    private String readNextNonEmptyLine() throws IOException {
        String line = "";
        do {
            line = reader.readLine();
            if (line == null) return null;
            line = line.trim();
        } while (line.isEmpty());
        return line;
    }

    public Point ReadNextPoint() throws IOException {
        String line = readNextNonEmptyLine();
        if (line == null) return null;
        String[] tokens = line.split(",");
        double[] point = new double[tokens.length];

        for (int i = 0; i < point.length; i++)
            try {
                point[i] = Double.parseDouble(tokens[i]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                System.out.println("OOOOOOPSPPSSS:  " + point[i]);
            }
        return new Point(point);
    }


    public Collection<Point> ReadSomePoint(int count) throws IOException {
        Stopwatch watch = new Stopwatch().start();
        List<Point> points = new ArrayList<Point>(count);
        Point point;
        for (int i = 0; i < count; i++) {
            point = ReadNextPoint();
            if (point == null)
                break;
            points.add(point);
        }
        if (points.size() == 0)
            return null;
        //System.out.println("Read "+points.size()+" in "+watch.stop());
        return points;
    }

    public static Stopwatch TimeSequentialFileRead(String file_name) throws IOException {
        CSVReader csvReader = new CSVReader(file_name);
        Stopwatch watch = new Stopwatch().start();
        Point point;
        while (true) {
            point = csvReader.ReadNextPoint();
            if (point == null)
                break;
        }
        csvReader.close();
        watch.stop();
        return watch;
    }

    public static Stopwatch TimeParallelFileRead(String file_name, int thread_count) throws IOException, InterruptedException {
        CSVReader csvReader = new CSVReader(file_name);
        csvReader.StartPool(thread_count, 1024);
        Stopwatch watch = new Stopwatch().start();
        Collection<Point> points;
        while (true) {
            points = csvReader.ReadSomePointInParallel(102400, 1024);
            if (points == null)
                break;
        }
        csvReader.close();
        watch.stop();
        return watch;
    }


    public Collection<Point> ReadSomePointInParallel(int count, int chunk_size) throws IOException, InterruptedException {
        if (producerConsumer == null)
            throw new ExceptionInInitializerError("Threadpool is not started!");
        if (count < 1024)
            return ReadSomePoint(count);

        int read_lines = 0;
        Stopwatch watch = new Stopwatch().start();
        while (read_lines < count) {
            List<String> lines = new ArrayList<String>(chunk_size);
            for (int i = 0; i < chunk_size; i++) {
                String line = readNextNonEmptyLine();
                if (line == null)
                    break;
                lines.add(line);
            }
            if (lines.size() == 0)
                break;
            producerConsumer.AddInput(lines);
            read_lines += lines.size();
        }

        //System.out.println("Read "+read_lines+" lines in "+watch);
        if (read_lines == 0)
            return null;

        producerConsumer.WaitTillIdle();
        try {
            return producerConsumer.GetAndClearResults();
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length > 0) {
            String command = args[0].toLowerCase();
            if (command.equals("convert")) {
                if (args.length != 3) {
                    System.out.println("Invalid arguments!");
                    System.out.println("Signature: convert [CSV_FILE] [DESTINATION]");
                    return;
                }
                String file_name1 = args[1];
                String file_name2 = args[2];
                convertCSVtoBinary(file_name1, file_name2);
                return;
            }
        }
        System.out.println("Accepted commands:");
        System.out.println("\t convert [CSV_FILE] [DESTINATION]");
    }

    public static void convertCSVtoBinary(final String csv_path, final String binary_path) throws IOException, InterruptedException {
        convertCSVtoBinary(csv_path, binary_path, 32 * 1024 * 1024);
    }

    public static void convertCSVtoBinary(final String csv_path, final String binary_path, int bufferSize) throws IOException, InterruptedException {
        Stopwatch watch = new Stopwatch().start();
        CSVReader csvReader = new CSVReader(csv_path);
        BinaryFormatWriter writer = new BinaryFormatWriter(binary_path);

        //read first point
        Point p = csvReader.ReadNextPoint();
        if (p == null) {
            System.out.println("Source file is empty!");
            return;
        }

        writer.writePoint(p);

        int written = 1;

        System.out.println("Point size is: " + p.size());
        int parallel_read_count = bufferSize / (8 * p.size());
        System.out.println("Will read " + parallel_read_count + " points in parallel at each step.");
        System.out.flush();
        csvReader.StartPool();

        Collection<Point> points;
        while (true) {
            points = csvReader.ReadSomePointInParallel(parallel_read_count, 8192 / p.size());
            //points=csvReader.ReadSomePoint(parallel_read_count);
            if (points == null)
                break;
            writer.writeSomePoints(points);
            written += points.size();
            System.out.println("Written " + written + " points.");
        }


        csvReader.StopPool();
        csvReader.close();
        writer.close();
        System.out.println("Time spent: " + watch.stop());
    }

    public static List<Point> readWholeFile(String file_name) throws IOException {
        CSVReader csvReader = new CSVReader(file_name);
        Collection<Point> points;
        List<Point> result = new ArrayList<Point>(1024);

        do {
            points = csvReader.ReadSomePoint(1024);
            if (points == null)
                break;
            result.addAll(points);
        } while (points.size() > 0);
        return result;
    }


}
