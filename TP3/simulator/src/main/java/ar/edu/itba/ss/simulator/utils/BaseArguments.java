package ar.edu.itba.ss.simulator.utils;

import java.io.File;

public class BaseArguments {

    private final File staticFile;
    private final File dynamicFile;
    private final String outResultsFilePath;
    private final String outTimeFilePath;
    private final String delimiter;
    private final int maxIterations;

    public BaseArguments(File staticFile, File dynamicFile, String outResultsFilePath, String outTimeFilePath, String delimiter, int maxIterations) {
        this.staticFile = staticFile;
        this.dynamicFile = dynamicFile;
        this.outResultsFilePath = outResultsFilePath;
        this.outTimeFilePath = outTimeFilePath;
        this.delimiter = delimiter;
        this.maxIterations = maxIterations;
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

    public int getMaxIterations() {
        return maxIterations;
    }
}

