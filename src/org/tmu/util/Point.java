package org.tmu.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Saeed
 * Date: 10/9/12
 * Time: 7:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class Point {
    private List<Double> elements=new ArrayList<Double>();

    public Point(){}

    public Point(int size)
    {
        for(int i=0;i<size;i++)
            getElements().add(0.0);
    }

    public Point(List<Double> point)
    {
        for(int i=0;i<point.size();i++)
            elements.add(point.get(i));
    }

    public Point clone()
    {
        Point p=new Point();
        for(Double d: getElements())
            p.getElements().add(d);
        return p;
    }

    public List<Double> getElements() {
        return elements;
    }

    public Double getElement(int index) {
        return elements.get(index);
    }

    public void setElement(int index, Double value) {
        elements.set(index,value);
    }

    public int size()
    {
        return elements.size();
    }

    public String toString()
    {
        StringBuilder builder=new StringBuilder();
        builder.append("(");
        for(int i=0;i<elements.size();i++)
            builder.append(elements.get(i).toString()+",");
        builder.deleteCharAt(builder.length()-1);
        builder.append(")");
        return builder.toString();
    }
}
