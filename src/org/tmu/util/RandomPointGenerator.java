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
    static public List<Point> Generate(int count, int dimension, int random_seed)
    {
        Random random=new Random(random_seed);
        List<Point> result=new ArrayList<Point>(count);

        for(int i=0;i<count;i++)
        {
            List<Double> elements=new ArrayList<Double>(dimension);
            for(int j=0;j<dimension;j++)
                elements.add(random.nextDouble());
            result.add(new Point(elements));
        }

        return result;
    }
}
