package org.tmu.clustering;

import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.stat.clustering.Cluster;
import org.apache.commons.math3.stat.clustering.Clusterable;
import org.apache.commons.math3.util.MathUtils;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Saeed
 * Date: 10/12/12
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleKMeansClusterer<T extends Clusterable<T>>  {
    private final Random random;

    public SimpleKMeansClusterer(Random random) {
        this.random = random;
    }

    public List<Cluster<T>> cluster(final Collection<T> points, final int k,
                                    final int maxIterations)
            throws MathIllegalArgumentException, ConvergenceException {

        // sanity checks
        MathUtils.checkNotNull(points);

        // number of clusters has to be smaller or equal the number of data points
        if (points.size() < k) {
            throw new NumberIsTooSmallException(points.size(), k, false);
        }

        long t0=System.currentTimeMillis();
        // create the initial clusters
        List<Cluster<T>> clusters = chooseInitialCenters(points, k, random);
        long t1=System.currentTimeMillis()-t0;
        System.out.println("OOOOOOPPPPPS: "+t1);

        // create an array containing the latest assignment of a point to a cluster
        // no need to initialize the array, as it will be filled with the first assignment
        int[] assignments = new int[points.size()];
        assignPointsToClusters(clusters, points, assignments);

        final int max = (maxIterations < 0) ? Integer.MAX_VALUE : maxIterations;
        for (int count = 0; count < max; count++) {
            System.out.println(count);
            boolean emptyCluster = false;
            List<Cluster<T>> newClusters = new ArrayList<Cluster<T>>();
            for (final Cluster<T> cluster : clusters) {
                final T newCenter;
                newCenter = cluster.getCenter().centroidOf(cluster.getPoints());
                newClusters.add(new Cluster<T>(newCenter));
            }
            int changes = assignPointsToClusters(newClusters, points, assignments);
            clusters = newClusters;

            // if there were no more changes in the point-to-cluster assignment
            // and there are no empty clusters left, return the current clusters
            if (changes == 0 && !emptyCluster) {
                return clusters;
            }
        }
        return clusters;
    }

    private static <T extends Clusterable<T>> int
    assignPointsToClusters(final List<Cluster<T>> clusters, final Collection<T> points,
                           final int[] assignments) {
        int assignedDifferently = 0;
        int pointIndex = 0;
        for (final T p : points) {
            int clusterIndex = getNearestCluster(clusters, p);
            if (clusterIndex != assignments[pointIndex]) {
                assignedDifferently++;
            }

            Cluster<T> cluster = clusters.get(clusterIndex);
            cluster.addPoint(p);
            assignments[pointIndex++] = clusterIndex;
        }

        return assignedDifferently;
    }

    private static <T extends Clusterable<T>> int
    getNearestCluster(final Collection<Cluster<T>> clusters, final T point) {
        double minDistance = Double.MAX_VALUE;
        int clusterIndex = 0;
        int minCluster = 0;
        for (final Cluster<T> c : clusters) {
            final double distance = point.distanceFrom(c.getCenter());
            if (distance < minDistance) {
                minDistance = distance;
                minCluster = clusterIndex;
            }
            clusterIndex++;
        }
        return minCluster;
    }


    private static <T extends Clusterable<T>> List<Cluster<T>>
    chooseInitialCenters(final Collection<T> points, final int k, final Random random) {

        // Convert to list for indexed access. Make it unmodifiable, since removal of items
        // would screw up the logic of this method.
        final List<T> pointList = Collections.unmodifiableList(new ArrayList<T>(points));

        // The number of points in the list.
        final int numPoints = pointList.size();

        // The resulting list of initial centers.
        final List<Cluster<T>> resultSet = new ArrayList<Cluster<T>>();

        // Choose one center uniformly at random from among the data points.
        final int firstPointIndex = random.nextInt(numPoints);
        int secondPointIndex=0;
        int thirdPointIndex=0;
        do{
            secondPointIndex=random.nextInt(numPoints);
        }while(secondPointIndex==firstPointIndex);

        do{
            thirdPointIndex=random.nextInt(numPoints);
        }while(thirdPointIndex==firstPointIndex || thirdPointIndex==secondPointIndex);

        resultSet.add(new Cluster<T>(pointList.get(firstPointIndex)));
        resultSet.add(new Cluster<T>(pointList.get(secondPointIndex)));
        resultSet.add(new Cluster<T>(pointList.get(thirdPointIndex)));

        return resultSet;
    }


}
