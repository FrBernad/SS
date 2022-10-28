package ar.edu.itba.ss.simulator.utils;

import java.io.File;

public class BaseArguments {

    private final File staticFile;
    private final File dynamicFile;
    private final String outResultsFile;
    private final String outExitTimeFile;
    private final String delimiter;
    private final double L;
    private final double W;
    private final double dt;
    private final double dt2;
    private final double tf;
    private final double vx;
    private final double vy;
    private final double w;
    private final double D;
    private final double kn;
    private final double kt;
    private final double A;
    private final double exitDistance;
    private final double renterMinHeight;
    private final double renterMaxHeight;
    private final double gravity;


    public BaseArguments(File staticFile, File dynamicFile, String outResultsFile, String outExitTimeFile, String delimiter, double L, double W, double dt, double dt2, double tf, double vx, double vy, double w, double d, double kn, double kt, double a, double exitDistance, double renterMinHeight, double renterMaxHeight, double gravity) {
        this.staticFile = staticFile;
        this.dynamicFile = dynamicFile;
        this.outResultsFile = outResultsFile;
        this.outExitTimeFile = outExitTimeFile;
        this.delimiter = delimiter;
        this.L = L;
        this.W = w;
        this.dt = dt;
        this.dt2 = dt2;
        this.tf = tf;
        this.vx = vx;
        this.vy = vy;
        this.w = w;
        this.D = d;
        this.kn = kn;
        this.kt = kt;
        this.A = a;
        this.exitDistance = exitDistance;
        this.renterMinHeight = renterMinHeight;
        this.renterMaxHeight = renterMaxHeight;
        this.gravity = gravity;
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

    public double getL() {
        return L;
    }

    public double getW() {
        return W;
    }

    public double getD() {
        return D;
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

    public double getRenterMinHeight() {
        return renterMinHeight;
    }

    public double getRenterMaxHeight() {
        return renterMaxHeight;
    }

    public double getGravity() {
        return gravity;
    }

    public double getDt() {
        return dt;
    }

    public double getDt2() {
        return dt2;
    }

    public double getTf() {
        return tf;
    }

    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
    }
}


