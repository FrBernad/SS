package ar.edu.itba.ss.simulator.utils;

import java.io.File;

public class FileGeneratorArguments {
    private final File staticFile;
    private final File dynamicFile;
    private final int N;
    private final String delimiter;

    private final int particleMinSpeed;
    private final int particleMaxSpeed;


    public FileGeneratorArguments(File staticFile, File dynamicFile, int n, String delimiter, int particleMinSpeed, int particleMaxSpeed) {
        this.staticFile = staticFile;
        this.dynamicFile = dynamicFile;
        this.N = n;
        this.delimiter = delimiter;
        this.particleMinSpeed = particleMinSpeed;
        this.particleMaxSpeed = particleMaxSpeed;
    }

    public File getStaticFile() {
        return staticFile;
    }

    public File getDynamicFile() {
        return dynamicFile;
    }

    public int getN() {
        return N;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public int getParticleMinSpeed() {
        return particleMinSpeed;
    }

    public int getParticleMaxSpeed() {
        return particleMaxSpeed;
    }
}

