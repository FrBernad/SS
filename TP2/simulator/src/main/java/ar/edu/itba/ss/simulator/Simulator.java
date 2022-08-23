package ar.edu.itba.ss.simulator;

import ar.edu.itba.ss.simulator.algorithms.flocks.Flocks;
import ar.edu.itba.ss.simulator.algorithms.flocks.FlocksAlgorithmResults;
import ar.edu.itba.ss.simulator.utils.ActionLogger;
import ar.edu.itba.ss.simulator.utils.BaseArguments;
import ar.edu.itba.ss.simulator.utils.Particle;
import ar.edu.itba.ss.simulator.utils.Particle.State;
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
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class Simulator {
    private static final Logger LOGGER = LoggerFactory.getLogger(Simulator.class);
    private static final String STATIC_FILE_PATH_P = "staticFile";
    private static final String DYNAMIC_FILE_PATH_P = "dynamicFile";
    private static final String RESULTS_OUT_PATH_P = "resultsFile";
    private static final String ORDER_OUT_PATH_P = "orderFile";
    private static final String TIME_OUT_PATH_P = "timeFile";
    private static final String PERIODIC_CONDITION_P = "periodic";
    private static final String MAX_ITERATIONS_P = "maxIterations";
    private static final String DELTA_TIME_P = "dt";
    private static final String ETA_P = "eta";
    private static final String RADIUS_P = "radius";
    private static final String DELIMITER_P = "delimiter";
    private static final String DEFAULT_DELIMITER = " ";

    public static void main(String[] args) throws IOException {
        LOGGER.info("Simulator Starting ...");

        final BaseArguments baseArguments;

        final Properties properties = System.getProperties();

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
            DEFAULT_DELIMITER);

        final double maxRadius = particlesParserResult.getParticlesPerTime()
            .get(0)
            .keySet()
            .stream()
            .map(Particle::getRadius)
            .max(Double::compare).orElseThrow();

        final double gridCondition = particlesParserResult.getL() / baseArguments.getR() + 2 * maxRadius;
        int optimalM = (int) Math.floor(gridCondition);
        if (gridCondition == (int) gridCondition) {
            optimalM = (int) gridCondition - 1;
        }

        LOGGER.info("Executing Flocks algorithm ...");

        FlocksAlgorithmResults methodResults = Flocks.execute(
            particlesParserResult.getParticlesPerTime().get(0),
            particlesParserResult.getN(),
            particlesParserResult.getL(),
            optimalM,
            baseArguments.getR(),
            baseArguments.getDt(),
            baseArguments.getEta(),
            baseArguments.getPeriodic(),
            baseArguments.getMaxIterations()
        );


        LOGGER.info("Writing Results ...");
        final File outFlockFile = new File(baseArguments.getOutFlocksFilePath());
        try (PrintWriter pw = new PrintWriter(outFlockFile)) {
            for (int i = 0; i < methodResults.getParticlesStates().size(); i++) {
                pw.append(String.format("%d\n", i));
                final Map<Particle, State> currentStates = methodResults.getParticlesStates().get(i);
                currentStates.forEach((particle, state) ->
                    pw.printf("%d %f %f %f %f\n",
                        particle.getId(),
                        state.getPosition().getX(), state.getPosition().getY(),
                        state.getSpeed(), state.getAngle())
                );
            }
        }

        final File outOrderFile = new File(baseArguments.getOutOrderFilePath());
        try (PrintWriter pw = new PrintWriter(outOrderFile)) {
            pw.printf("%d ", particlesParserResult.getN());
            pw.printf("%d ", particlesParserResult.getL());
            pw.printf("%f ", baseArguments.getR());
            pw.printf("%f ", baseArguments.getEta());
            pw.printf("%d\n", baseArguments.getMaxIterations());
            methodResults.getOrderParameter().forEach(pw::println);
        }

        final File outTimeFile = new File(baseArguments.getOutTimeFilePath());
        try (PrintWriter pw = new PrintWriter(outTimeFile)) {
            ActionLogger.logTimestamps(pw, methodResults.getExecutionTimestamps());
        }

        LOGGER.info("Done!");

    }

    private static BaseArguments getAndParseBaseArguments(final Properties properties) throws IllegalArgumentException {
        final String staticFilePath = getPropertyOrFail(properties, STATIC_FILE_PATH_P);
        final String dynamicFilePath = getPropertyOrFail(properties, DYNAMIC_FILE_PATH_P);

        final String outFlockFilePath = getPropertyOrFail(properties, RESULTS_OUT_PATH_P);
        final String orderFilePath = getPropertyOrFail(properties, ORDER_OUT_PATH_P);
        final String timeFilePath = getPropertyOrFail(properties, TIME_OUT_PATH_P);
        final String delimiter = getPropertyOrDefault(properties, DELIMITER_P, DEFAULT_DELIMITER);

        final Boolean isPeriodic = !getPropertyOrDefault(properties, PERIODIC_CONDITION_P, "NOT_EMPTY").equals("");
        final double radius = parseDouble(getPropertyOrFail(properties, RADIUS_P));
        final int maxIterations = parseInt(getPropertyOrDefault(properties, MAX_ITERATIONS_P, "3000"));

        final double eta = parseDouble(getPropertyOrDefault(properties, ETA_P, "1"));
        final double dt = Double.parseDouble(getPropertyOrDefault(properties, DELTA_TIME_P, "1"));

        final File staticFile = Paths.get(staticFilePath).toFile();
        final File dynamicFile = Paths.get(dynamicFilePath).toFile();


        return new BaseArguments(staticFile, dynamicFile, outFlockFilePath, timeFilePath, orderFilePath, isPeriodic, delimiter, radius, eta, dt, maxIterations);
    }

    private static void printClientUsage() {
        System.out.println("Invalid simulator invocation.\n" +
            "Usage: ./simulator -DstaticFile='path/to/static/file' -DdynamicFile='path/to/dynamic/file' " +
            "[-Dperiodic] -Dradius=radius -DresultsFile=resultsFile -DtimeFile=timeFile -DorderFile=orderFile [-DmaxIterations=iters] " +
            "[-Deta=eta] [-Ddt=dt]");
    }
}
