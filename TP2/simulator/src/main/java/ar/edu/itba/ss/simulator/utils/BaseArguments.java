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
    private final int M;
    private final double eta;
    private final double dt;
    private final double threshold;

    public BaseArguments(File staticFile, File dynamicFile, File outNeighborsFile, File outTimeFile, Boolean isPeriodic, String delimiter, double r, int m, double eta, double dt, double threshold) {
        this.staticFile = staticFile;
        this.dynamicFile = dynamicFile;
        this.outNeighborsFile = outNeighborsFile;
        this.outTimeFile = outTimeFile;
        this.isPeriodic = isPeriodic;
        this.delimiter = delimiter;
        R = r;
        M = m;
        this.eta = eta;
        this.dt = dt;
        this.threshold = threshold;
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

    public int getM() {
        return M;
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
}

