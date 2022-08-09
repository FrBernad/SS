package ar.edu.itba.ss.simulator.utils;

import java.io.File;

public class FileGeneratorArguments {
    private final File staticFile;
    private final File dynamicFile;
    private final int L;
    private final double minR;
    private final double maxR;
    private final double property;
    private final int N;
    private final String delimiter;
    private final int times;


    public FileGeneratorArguments(File staticFile, File dynamicFile, int l, double minR, double maxR,
                                  double property, int n, String delimiter, int times) {
        this.staticFile = staticFile;
        this.dynamicFile = dynamicFile;
        L = l;
        this.minR = minR;
        this.maxR = maxR;
        this.property = property;
        N = n;
        this.delimiter = delimiter;
        this.times = times;
    }

    public File getStaticFile() {
        return staticFile;
    }

    public File getDynamicFile() {
        return dynamicFile;
    }

    public int getL() {
        return L;
    }

    public double getMinR() {
        return minR;
    }

    public double getMaxR() {
        return maxR;
    }

    public double getProperty() {
        return property;
    }

    public int getN() {
        return N;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public int getTimes() {
        return times;
    }
}

