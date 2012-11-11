package org.tmu.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.stat.clustering.Clusterable;
/**
 * Created with IntelliJ IDEA.
 * User: Saeed
 * Date: 10/9/12
 * Time: 7:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class Point implements Clusterable<Point>{
    public double[] elements=null;

    public Point()
    {
        elements=new double[0];
    }

    public Point(int size)
    {
        elements=new double[size];
        for(int i=0;i<size;i++)
            elements[i]=0.0;
    }

    public Point(double[] point)
    {
        elements=new double[point.length];
        System.arraycopy(point, 0, elements, 0, point.length);
    }

    public Point(Point point)
    {
        this(point.elements);
    }

    public Double getElement(int index) {
        return elements[index];
    }

    public void setElement(int index, double value)
    {
        elements[index]=value;
    }

    public int size()
    {
        return elements.length;
    }

    public static Point generateRandom(Point center, Random random){
        Point result=new Point(center.size());
        for(int i=0;i<result.size();i++)
            result.setElement(i,random.nextDouble()+center.getElement(i)-0.5);
        return result;
    }

    public String toString()
    {
        StringBuilder builder=new StringBuilder();
        //DecimalFormat threeDec = new DecimalFormat("0.000");
        builder.append("(");
        for(int i=0;i<elements.length;i++){
            //builder.append(threeDec.format(elements[i])).append(",");
            //some dirty code to convert double to string with 4 precision
            String s=Double.toString(elements[i]);
            int dot_place=s.indexOf('.');
            if(dot_place+5<s.length())
                s=s.substring(0,dot_place+5);
            builder.append(s).append(",");
        }
        builder.deleteCharAt(builder.length()-1);
        builder.append(")");
        return builder.toString();
    }

    public double distanceFrom(Point point) {
        double distance=0.0;
        if(size()!=point.size())
            throw new IllegalArgumentException("Target point's size is not equal!");

        for(int i=0;i<size();i++)
            distance+=(elements[i]-point.getElement(i))*(elements[i]-point.getElement(i));
        return distance;
    }

    public Point centroidOf(Collection<Point> points) {
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

    public Point findNearest(Collection<Point> points){
        Point result=null;
        double  min_dis=Double.MAX_VALUE;
        for( Point p:points){
            if(distanceFrom(p)<min_dis){
                min_dis=distanceFrom(p);
                result=p;
            }
        }
        return result;
    }
}
