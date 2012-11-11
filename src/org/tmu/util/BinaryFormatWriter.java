package org.tmu.util;

import java.io.*;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: Saeed
 * Date: 10/23/12
 * Time: 7:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class BinaryFormatWriter {
    DataOutputStream stream;
    int pointLength=0;


    public BinaryFormatWriter(String file_name) throws IOException {
        stream=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file_name),64*1024));
    }

    public void flush() throws IOException {
        stream.flush();
    }

    public void close() throws IOException {
        stream.close();
    }

    public void writePoint(Point p) throws IOException {
        if(pointLength==0){
            stream.writeBytes("BIN");
            pointLength=p.size();
            stream.writeInt(pointLength);
            stream.flush();
        }

        for(int i=0;i<pointLength;i++)
            stream.writeDouble(p.getElement(i));
    }

    public void writePoint(double[] p) throws IOException {
        if(pointLength==0){
            pointLength=p.length;
            stream.writeInt(pointLength);
        }

        for(int i=0;i<pointLength;i++)
            stream.writeDouble(p[i]);
    }

    public void writeSomePoints(Collection<Point> points) throws IOException {
        for(Point p:points)
            writePoint(p);
    }

}
