package ar.edu.itba.ss.simulator.utils;

import java.io.File;

public class FileGeneratorArguments {
    private final File staticFile;
    private final File dynamicFile;
    private final int N;
    private final String delimiter;

    public FileGeneratorArguments(File staticFile, File dynamicFile, int n, String delimiter) {
        this.staticFile = staticFile;
        this.dynamicFile = dynamicFile;
        this.N = n;
        this.delimiter = delimiter;
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
}

