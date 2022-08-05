package ar.edu.itba.ss.simulator.utils;

import java.io.File;

public class BaseArguments {

    private final File staticFile;
    private final File dynamicFile;
    private final File outNeighborsFile;
    private final File outTimeFile;
    private final Boolean isPeriodic;

    private final double R;

    private final int M;

    public BaseArguments(File staticFile, File dynamicFile, File outNeighborsFile, File outTimeFile, Boolean isPeriodic, double r, int m) {
        this.staticFile = staticFile;
        this.dynamicFile = dynamicFile;
        this.outNeighborsFile = outNeighborsFile;
        this.outTimeFile = outTimeFile;
        this.isPeriodic = isPeriodic;
        this.R = r;
        this.M = m;
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

    public double getR() {
        return R;
    }

    public int getM() {
        return M;
    }
}

