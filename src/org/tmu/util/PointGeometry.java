package org.tmu.util;

import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Saeed
 * Date: 10/9/12
 * Time: 8:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class PointGeometry {
    public static Point ComputeCenter(Collection<Point> points)
    {
        if(points.size()==0)
            throw new IllegalArgumentException("There is no points!");

        Point result=new Point(points.iterator().next().size());

        for( Point p:points)
        {
            if(p.size()!=result.size())
                throw new IllegalArgumentException("There is a point with mismatched size: "+ p.toString());
            for (int i=0;i<p.size();i++)
                result.setElement(i,result.getElement(i)+p.getElement(i));
        }

        for (int i=0;i<result.size();i++)
            result.setElement(i,result.getElement(i)/points.size());

        return result;
    }
}
