package ar.edu.itba.ss.simulator.utils;

import java.io.File;

public class BaseArguments {

    private final File staticFile;
    private final File dynamicFile;
    private final String outResultsFilePath;
    private final String outTimeFilePath;
    private final String delimiter;
    private final double maxTime;
    private final double dt;

    private final String algorithm;

    public BaseArguments(File staticFile, File dynamicFile, String outResultsFilePath, String outTimeFilePath, String delimiter, double maxTime, double dt, String algorithm) {
        this.staticFile = staticFile;
        this.dynamicFile = dynamicFile;
        this.outResultsFilePath = outResultsFilePath;
        this.outTimeFilePath = outTimeFilePath;
        this.delimiter = delimiter;
        this.maxTime = maxTime;
        this.algorithm = algorithm;
        this.dt = dt;
    }

    public File getStaticFile() {
        return staticFile;
    }

    public File getDynamicFile() {
        return dynamicFile;
    }

    public String getOutResultsFilePath() {
        return outResultsFilePath;
    }

    public String getOutTimeFilePath() {
        return outTimeFilePath;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public double getMaxTime() {
        return maxTime;
    }

    public double getDt() {
        return dt;
    }

    public String getAlgorithm() {
        return algorithm;
    }
}


