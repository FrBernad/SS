package ar.edu.itba.ss.simulator;

import ar.edu.itba.ss.simulator.algorithms.vibratedsilo.VibratedSilo;
import ar.edu.itba.ss.simulator.utils.*;
import ar.edu.itba.ss.simulator.utils.Particle.Position;
import ar.edu.itba.ss.simulator.utils.Particle.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrDefault;
import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrFail;
import static ar.edu.itba.ss.simulator.utils.ParseUtils.*;
import static java.lang.Double.parseDouble;
import static java.lang.Math.pow;

public class Simulator {
    private static final Logger LOGGER = LoggerFactory.getLogger(Simulator.class);
    // PARAMETERS
    private static final String DT_P = "dt";
    private static final String TF_P = "tf";
    private static final String TIME_OUT_PATH_P = "timeFile";
    private static final String RESULTS_OUT_PATH_P = "resultsFile";

    private static final String W_P = "w";
    private static final String D_P = "D";
    private static final int L = 70;
    private static final double REENTER_HEIGHT = L / 10.0;
    private static final double REENTER_SPEED = 0.0;
    private static final double REENTER_MIN_HEIGHT = 40;
    private static final double REENTER_MAX_HEIGHT = 70;
    private static final double KN = 250;
    private static final double KT = 2 * KN;


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
            L, REENTER_HEIGHT, REENTER_MIN_HEIGHT, REENTER_MAX_HEIGHT,
            KN, KT,
            baseArguments.getw(),
            baseArguments.getDt(),
            baseArguments.getMaxTime()
        );

        LOGGER.info(String.format("Finished Simulation In %d Iterations", methodResults.getIterations()));

        LOGGER.info("Writing Results ...");
        final File outResultsFile = new File(baseArguments.getOutResultsFilePath());
        try (PrintWriter pw = new PrintWriter(outResultsFile)) {
            methodResults.getParticlesStates()
                .forEach((time, states) -> {
                    pw.append(String.format("%f\n", time));
                    states.forEach((particle, state) ->
                        pw.printf("%d %.16f %.16f %.16f %.16f\n",
                            particle.getId(),
                            state.getPosition().getX(), state.getPosition().getY(),
                            state.getVelocityX(), state.getVelocityY()));
                });
        }

        final File outTimeFile = new File(baseArguments.getOutTimeFilePath());
        try (PrintWriter pw = new PrintWriter(outTimeFile)) {
            ActionLogger.logTimestamps(pw, methodResults.getExecutionTimestamps());
        }

        LOGGER.info("Done!");

    }

    private static BaseArguments getAndParseBaseArguments(final Properties properties) throws
        IllegalArgumentException {

        final String outResultsFile = getPropertyOrFail(properties, RESULTS_OUT_PATH_P);
        final double dt = parseDouble(getPropertyOrFail(properties, DT_P));
        final double tf = parseDouble(getPropertyOrDefault(properties, TF_P, "5"));
        final double D = parseDouble(getPropertyOrFail(properties, D_P));
        final double w = parseDouble(getPropertyOrFail(properties, W_P));
        final String timeFilePath = getPropertyOrFail(properties, TIME_OUT_PATH_P);

        return new BaseArguments(null, null, outResultsFile, timeFilePath, null, tf, dt, w, D);
    }

    private static void printClientUsage() {
        System.out.println("Invalid simulator invocation.\n" +
            "Usage: ./simulator -DresultsFile=resultsFile -DtimeFile=timeFile -Ddt=dt -Dtf=tf -DD=D -Dw=w"
        );
    }
}
