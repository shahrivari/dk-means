package org.tmu;

import com.google.common.base.Stopwatch;
import org.apache.commons.math3.stat.clustering.KMeansPlusPlusClusterer;
import org.tmu.util.ParallelProducerConsumer;
import org.tmu.util.Point;
import org.tmu.util.RandomPointGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Saeed
 * Date: 10/18/12
 * Time: 7:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class Test {
    public static void main(String[] args) throws InterruptedException {
        final int count=1000;
        int size=1000;

        ParallelProducerConsumer<List<Point>,Point> parallelProducerConsumer =new ParallelProducerConsumer<List<Point>, Point>() {
            @Override
            protected void processItem(List<Point> input) {
                KMeansPlusPlusClusterer<Point> kmpp=new KMeansPlusPlusClusterer<Point>(new Random());
                kmpp.cluster(input,5,7);
            }
        };

        List<Point> pointList=new ArrayList<Point>(1000);


        for(int j=0;j<size;j++)
            {
                List<Point> rand= RandomPointGenerator.GenerateSphere(new Point(new double[]{j,j,j}),count,new Random());
                pointList.addAll(rand);
                parallelProducerConsumer.AddInput(rand);
            }
        parallelProducerConsumer.InputIsDone();

        KMeansPlusPlusClusterer<Point> kmpp=new KMeansPlusPlusClusterer<Point>(new Random());

        Stopwatch watch=new Stopwatch().start();
        kmpp.cluster(pointList,5,7);

        System.out.println("Serial took:"+watch.elapsedMillis());
        watch.reset().start();


        parallelProducerConsumer.Start();
        parallelProducerConsumer.WaitTillDone();

        System.out.println("Parallel took:"+watch.elapsedMillis());
        watch.reset().start();

    }
}
