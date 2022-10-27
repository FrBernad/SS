package ar.edu.itba.ss.simulator;

import ar.edu.itba.ss.simulator.simulation.VibratedSilo;
import ar.edu.itba.ss.simulator.utils.AlgorithmResults;
import ar.edu.itba.ss.simulator.utils.BaseArguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.Properties;

import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrDefault;
import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrFail;
import static ar.edu.itba.ss.simulator.utils.ParseUtils.ParticlesParserResult;
import static ar.edu.itba.ss.simulator.utils.ParseUtils.parseParticlesList;
import static java.lang.Double.parseDouble;

public class Simulator {
    private static final Logger LOGGER = LoggerFactory.getLogger(Simulator.class);
    private static final String STATIC_FILE_PATH_P = "staticFile";
    private static final String DYNAMIC_FILE_PATH_P = "dynamicFile";
    private static final String EXIT_TIME_PATH_P = "exitTimeFile";
    private static final String RESULTS_OUT_PATH_P = "resultsFile";
    private static final String DELIMITER_P = "delimiter";
    private static final String DEFAULT_DELIMITER = " ";
    private static final String DT_P = "dt";
    private static final String TF_P = "tf";
    private static final String w_P = "w";
    private static final String D_P = "D";
    private static final int L = 70;
    private static final int W = 20;
    private static final double EXIT_DISTANCE = L / 10.0;
    private static final double REENTER_MIN_HEIGHT = 40;
    private static final double REENTER_MAX_HEIGHT = 70;
    private static final double KN = 250;
    private static final double KT = 2 * KN;
    private static final double A = 0.15;

    public static void main(String[] args) throws IOException {
        LOGGER.info("Simulator Starting ...");

        final Properties properties = System.getProperties();
        final BaseArguments baseArguments;

        try {
            LOGGER.info("Parsing Arguments ...");
            baseArguments = getAndParseBaseArguments(properties);
        } catch (IllegalArgumentException e) {
            printClientUsage();
            return;
        }

        LOGGER.info("Parsing Particles ...");
        final ParticlesParserResult particlesParserResult = parseParticlesList(baseArguments.getStaticFile(),
            baseArguments.getDynamicFile(),
            baseArguments.getDelimiter());

        final File outResultsFile = new File(baseArguments.getOutResultsFilePath());
        final File outExitTimeFile = new File(baseArguments.getOutExitTimeFile());

        double secondsStep = 0.1;
        double printStep = secondsStep / baseArguments.getDt();

        PrintWriter resultsWriter = new PrintWriter(outResultsFile);
        PrintWriter exitTimeWriter = new PrintWriter(outExitTimeFile);

        LOGGER.info(String.format("Executing Simulator with %d particles / w = %f / d = %f", particlesParserResult.getN(),
            baseArguments.getW(), baseArguments.getD()));
        LOGGER.info(String.format("Writing Results every %.2f seconds", printStep * baseArguments.getDt()));

        final AlgorithmResults methodResults = VibratedSilo.execute(
            particlesParserResult.getParticlesPerTime().get(0),
            L, W, baseArguments.getD(),
            EXIT_DISTANCE, REENTER_MIN_HEIGHT, REENTER_MAX_HEIGHT,
            KN, KT, baseArguments.getW(), A,
            baseArguments.getDt(), baseArguments.getMaxTime(),
            printStep, resultsWriter, exitTimeWriter
        );

        resultsWriter.close();
        exitTimeWriter.close();

        LOGGER.info(String.format("Finished Simulation In %d Iterations", methodResults.getIterations()));

        LOGGER.info("Done!");
    }

    private static BaseArguments getAndParseBaseArguments(final Properties properties) throws IllegalArgumentException {

        final String staticFilePath = getPropertyOrFail(properties, STATIC_FILE_PATH_P);
        final String dynamicFilePath = getPropertyOrFail(properties, DYNAMIC_FILE_PATH_P);
        final String outResultsFile = getPropertyOrFail(properties, RESULTS_OUT_PATH_P);
        final String outExitTimeFile = getPropertyOrFail(properties, EXIT_TIME_PATH_P);
        final String delimiter = getPropertyOrDefault(properties, DELIMITER_P, DEFAULT_DELIMITER);

        final double dt = parseDouble(getPropertyOrFail(properties, DT_P));
        final double tf = parseDouble(getPropertyOrFail(properties, TF_P));
        final double D = parseDouble(getPropertyOrFail(properties, D_P));
        final double w = parseDouble(getPropertyOrFail(properties, w_P));

        final File staticFile = Paths.get(staticFilePath).toFile();
        final File dynamicFile = Paths.get(dynamicFilePath).toFile();

        return new BaseArguments(staticFile, dynamicFile, outResultsFile, outExitTimeFile, delimiter, tf, dt, w, D);
    }

    private static void printClientUsage() {
        System.out.println("Invalid simulator invocation.\n" +
            "Usage: ./simulator -DstaticFile='path/to/static/file' -DdynamicFile='path/to/dynamic/file' " +
            "-DresultsFile='path/to/results/file' -DexitTimeFile='path/to/exitTime/file' -Ddt=dt -Dtf=tf -DD=D -Dw=w"
        );
    }
}
