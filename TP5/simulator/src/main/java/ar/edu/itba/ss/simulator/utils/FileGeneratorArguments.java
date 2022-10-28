package ar.edu.itba.ss.simulator.utils;

import java.io.File;

public class FileGeneratorArguments {
    private final File staticFile;
    private final File dynamicFile;
    private final String delimiter;
    private final int N;
    private final double L;
    private final double W;
    private final double mass;
    private final double r0;
    private final double dr;
    private final double vx;
    private final double vy;


    public FileGeneratorArguments(File staticFile, File dynamicFile, String delimiter, int n, double l, double w, double mass, double r0, double dr, double vx, double vy) {
        this.staticFile = staticFile;
        this.dynamicFile = dynamicFile;
        this.delimiter = delimiter;
        N = n;
        L = l;
        W = w;
        this.mass = mass;
        this.r0 = r0;
        this.dr = dr;
        this.vx = vx;
        this.vy = vy;
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

    public double getL() {
        return L;
    }

    public double getW() {
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
}

