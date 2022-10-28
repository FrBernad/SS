package ar.edu.itba.ss.simulator.utils;

import java.io.File;

public class FileGeneratorArgumentsPhase {
    private final File staticFile;
    private final File dynamicFile;
    private final String delimiter;
    private final int N;
    private final int L;
    private final int W;
    private final double A;
    private final double mass;
    private final double r0;
    private final double minTheta;

    private final double maxTheta;
    private final double frequency;

    private final double vx;
    private final double vy;
    private final Long seed;


    public FileGeneratorArgumentsPhase(File staticFile, File dynamicFile, String delimiter, int n, int l, int w, double a, double mass, double r0, double minTheta, double maxTheta, double frequency, double vx, double vy, Long seed) {
        this.staticFile = staticFile;
        this.dynamicFile = dynamicFile;
        this.delimiter = delimiter;
        N = n;
        L = l;
        W = w;
        A = a;
        this.mass = mass;
        this.r0 = r0;
        this.minTheta = minTheta;
        this.maxTheta = maxTheta;
        this.frequency = frequency;
        this.vx = vx;
        this.vy = vy;
        this.seed = seed;
    }

    public File getStaticFile() {
        return staticFile;
    }

    public File getDynamicFile() {
        return dynamicFile;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public int getN() {
        return N;
    }

    public int getL() {
        return L;
    }

    public int getW() {
        return W;
    }

    public double getA() {
        return A;
    }

    public double getMass() {
        return mass;
    }

    public double getR0() {
        return r0;
    }

    public double getMinTheta() {
        return minTheta;
    }

    public double getMaxTheta() {
        return maxTheta;
    }

    public double getFrequency() {
        return frequency;
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
}

