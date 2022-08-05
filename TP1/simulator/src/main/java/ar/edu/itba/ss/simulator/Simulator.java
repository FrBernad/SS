package ar.edu.itba.ss.simulator;

import ar.edu.itba.ss.simulator.methods.CellIndexMethod;
import ar.edu.itba.ss.simulator.methods.CellIndexMethod.CellIndexMethodResults;
import ar.edu.itba.ss.simulator.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.Properties;

import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrDefault;
import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrFail;
import static ar.edu.itba.ss.simulator.utils.ParseUtils.*;
import static ar.edu.itba.ss.simulator.utils.ParseUtils.parseParticlesList;

public class Simulator {
    private static final Logger LOGGER = LoggerFactory.getLogger(Simulator.class);

    private static final String STATIC_FILE_PATH_P = "staticFile";
    private static final String DYNAMIC_FILE_PATH_P = "dynamicFile";

    private static final String PERIODIC_CONDITION_P = "periodic";
    private static final String RADIUS_P = "radius";
    private static final String M_P = "M";


    public static void main(String[] args) throws IOException {
        LOGGER.info("Simulator Starting ...");

        BaseArguments baseArguments;

        final Properties properties = System.getProperties();

        try {
            baseArguments = getAndParseBaseArguments(properties);
        } catch (IllegalArgumentException e) {
            printClientUsage();
            return;
        }

        final ParticlesParserResult particlesParserResult = parseParticlesList(baseArguments.getStaticFile(), baseArguments.getDynamicFile());
        CellIndexMethodResults methodResults = CellIndexMethod.calculateNeighbors(
            particlesParserResult.getParticles(),
            particlesParserResult.getN(),
            particlesParserResult.getL(),
            baseArguments.getM(),
            baseArguments.getR(),
            baseArguments.getPeriodic()
        );

        try (PrintWriter pw = new PrintWriter(baseArguments.getOutNeighborsFile())) {
            methodResults.getNeighbors().forEach(pw::println);
        }

        try (PrintWriter pw = new PrintWriter(baseArguments.getOutTimeFile())) {
            ActionLogger.logTimestamps(pw, methodResults.getExecutionTimestamps());
        }

    }

    private static BaseArguments getAndParseBaseArguments(final Properties properties) throws IllegalArgumentException {
        final String staticFilePath = getPropertyOrFail(properties, STATIC_FILE_PATH_P);
        final String dynamicFilePath = getPropertyOrFail(properties, DYNAMIC_FILE_PATH_P);

        final String outNeighborsFilePath = getPropertyOrFail(properties, DYNAMIC_FILE_PATH_P);
        final String outTimeFilePath = getPropertyOrFail(properties, DYNAMIC_FILE_PATH_P);

        final Boolean isPeriodic = Boolean.valueOf(getPropertyOrDefault(properties, PERIODIC_CONDITION_P, "false"));
        final double radius = Double.parseDouble(getPropertyOrFail(properties, RADIUS_P));
        final int M = Integer.parseInt(getPropertyOrFail(properties, M_P));

        final File staticFile = Paths.get(staticFilePath).toFile();
        final File dynamicFile = Paths.get(dynamicFilePath).toFile();

        final File outNeighborsFile = new File(outNeighborsFilePath);
        final File outTimeFile = new File(outTimeFilePath);

        return new BaseArguments(staticFile, dynamicFile, outNeighborsFile, outTimeFile, isPeriodic, radius, M);
    }

    //FIXME:!!!
    private static void printClientUsage() {
        System.out.println("Invalid simulator invocation.\n" +
            "Usage: ./simulator -DstaticFile='path/to/static/file' -DdynamicFile='path/to/dynamic/file' " +
            "-Dperiodic -Dradius=radius -DM=M");
    }
}
