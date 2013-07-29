package org.tmu;

import com.google.common.base.Stopwatch;
import org.apache.commons.cli.*;
import org.tmu.clustering.CenteroidEvaluator;
import org.tmu.old.DKMeansClusterer;
import org.tmu.clustering.MasterPointClusterer;
import org.tmu.old.InMemClusteringTester;
import org.tmu.old.OnDiskClusteringTester;
import org.tmu.util.*;


import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Saeed
 * Date: 10/9/12
 * Time: 5:41 PM
 * To change this template use File | Settings | File Templates.
 */

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        //parsing command line
        // create the command line parser
        CommandLineParser parser = new BasicParser();

        // create the Options
        Options options = new Options();
        options.addOption("i", "input", true, "the input file name");
        options.addOption("k", "clusters", true, "number of the clusters");
        options.addOption("a", "algorithm", true, "algorithm to use: kmeans, or kmeans++, or dkmeans (default is dkmeans)");
        options.addOption("b", "binary", false, "read input file as binary");
        options.addOption("s", "statistics", false, "print detailed statistics (Sum of Errors)");
        options.addOption("t", "threads", true, "number of threads to use (default is equal to number of cores)");
        options.addOption("ch", "chunk_size", true, "Size of the chunks (number of points in each chunk)");
        options.addOption("silent", false, "suppress progress report.");
        HelpFormatter formatter = new HelpFormatter();

        String file_name=null;
        int threads=Runtime.getRuntime().availableProcessors();
        int chunk_size=100;
        String algorithm="dkmeans";

        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);

            if (!line.hasOption("i")) {
                System.out.println("An input file must be given.");
                formatter.printHelp("dkmeans", options);
                System.exit(-1);
            }

            file_name=line.getOptionValue("i");

            if (line.hasOption("t"))
                threads = Integer.parseInt(line.getOptionValue("t"));

            if (line.hasOption("ch"))
                chunk_size = Integer.parseInt(line.getOptionValue("ch"));






        } catch (org.apache.commons.cli.ParseException exp) {
            System.out.println("Unexpected exception:" + exp.getMessage());
            formatter.printHelp("dkmeans", options);
            System.exit(-1);
        }

    }
}
