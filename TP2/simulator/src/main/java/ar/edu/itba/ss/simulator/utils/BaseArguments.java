package ar.edu.itba.ss.simulator.utils;

import java.io.File;

public class BaseArguments {

    private final File staticFile;
    private final File dynamicFile;
    private final File outNeighborsFile;
    private final File outTimeFile;
    private final Boolean isPeriodic;
    private final String delimiter;
    private final double R;
    private final double eta;
    private final double dt;
    private final double threshold;

    private final int maxHits;

    public BaseArguments(File staticFile, File dynamicFile, File outNeighborsFile, File outTimeFile, Boolean isPeriodic, String delimiter, double r, double eta, double dt, double threshold, int maxHits) {
        this.staticFile = staticFile;
        this.dynamicFile = dynamicFile;
        this.outNeighborsFile = outNeighborsFile;
        this.outTimeFile = outTimeFile;
        this.isPeriodic = isPeriodic;
        this.delimiter = delimiter;
        this.R = r;
        this.eta = eta;
        this.dt = dt;
        this.threshold = threshold;
        this.maxHits = maxHits;
    }

    public File getStaticFile() {
        return staticFile;
    }

    public File getDynamicFile() {
        return dynamicFile;
    }

    public File getOutNeighborsFile() {
        return outNeighborsFile;
    }

    public File getOutTimeFile() {
        return outTimeFile;
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

    public int getMaxHits() {
        return maxHits;
    }
}

