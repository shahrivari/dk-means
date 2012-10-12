package org.tmu.clustering;

import org.apache.commons.math3.stat.clustering.Cluster;
import org.apache.commons.math3.stat.clustering.Clusterable;
import org.apache.commons.math3.stat.clustering.KMeansPlusPlusClusterer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * User: Saeed
 * Date: 10/12/12
 * Time: 11:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class StreamClusterer<T extends Clusterable<T>>   {
    BlockingQueue<List<T>> queue=new LinkedBlockingQueue<List<T>>();

    ConcurrentLinkedDeque<T> intermediateCenters=new ConcurrentLinkedDeque<T>();

    boolean stop=false;
    boolean done=false;
    int seed=0;
    int clusterCount=0;

    public StreamClusterer(int cluster_count,Random random)
    {
        seed=random.nextInt();
        clusterCount=cluster_count;
        Runnable r=new Runnable() {
            public void run() {
                doWork();
            }
        };
        Thread t=new Thread(r);
        t.start();
    }

    public void addChunk(List<T> chunk)
    {
        queue.add(chunk);
    }

    public List<T> getIntermediateCenters()
    {
        while (!done)
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                continue;
            }
        List<T> result=new ArrayList<T>(intermediateCenters.size());
        result.addAll(intermediateCenters);
        return result;
    }

    private void doWork() {
        int i=0;
        KMeansPlusPlusClusterer<T> kmeanspp=new KMeansPlusPlusClusterer<T>(new Random(seed));
        while(!stop)
        {
            try {
                List<T> chunk=queue.poll(1, TimeUnit.SECONDS);
                if(chunk==null) continue;
                System.out.println(i++);
                if(chunk.size()==0){
                    stop=true;
                    break;
                }
                List<Cluster<T>> clusters=kmeanspp.cluster(chunk,clusterCount,(int)Math.ceil(Math.log(chunk.size())));
                for(Cluster<T> cluster:clusters)
                    intermediateCenters.add(cluster.getCenter());
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        done=true;
    }

}
