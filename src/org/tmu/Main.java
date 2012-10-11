package org.tmu;

import org.tmu.util.PointGeometry;
import org.tmu.util.Point;
import org.tmu.util.RandomPointGenerator;


import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Saeed
 * Date: 10/9/12
 * Time: 5:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class Main {
    public static void main(String[] args)
    {
        List<Point> points= RandomPointGenerator.Generate(100000,10,123);

        Point center= PointGeometry.ComputeCenter(points);


    }
}
