package ar.edu.itba.ss.simulator;

import ar.edu.itba.ss.simulator.simulation.VibratedSilo;
import ar.edu.itba.ss.simulator.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrDefault;
import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrFail;
import static ar.edu.itba.ss.simulator.utils.ParseUtils.ParticlesParserResult;
import static ar.edu.itba.ss.simulator.utils.ParseUtils.parseParticlesList;
import static ar.edu.itba.ss.simulator.utils.R.values.*;
import static java.lang.Double.parseDouble;

public class Simulator {
    private static final Logger LOGGER = LoggerFactory.getLogger(Simulator.class);
    private static final String STATIC_FILE_PATH_P = "staticFile";
    private static final String DYNAMIC_FILE_PATH_P = "dynamicFile";
    private static final String TIME_OUT_PATH_P = "timeFile";
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

        LOGGER.info("Executing Simulator ...");
        final AlgorithmResults methodResults = VibratedSilo.execute(
            particlesParserResult.getParticlesPerTime().get(0),
            L, W, baseArguments.getD(), EXIT_DISTANCE, REENTER_MIN_HEIGHT, REENTER_MAX_HEIGHT,
            KN, KT, baseArguments.getW(), A,
            baseArguments.getDt(), baseArguments.getMaxTime()
        );

        LOGGER.info(String.format("Finished Simulation In %d Iterations", methodResults.getIterations()));

        LOGGER.info("Writing Results ...");
        final File outResultsFile = new File(baseArguments.getOutResultsFilePath());

        int index = 0;
        double step = 500;

        try (PrintWriter pw = new PrintWriter(outResultsFile)) {
            for (Map.Entry<Double, Map<Particle, R>> entry : methodResults.getParticlesStates().entrySet()) {
                Double time = entry.getKey();
                Map<Particle, R> states = entry.getValue();
                if (index % step == 0) {
                    pw.append(String.format("%f\n", time));
                    states.forEach((particle, state) ->
                        pw.printf("%d %.16f %.16f %.16f %.16f\n",
                            particle.getId(),
                            state.get(R0.ordinal()).getX(), state.get(R0.ordinal()).getY(),
                            state.get(R1.ordinal()).getX(), state.get(R1.ordinal()).getY()));
                }
                index++;
            }
        }

        final File outTimeFile = new File(baseArguments.getOutTimeFilePath());
        try (PrintWriter pw = new PrintWriter(outTimeFile)) {
            ActionLogger.logTimestamps(pw, methodResults.getExecutionTimestamps());
        }

        LOGGER.info("Done!");

    }

    private static BaseArguments getAndParseBaseArguments(final Properties properties) throws
        IllegalArgumentException {

        final String staticFilePath = getPropertyOrFail(properties, STATIC_FILE_PATH_P);
        final String dynamicFilePath = getPropertyOrFail(properties, DYNAMIC_FILE_PATH_P);
        final String outResultsFile = getPropertyOrFail(properties, RESULTS_OUT_PATH_P);
        final String timeFilePath = getPropertyOrFail(properties, TIME_OUT_PATH_P);
        final String delimiter = getPropertyOrDefault(properties, DELIMITER_P, DEFAULT_DELIMITER);

        final double dt = parseDouble(getPropertyOrFail(properties, DT_P));
        final double tf = parseDouble(getPropertyOrDefault(properties, TF_P, "5"));
        final double D = parseDouble(getPropertyOrFail(properties, D_P));
        final double w = parseDouble(getPropertyOrFail(properties, w_P));

        final File staticFile = Paths.get(staticFilePath).toFile();
        final File dynamicFile = Paths.get(dynamicFilePath).toFile();

        return new BaseArguments(staticFile, dynamicFile, outResultsFile, timeFilePath, delimiter, tf, dt, w, D);
    }

    private static void printClientUsage() {
        System.out.println("Invalid simulator invocation.\n" +
            "Usage: ./simulator -DstaticFile='path/to/static/file' -DdynamicFile='path/to/dynamic/file' " +
            "-DresultsFile='path/to/results/file' -DtimeFile='path/to/time/file' -Ddt=dt -Dtf=tf -DD=D -Dw=w"
        );
    }
}
