package ar.edu.itba.ss.simulator.utils;

import java.io.File;

public class BaseArguments {

    private final File staticFile;
    private final File dynamicFile;
    private final String outResultsFilePath;

    private final String outExitTimeFile;
    private final String delimiter;
    private final double maxTime;
    private final double dt;
    private final double w;
    private final double D;


    public BaseArguments(File staticFile, File dynamicFile, String outResultsFilePath, String outExitTimeFile, String delimiter, double maxTime, double dt, double w, double d) {
        this.staticFile = staticFile;
        this.dynamicFile = dynamicFile;
        this.outResultsFilePath = outResultsFilePath;
        this.outExitTimeFile = outExitTimeFile;
        this.delimiter = delimiter;
        this.maxTime = maxTime;
        this.dt = dt;
        this.w = w;
        D = d;
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

    public String getOutExitTimeFile() {
        return outExitTimeFile;
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

    public double getW() {
        return w;
    }

    public double getD() {
        return D;
    }
}


