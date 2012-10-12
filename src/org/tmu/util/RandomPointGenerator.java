package org.tmu.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Saeed
 * Date: 10/9/12
 * Time: 8:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class RandomPointGenerator {
    static public List<Point> GenerateSphere(int point_count, int dimension, int random_seed)
    {
        Random random=new Random(random_seed);
        List<Point> result=new ArrayList<Point>(point_count);

        for(int i=0;i<point_count;i++)
        {
            double[] elements=new double[dimension];
            for(int j=0;j<dimension;j++)
                elements[j]=random.nextDouble();
            result.add(new Point(elements));
        }

        return result;
    }

    static public List<Point> GenerateSphere(Point center, int point_count, Random random)
    {
        if(random==null)random=new Random();
        List<Point> result=new ArrayList<Point>(point_count);

        for(int i=0;i<point_count;i++)
        {
            double[] elements=new double[center.size()];
            for(int j=0;j<center.size();j++)
                elements[j]=random.nextDouble()+center.getElement(j);
            result.add(new Point(elements));
        }

        return result;
    }
}
