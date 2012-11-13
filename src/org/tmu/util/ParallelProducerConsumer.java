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
public abstract class ParallelProducerConsumer<InputType, OutputType> {
    BlockingQueue<InputType> inputQ = new LinkedBlockingQueue<InputType>();
    //ConcurrentLinkedQueue<InputType> inputQ=new ConcurrentLinkedQueue<InputType>();
    protected ConcurrentLinkedQueue<OutputType> resultsQ = new ConcurrentLinkedQueue<OutputType>();

    CountDownLatch liveThreadsCount;
    List<Thread> threadList = new ArrayList<Thread>();
    int threadCount;
    AtomicBoolean inputRemains = new AtomicBoolean(true);
    int inputQueueLimit =Integer.MAX_VALUE;

    abstract protected void processItem(InputType input);

    public ParallelProducerConsumer() {
        this(Runtime.getRuntime().availableProcessors(), 10240);
    }

    public ParallelProducerConsumer(int thread_count, int queue_limit) {
        threadCount = thread_count;
        inputQueueLimit=queue_limit;
        initThreads();
    }

    public void InputIsDone() {
        inputRemains.set(false);
    }

    private void initThreads() {
        liveThreadsCount = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            Runnable r = new Runnable() {
                public void run() {
                    while (true) {
                        try {
                            InputType chunk = inputQ.poll(1, TimeUnit.MILLISECONDS);
                            if (chunk == null) {
                                //System.out.println("Consumer: I am IDLE!!!!!");
                                if (inputRemains.get()) {
//                                    //System.out.println("Consumer: I am IDLE!!!!!");
//                                      Thread.sleep(1);
                                    continue;
                                } else {
                                    liveThreadsCount.countDown();
                                    return;
                                }
                            }
                            processItem(chunk);
                        } catch (InterruptedException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }

                    }
                }
            };
            Thread t = new Thread(r);
            threadList.add(t);
        }
    }

    public void Start() {
        for (Thread t : threadList)
            t.start();
    }

    public void AddInput(InputType input) throws InterruptedException {
        if(inputQ.size()>inputQueueLimit){
            Thread.sleep(1);
            //System.out.println("Waiting for queueeeeeeeeeeeeeeeeeeeeeeeeee!!!!");
        }
        inputQ.add(input);
    }

    public void AddAllInput(Collection<InputType> inputs) {
        inputQ.addAll(inputs);
    }


    public void WaitTillDone() throws InterruptedException {
        liveThreadsCount.await();
    }

    public void WaitTillDone(long timeout, TimeUnit timeUnit) throws InterruptedException {
        liveThreadsCount.await(timeout, timeUnit);
    }

    public Collection<OutputType> GetResults() throws InterruptedException {
        WaitTillDone();
        return resultsQ;
    }

    public Collection<OutputType> GetResults(long timeout, TimeUnit timeUnit) throws InterruptedException {
        WaitTillDone(timeout, timeUnit);
        return resultsQ;
    }

}
