package org.tmu.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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

    public Point ReadNextPoint() throws IOException {
        String line="";
        do
        {
            line= reader.readLine();
            if(line==null) return null;
            line=line.trim();
        }while(line.isEmpty());
        String[] tokens=line.split(",");
        double [] point=new double[tokens.length];
        for(int i=0;i<point.length;i++)
            point[i]=Double.parseDouble(tokens[i]);
        return new Point(point);
    }
}
