package ar.edu.itba.ss.simulator.utils;

import java.io.File;

public class BaseArgumentsWithPhase {

    private final File staticFile;
    private final File dynamicFile;
    private final String outResultsFile;
    private final String outExitTimeFile;
    private final String delimiter;
    private final int L;
    private final int W;
    private final int D;
    private final double frequency;
    private final double kn;
    private final double kt;
    private final double A;
    private final double exitDistance;
    private final double reenterMinHeight;
    private final double reenterMaxHeight;
    private final double dt;
    private final double dt2;
    private final double tf;
    private final double vx;
    private final double vy;
    private final Long seed;
    private final double r0;
    private final double vd;
    private final double tau;

    public BaseArgumentsWithPhase(File staticFile, File dynamicFile, String outResultsFile, String outExitTimeFile,
                                  String delimiter, int L, int W, int D, double frequency, double kn, double kt, double A,
                                  double exitDistance, double reenterMinHeight, double reenterMaxHeight,
                                  double dt, double dt2, double tf, double vx, double vy, Long seed, double r0, double vd, double tau) {
        this.staticFile = staticFile;
        this.dynamicFile = dynamicFile;
        this.outResultsFile = outResultsFile;
        this.outExitTimeFile = outExitTimeFile;
        this.delimiter = delimiter;
        this.L = L;
        this.W = W;
        this.D = D;
        this.frequency = frequency;
        this.kn = kn;
        this.kt = kt;
        this.A = A;
        this.exitDistance = exitDistance;
        this.reenterMinHeight = reenterMinHeight;
        this.reenterMaxHeight = reenterMaxHeight;
        this.dt = dt;
        this.dt2 = dt2;
        this.tf = tf;
        this.vx = vx;
        this.vy = vy;
        this.seed = seed;
        this.r0 = r0;
        this.vd = vd;
        this.tau = tau;
    }

    public File getStaticFile() {
        return staticFile;
    }

    public File getDynamicFile() {
        return dynamicFile;
    }

    public String getOutResultsFile() {
        return outResultsFile;
    }

    public String getOutExitTimeFile() {
        return outExitTimeFile;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public int getL() {
        return L;
    }

    public int getW() {
        return W;
    }

    public int getD() {
        return D;
    }

    public double getFrequency() {
        return frequency;
    }

    public double getKn() {
        return kn;
    }

    public double getKt() {
        return kt;
    }

    public double getA() {
        return A;
    }

    public double getExitDistance() {
        return exitDistance;
    }

    public double getReenterMinHeight() {
        return reenterMinHeight;
    }

    public double getReenterMaxHeight() {
        return reenterMaxHeight;
    }

    public double getDt() {
        return dt;
    }

    public double getDt2() {
        return dt2;
    }

    public double getMaxTime() {
        return tf;
    }

    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
    }

    public Long getSeed() {
        return seed;
    }

    public double getR0() {
        return r0;
    }

    public double getVd() {
        return vd;
    }

    public double getTau() {
        return tau;
    }


}


