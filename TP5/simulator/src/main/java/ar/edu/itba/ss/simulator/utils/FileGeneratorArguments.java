package ar.edu.itba.ss.simulator.utils;

import java.io.File;

public class FileGeneratorArguments {
    private final File staticFile;
    private final File dynamicFile;
    private final String delimiter;
    private final int N;
    private final int L;
    private final int W;
    private final double mass;
    private final double r0;
    private final double dr;
    private final double vx;
    private final double vy;
    private final Long seed;


    public FileGeneratorArguments(File staticFile, File dynamicFile, String delimiter, int N, int L, int W, double mass,
                                  double r0, double dr, double vx, double vy, Long seed) {
        this.staticFile = staticFile;
        this.dynamicFile = dynamicFile;
        this.delimiter = delimiter;
        this.N = N;
        this.L = L;
        this.W = W;
        this.mass = mass;
        this.r0 = r0;
        this.dr = dr;
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

    public double getMass() {
        return mass;
    }

    public double getR0() {
        return r0;
    }

    public double getDr() {
        return dr;
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

