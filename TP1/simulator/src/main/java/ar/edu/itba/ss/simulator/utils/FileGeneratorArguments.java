package ar.edu.itba.ss.simulator.utils;

import java.io.File;

public class FileGeneratorArguments {
    private final File staticFile;
    private final File dynamicFile;
    private final int L;
    private final double R;
    private final double property;
    private final int N;
    private final String delimiter;
    private final int times;

    public FileGeneratorArguments(File staticFile, File dynamicFile, int L, double R, double property, int N, String delimiter, int times) {
        this.staticFile = staticFile;
        this.dynamicFile = dynamicFile;
        this.L = L;
        this.R = R;
        this.property = property;
        this.N = N;
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

    public double getR() {
        return R;
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

