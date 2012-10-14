package org.tmu.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: Saeed
 * Date: 10/14/12
 * Time: 7:24 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ParallelProducerConsumer<InputType,OutputType> {
    BlockingQueue<InputType> queue=new LinkedBlockingQueue<InputType>(1024);
    ConcurrentLinkedQueue<OutputType> results=new ConcurrentLinkedQueue<OutputType>();

    CountDownLatch liveThreadsCount;
    List<Thread> threadList=new ArrayList<Thread>();
    int threadCount;
    AtomicBoolean stop=new AtomicBoolean(false);

    abstract void doWork();

    ParallelProducerConsumer()
    {
        threadCount=Runtime.getRuntime().availableProcessors();
    }

    ParallelProducerConsumer(int thread_count)
    {
        threadCount=thread_count;
    }

    private void initThreads()
    {
        liveThreadsCount=new CountDownLatch(threadCount);
        for(int i=0;i<threadCount;i++)
        {
            Runnable r=new Runnable() {
                public void run() {
                    try{
                    do{
                        doWork();
                    }while (!stop.get());
                    }catch (Exception e) {
                        System.out.println("This shall never happen!");
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    finally {
                        liveThreadsCount.countDown();
                    }
                }
            };
            Thread t=new Thread(r);
            t.start();
            threadList.add(t);
        }
    }

    public boolean IsDone()
    {
        try {
            liveThreadsCount.await();
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return true;
    }

    public Collection<OutputType> GetResults()
    {
        IsDone();
        return results;
    }

}
