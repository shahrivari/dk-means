package org.tmu.clustering;

import org.apache.commons.math3.stat.clustering.Cluster;
import org.apache.commons.math3.stat.clustering.Clusterable;
import org.tmu.util.CSVReader;
import org.tmu.util.Point;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Saeed
 * Date: 10/20/12
 * Time: 8:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class DKMeansClusterer  {



    public List<Cluster<Point>> cluster(String file_name,int cluster_count, int thread_count) throws IOException, InterruptedException {
        CSVReader csvReader=new CSVReader(file_name);
        ImprovedStreamClusterer<Point> smeans=new ImprovedStreamClusterer<Point>(cluster_count);
        smeans.Start();

        Collection<Point> points;
        do{
            points=csvReader.ReadSomePointInParallel(102400,2);
            if(points==null)
                break;
            smeans.AddChunk(points);
        }while (points.size()>0);

        smeans.InputIsDone();

        smeans.WaitTillDone();

        return null;
    }

}
