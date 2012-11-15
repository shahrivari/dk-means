package org.tmu.util;

import com.google.common.base.Stopwatch;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: Saeed
 * Date: 10/23/12
 * Time: 5:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class BinaryFormatReader {
    DataInputStream stream;
    int pointLength=0;

    double [] point;


    public BinaryFormatReader(String file_name) throws IOException{
        stream=new DataInputStream(new BufferedInputStream(new FileInputStream(file_name),64*1024));
        char ch= (char) stream.readByte();
        if(ch!='B')
            throw new IOException("Wrong binary format!");
        ch=(char) stream.readByte();
        if(ch!='I')
            throw new IOException("Wrong binary format!");
        ch=(char) stream.readByte();
        if(ch!='N')
            throw new IOException("Wrong binary format!");

        pointLength=stream.readInt();
        if(pointLength<=0)
            throw new IOException("Point length is invalid:"+pointLength);
        point=new double[pointLength];
    }

    public void close() throws IOException {
        stream.close();
    }


//    public Point readNextPoint() throws IOException {
//        double [] point=new double[pointLength];
//        try{
//            for(int i=0;i<pointLength;i++)
//                point[i]=stream.readDouble();
//        }catch (EOFException exp){
//            return null;
//        }
//        return new Point(point);
//    }

    public Point readNextPoint() throws IOException {
        try{
            for(int i=0;i<pointLength;i++)
                point[i]=stream.readDouble();
        }catch (EOFException exp){
            return null;
        }
        return new Point(point);
    }


    public Collection<Point> readSomePoint(int count) throws IOException {
        Collection<Point> points=new ArrayList<Point>(count);
        for(int i=0;i<count;i++){
            Point p=readNextPoint();
            if(p==null)
                break;
            points.add(p);
        }
        if(points.size()==0)
            return null;
        return points;
    }

    public static Stopwatch TimeSequentialFileRead(String file_name) throws IOException {
        BinaryFormatReader reader=new BinaryFormatReader(file_name);
        Stopwatch watch=new Stopwatch().start();
        Point point;
        while (true){
            point=reader.readNextPoint();
            if(point==null)
                break;
        }
        watch.stop();
        return watch;
    }


}
