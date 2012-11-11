package org.tmu.clustering;

import org.apache.commons.math3.stat.clustering.Cluster;
import org.apache.commons.math3.stat.clustering.Clusterable;
import org.apache.commons.math3.stat.clustering.KMeansPlusPlusClusterer;
import org.tmu.util.ParallelProducerConsumer;

import java.util.Collection;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Saeed
 * Date: 10/16/12
 * Time: 4:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImprovedStreamClusterer<T extends Clusterable<T>> {
    ParallelProducerConsumer<Collection<T>, T> parallelProducerConsumer;

    public ImprovedStreamClusterer(final int cluster_count, final int chunk_iteration)
    {
        parallelProducerConsumer =new ParallelProducerConsumer<Collection<T>, T>() {
            @Override
            protected void processItem(Collection<T> input) {
                if(input.size()<cluster_count)
                    return;
                KMeansPlusPlusClusterer<T> kmpp=new KMeansPlusPlusClusterer<T>(new Random());
                Collection<Cluster<T>> clusters=kmpp.cluster(input,cluster_count,chunk_iteration);
                for (Cluster<T> cluster:clusters)
                    resultsQ.add(cluster.getCenter());
            }
        };

    }

    public ImprovedStreamClusterer(final int cluster_count){
        parallelProducerConsumer =new ParallelProducerConsumer<Collection<T>, T>() {
            @Override
            protected void processItem(Collection<T> input) {
                KMeansPlusPlusClusterer<T> kmpp=new KMeansPlusPlusClusterer<T>(new Random());
                Collection<Cluster<T>> clusters=kmpp.cluster(input,cluster_count,(int)Math.log(input.size()));
                for (Cluster<T> cluster:clusters)
                    resultsQ.add(cluster.getCenter());
            }
        };
    }


    public void Start(){
        parallelProducerConsumer.Start();
    }

    public void AddChunk(Collection<T> chunk) throws InterruptedException {
        parallelProducerConsumer.AddInput(chunk);
    }

    public void InputIsDone()
    {
        parallelProducerConsumer.InputIsDone();
    }

    public void WaitTillDone() throws InterruptedException {
        parallelProducerConsumer.WaitTillDone();
    }

    public Collection<T> GetIntermediateCenters()
    {
        try {
            return parallelProducerConsumer.GetResults();
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }



}
