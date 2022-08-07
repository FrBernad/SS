package ar.edu.itba.ss.simulator;

import ar.edu.itba.ss.simulator.methods.CellIndexMethod;
import ar.edu.itba.ss.simulator.methods.CellIndexMethod.CellIndexMethodResults;
import ar.edu.itba.ss.simulator.utils.ActionLogger;
import ar.edu.itba.ss.simulator.utils.BaseArguments;
import ar.edu.itba.ss.simulator.utils.ExecutionTimestamps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Properties;

import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrDefault;
import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrFail;
import static ar.edu.itba.ss.simulator.utils.ParseUtils.ParticlesParserResult;
import static ar.edu.itba.ss.simulator.utils.ParseUtils.parseParticlesList;

public class Simulator {
    private static final Logger LOGGER = LoggerFactory.getLogger(Simulator.class);

    private static final String STATIC_FILE_PATH_P = "staticFile";
    private static final String DYNAMIC_FILE_PATH_P = "dynamicFile";
    private static final String NEIGHBORS_OUT_PATH_P = "neighborsFile";
    private static final String TIME_OUT_PATH_P = "timeFile";

    private static final String PERIODIC_CONDITION_P = "periodic";
    private static final String RADIUS_P = "radius";
    private static final String M_P = "M";
    private static final String DELIMITER_P = "delimiter";

    private static final String DEFAULT_DELIMITER = " ";


    public static void main(String[] args) throws IOException {
        LOGGER.info("Simulator Starting ...");

        final BaseArguments baseArguments;

        final Properties properties = System.getProperties();

        try {
            baseArguments = getAndParseBaseArguments(properties);
        } catch (IllegalArgumentException e) {
            printClientUsage();
            return;
        }

        final ParticlesParserResult particlesParserResult = parseParticlesList(baseArguments.getStaticFile(),
            baseArguments.getDynamicFile(),
            DEFAULT_DELIMITER);

        if (particlesParserResult.getL() / (double) baseArguments.getM() <= baseArguments.getR()) {
//            #FIXME: NO HAY Q TENER EN CUENTA Ro TAMBN?
            System.out.printf("Invalid value M=%d given R=%f --- (L/M>R)\n", baseArguments.getM(), baseArguments.getR());
            return;
        }

        CellIndexMethodResults methodResults = CellIndexMethod.calculateNeighbors(
            particlesParserResult.getParticlesPerTime().get(0),
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

        final String outNeighborsFilePath = getPropertyOrFail(properties, NEIGHBORS_OUT_PATH_P);
        final String outTimeFilePath = getPropertyOrFail(properties, TIME_OUT_PATH_P);
        final String delimiter = getPropertyOrDefault(properties, DELIMITER_P, DEFAULT_DELIMITER);

        final Boolean isPeriodic = getPropertyOrDefault(properties, PERIODIC_CONDITION_P, "false").equals("");
        final double radius = Double.parseDouble(getPropertyOrFail(properties, RADIUS_P));
        final int M = Integer.parseInt(getPropertyOrFail(properties, M_P));

        final File staticFile = Paths.get(staticFilePath).toFile();
        final File dynamicFile = Paths.get(dynamicFilePath).toFile();

        final File outNeighborsFile = new File(outNeighborsFilePath);
        final File outTimeFile = new File(outTimeFilePath);

        return new BaseArguments(staticFile, dynamicFile, outNeighborsFile, outTimeFile, isPeriodic, delimiter, radius, M);
    }

    private static void printClientUsage() {
        System.out.println("Invalid simulator invocation.\n" +
            "Usage: ./simulator -DstaticFile='path/to/static/file' -DdynamicFile='path/to/dynamic/file' " +
            "[-Dperiodic] -Dradius=radius -DM=M");
    }
}
