package ar.edu.itba.ss.simulator.utils;

import java.io.File;

public class BaseArguments {

    private final File staticFile;
    private final File dynamicFile;
    private final String outFlocksFilePath;
    private final String outTimeFilePath;
    private final String outOrderFilePath;
    private final Boolean isPeriodic;
    private final String delimiter;
    private final double R;
    private final double eta;
    private final double dt;
    private final int maxIterations;

    public BaseArguments(File staticFile, File dynamicFile, String outFlocksFilePath, String outTimeFilePath, String outOrderFilePath, Boolean isPeriodic, String delimiter, double r, double eta, double dt, int maxIterations) {
        this.staticFile = staticFile;
        this.dynamicFile = dynamicFile;
        this.outFlocksFilePath = outFlocksFilePath;
        this.outTimeFilePath = outTimeFilePath;
        this.outOrderFilePath = outOrderFilePath;
        this.isPeriodic = isPeriodic;
        this.delimiter = delimiter;
        R = r;
        this.eta = eta;
        this.dt = dt;
        this.maxIterations = maxIterations;
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

    public String getOutOrderFilePath() {
        return outOrderFilePath;
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

    public int getMaxIterations() {
        return maxIterations;
    }
}

