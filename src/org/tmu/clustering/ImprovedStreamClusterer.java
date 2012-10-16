package org.tmu.clustering;

import org.apache.commons.math3.stat.clustering.Cluster;
import org.apache.commons.math3.stat.clustering.Clusterable;
import org.apache.commons.math3.stat.clustering.KMeansPlusPlusClusterer;

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
    //ConcurrentLinkedQueue chunkQ=new ConcurrentLinkedQueue();
    ArrayBlockingQueue<Collection<T>> chunkQ = new ArrayBlockingQueue<Collection<T>>(1024, false);
    ConcurrentLinkedQueue<T> intermediateCenters = new ConcurrentLinkedQueue<T>();

    CountDownLatch liveThreadsCount;
    List<Thread> threadList = new ArrayList<Thread>();
    int threadCount;
    AtomicBoolean inputRemains = new AtomicBoolean(true);
    int randomSeed = 0;

    int clusterCount = 0;
    int innerLoopCount = 7;

    public ImprovedStreamClusterer(int cluster_count, int inner_loop_count, int thread_count, Random random) {
        clusterCount = cluster_count;
        innerLoopCount = inner_loop_count;
        threadCount = thread_count;
        randomSeed = random.nextInt();
        init();
    }

    public ImprovedStreamClusterer(int clusterCount) {
        this(clusterCount, 0, Runtime.getRuntime().availableProcessors(), new Random());
    }

    private void init() {
        liveThreadsCount = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++)
            threadList.add(new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            doWork();
                            liveThreadsCount.countDown();
                        }
                    }
            ));
    }

    public void InputIsDone()
    {
        inputRemains.set(false);
    }

    public void Start()
    {
        for(Thread t:threadList)
            t.start();
    }

    public void AddChunk(Collection<T> input)
    {
        chunkQ.add(input);
    }


    public void WaitTillDone() throws InterruptedException {
        liveThreadsCount.await();
    }

    public void WaitTillDone(long timeout, TimeUnit timeUnit) throws InterruptedException {
        liveThreadsCount.await(timeout,timeUnit);
    }

    public Collection<T> GetIntermediateCenters() throws InterruptedException {
        return intermediateCenters;
    }

    private void doWork() {
        KMeansPlusPlusClusterer<T> kmeanspp = new KMeansPlusPlusClusterer<T>(new Random(randomSeed));

        try {
            while (inputRemains.get()||!chunkQ.isEmpty()) {
                Collection<T> chunk = chunkQ.poll(5, TimeUnit.MILLISECONDS);
                if(chunk==null)
                    continue;

                List<Cluster<T>> clusters=null;
                if(innerLoopCount<=0)
                    clusters=kmeanspp.cluster(chunk,clusterCount,(int)Math.ceil(Math.log(chunk.size())));
                else
                    clusters=kmeanspp.cluster(chunk,clusterCount,innerLoopCount);

                for(Cluster<T> cluster:clusters)
                    intermediateCenters.add(cluster.getCenter());

            }
        } catch (InterruptedException e) {
            System.out.println("This shall never happen!");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

}
