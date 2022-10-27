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
import java.util.List;
import java.util.Properties;

import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrDefault;
import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrFail;
import static ar.edu.itba.ss.simulator.utils.ParseUtils.ParticlesParserResult;
import static ar.edu.itba.ss.simulator.utils.ParseUtils.parseParticlesList;
import static java.lang.Double.parseDouble;

public class MultipleFrequencySimulator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MultipleFrequencySimulator.class);
    private static final String RESULTS_DIR_PATH_P = "resultsDir";
    private static final String STATIC_FILE_PATH_P = "staticFile";
    private static final String DYNAMIC_FILE_PATH_P = "dynamicFile";
    private static final String DELIMITER_P = "delimiter";
    private static final String DEFAULT_DELIMITER = " ";
    private static final String D_P = "D";
    private static final List<Integer> FREQUENCIES = List.of(5, 10, 15, 20, 30, 50);
    private static final int L = 70;
    private static final int W = 20;
    private static final double EXIT_DISTANCE = L / 10.0;
    private static final double REENTER_MIN_HEIGHT = 40;
    private static final double REENTER_MAX_HEIGHT = 70;
    private static final double KN = 250;
    private static final double KT = 2 * KN;
    private static final double A = 0.15;

    public static void main(String[] args) throws IOException {
        LOGGER.info("MultipleFrequencySimulator Starting ...");

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

        double secondsStep = 0.1;
        double printStep = secondsStep / baseArguments.getDt();

        for (Integer w : FREQUENCIES) {
            final String outResultsFilePath = String.format("%s/results%d", baseArguments.getOutResultsFilePath(), w);
            final String outExitTimeFilePath = String.format("%s/exitTime%d", baseArguments.getOutResultsFilePath(), w);

            final File outResultsFile = new File(outResultsFilePath);
            final File outExitTimeFile = new File(outExitTimeFilePath);

            PrintWriter resultsWriter = new PrintWriter(outResultsFile);
            PrintWriter exitTimeWriter = new PrintWriter(outExitTimeFile);

            System.out.println();
            LOGGER.info(String.format("Executing Simulator with %d particles - Frequency: %d", particlesParserResult.getN(), w));
            LOGGER.info(String.format("Writing Results every %.2f seconds", printStep * baseArguments.getDt()));

            final AlgorithmResults methodResults = VibratedSilo.execute(
                particlesParserResult.getParticlesPerTime().get(0),
                L, W, baseArguments.getD(),
                EXIT_DISTANCE, REENTER_MIN_HEIGHT, REENTER_MAX_HEIGHT,
                KN, KT, w, A,
                baseArguments.getDt(), baseArguments.getMaxTime(),
                printStep, resultsWriter, exitTimeWriter
            );

            resultsWriter.close();
            exitTimeWriter.close();
            LOGGER.info(String.format("Finished Simulation In %d Iterations", methodResults.getIterations()));

        }

        LOGGER.info("Done!");
    }

    private static BaseArguments getAndParseBaseArguments(final Properties properties) throws IllegalArgumentException {

        final String outResultsDir = getPropertyOrFail(properties, RESULTS_DIR_PATH_P);
        final String delimiter = getPropertyOrDefault(properties, DELIMITER_P, DEFAULT_DELIMITER);
        final String staticFilePath = getPropertyOrFail(properties, STATIC_FILE_PATH_P);
        final String dynamicFilePath = getPropertyOrFail(properties, DYNAMIC_FILE_PATH_P);

        final double dt = 0.001;
        final double tf = 1000;
        final double D = parseDouble(getPropertyOrDefault(properties, D_P, "3"));

        final File staticFile = Paths.get(staticFilePath).toFile();
        final File dynamicFile = Paths.get(dynamicFilePath).toFile();

        return new BaseArguments(staticFile, dynamicFile, outResultsDir, null, delimiter, tf, dt, 0, D);
    }

    private static void printClientUsage() {
        System.out.println("Invalid simulator invocation.\n" +
            "Usage: ./simulator -DstaticFile='path/to/static/file' -DdynamicFile='path/to/dynamic/file' -DresultsDir='path/to/results/dir' -Ddt=dt -Dtf=tf -DD=D"
        );
    }
}
