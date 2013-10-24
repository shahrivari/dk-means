package org.tmu.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: Saeed
 * Date: 11/5/12
 * Time: 6:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataSetInfo {
    private static int rawChunk=64*1024;

    public static int estimatePointCountBinary(String file_name) throws IOException {
        long size=new File(file_name).length()/8;
        BinaryFormatReader reader=new BinaryFormatReader(file_name);

        if(reader.pointLength>0){
            long result=size/reader.pointLength;
            if(result>Integer.MAX_VALUE)
                throw new IOException("File is too big!");
            return (int)result;
        }
        return 0;
    }

    public static int pointSizeBinary(String file_name) throws IOException {
        BinaryFormatReader reader1=new BinaryFormatReader(file_name);
        Point p=reader1.readNextPoint();
        reader1.close();
        return p.size();
    }

    public static int estimatePointCountCSV(String file_name) throws IOException {
        long size=new File(file_name).length();
        BufferedReader reader1=new BufferedReader(new FileReader(file_name));
        String line=reader1.readLine();
        reader1.close();
        if(line.length()>0){
            long result=size/line.length();
            if(result>Integer.MAX_VALUE)
                throw new IOException("File is too big!");
            return (int)result;
        }

        return 0;
    }

    public static int pointSizeCSV(String file_name) throws IOException {
        CSVReader reader1=new CSVReader(file_name);
        Point p=reader1.ReadNextPoint();
        reader1.close();
        return p.size();
    }

    public static int estimateChunkSize(int pointCount,int pointSize){
        if(pointCount<=0)
            return 0;
        int row_size=pointSize*8;
        int chunk_size=rawChunk/row_size;
        if(pointCount/chunk_size>1000*1000)
            chunk_size=pointCount/(1000*1000);
        return chunk_size;
    }


    public static int estimateChunkSize(Collection<Point> points){
        if(! points.iterator().hasNext())
            return 0;
        return estimateChunkSize(points.size(),points.iterator().next().size());
    }

    public static int estimateIteration(int count){
        return (int)(Math.log(count)*2);///Math.log(2));
    }

    public static int estimateIteration(Collection<Point> points){
        return estimateIteration(points.size());
    }

}
