package org.tmu.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: Saeed
 * Date: 10/14/12
 * Time: 7:24 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ParallelProducerConsumer<InputType,OutputType> {
    BlockingQueue<InputType> inputQ=new LinkedBlockingQueue<InputType>(1024);
    ConcurrentLinkedQueue<OutputType> resultsQ=new ConcurrentLinkedQueue<OutputType>();

    CountDownLatch liveThreadsCount;
    List<Thread> threadList=new ArrayList<Thread>();
    int threadCount;
    AtomicBoolean inputRemains =new AtomicBoolean(true);

    abstract void processItem(InputType input);

    ParallelProducerConsumer()
    {
        threadCount=Runtime.getRuntime().availableProcessors();
        initThreads();
    }

    ParallelProducerConsumer(int thread_count)
    {
        threadCount=thread_count;
        initThreads();
    }

    public void InputIsDone()
    {
        inputRemains.set(false);
    }

    private void initThreads()
    {
        liveThreadsCount=new CountDownLatch(threadCount);
        for(int i=0;i<threadCount;i++)
        {
            Runnable r=new Runnable() {
                public void run() {
                    try{
                        while (true){
                            InputType chunk=inputQ.poll(10, TimeUnit.MILLISECONDS);
                            if(chunk==null){
                                //System.out.println("Consumer: I am IDLE!!!!!");
                                if(inputRemains.get())
                                    continue;
                                else{
                                    liveThreadsCount.countDown();
                                    return;
                                }
                            }
                            processItem(chunk);
                        }
                    }catch (Exception e) {
                        System.out.println("Exception Occurred!");
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    finally {
                        //System.out.println("Sure????!");
                        //liveThreadsCount.countDown();
                    }
                }
            };
            Thread t=new Thread(r);
            threadList.add(t);
        }
    }

    public void Start()
    {
        for(Thread t:threadList)
            t.start();
    }

    public void AddInput(InputType input)
    {
        inputQ.add(input);
    }

    public void AddAllInput(Collection<InputType> inputs)
    {
        inputQ.addAll(inputs);
    }


    public void WaitTillDone() throws InterruptedException {
        liveThreadsCount.await();
    }

    public void WaitTillDone(long timeout, TimeUnit timeUnit) throws InterruptedException {
        liveThreadsCount.await(timeout,timeUnit);
    }

    public Collection<OutputType> GetResults() throws InterruptedException {
        WaitTillDone();
        return resultsQ;
    }

    public Collection<OutputType> GetResults(long timeout,TimeUnit timeUnit) throws InterruptedException {
        WaitTillDone(timeout, timeUnit);
        return resultsQ;
    }

}
