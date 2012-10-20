package org.tmu.clustering;

import org.apache.commons.math3.stat.clustering.Cluster;
import org.apache.commons.math3.stat.clustering.Clusterable;
import org.apache.commons.math3.stat.clustering.KMeansPlusPlusClusterer;
import org.tmu.util.PCNG;
import org.tmu.util.Point;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: Saeed
 * Date: 10/16/12
 * Time: 4:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImprovedStreamClusterer<T extends Clusterable<T>> {
    PCNG<Collection<T>, T> pcng;

    public ImprovedStreamClusterer(final int clus_count)
    {
        pcng=new PCNG<Collection<T>, T>() {
            @Override
            protected void processItem(Collection<T> input) {
                KMeansPlusPlusClusterer<T> kmpp=new KMeansPlusPlusClusterer<T>(new Random());
                kmpp.cluster(input,clus_count,7);
            }
        };

    }

    public void Start(){
        pcng.Start();
    }

    public void AddChunk(Collection<T> chunk)
    {
        pcng.AddInput(chunk);
    }

    public void InputIsDone()
    {
        pcng.InputIsDone();
    }

    public void WaitTillDone() throws InterruptedException {
        pcng.WaitTillDone();
    }



}
