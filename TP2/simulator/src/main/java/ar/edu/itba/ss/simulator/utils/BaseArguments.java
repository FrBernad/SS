package ar.edu.itba.ss.simulator.utils;

import java.io.File;

public class BaseArguments {

    private final File staticFile;
    private final File dynamicFile;
    private final String outFlocksFilePath;
    private final String outTimeFilePath;
    private final Boolean isPeriodic;
    private final String delimiter;
    private final double R;
    private final double eta;
    private final double dt;
    private final double threshold;

    private final int maxIterationsOverThreshold;

    public BaseArguments(File staticFile, File dynamicFile, String outFlocksFilePath, String outTimeFilePath, Boolean isPeriodic, String delimiter, double r, double eta, double dt, double threshold, int maxIterationsOverThreshold) {
        this.staticFile = staticFile;
        this.dynamicFile = dynamicFile;
        this.outFlocksFilePath = outFlocksFilePath;
        this.outTimeFilePath = outTimeFilePath;
        this.isPeriodic = isPeriodic;
        this.delimiter = delimiter;
        this.R = r;
        this.eta = eta;
        this.dt = dt;
        this.threshold = threshold;
        this.maxIterationsOverThreshold = maxIterationsOverThreshold;
    }

    public File getStaticFile() {
        return staticFile;
    }

    public File getDynamicFile() {
        return dynamicFile;
    }

    public String getOutFlocksFilePath() {
        return outFlocksFilePath;
    }

    public String getOutTimeFilePath() {
        return outTimeFilePath;
    }

    public Boolean getPeriodic() {
        return isPeriodic;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public double getR() {
        return R;
    }

    public double getEta() {
        return eta;
    }

    public double getDt() {
        return dt;
    }

    public double getThreshold() {
        return threshold;
    }

    public int getMaxIterationsOverThreshold() {
        return maxIterationsOverThreshold;
    }
}

