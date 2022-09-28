package ar.edu.itba.ss.simulator;

import ar.edu.itba.ss.simulator.algorithms.Beeman;
import ar.edu.itba.ss.simulator.algorithms.GearPredictor;
import ar.edu.itba.ss.simulator.algorithms.VerletOriginal;
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
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.Math.pow;

public class SimulatorPlanets {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimulatorPlanets.class);
    private static final String STATIC_FILE_PATH_P = "staticFile";
    private static final String DYNAMIC_FILE_PATH_P = "dynamicFile";
    private static final String RESULTS_OUT_PATH_P = "resultsFile";
    private static final String TIME_OUT_PATH_P = "timeFile";
    private static final String MAX_ITERATIONS_P = "maxIterations";
    private static final String ALGORITHM_P = "algorithm";
    private static final String DT_P = "dt";
    private static final String TF_P = "tf";
    private static final double DISTANCE_TO_SPACESHIP = 1500;
    private static final Double G = 6.693 * pow(10, -11);

    private static final String DELIMITER_P = "delimiter";
    private static final String DEFAULT_DELIMITER = " ";

    public static void main(String[] args) throws IOException {
        LOGGER.info("SimulatorPlanets Starting ...");

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
            baseArguments.getDelimiter());

        double sunx = 0.0;
        double suny = 0.0;
        double earthx = Double.parseDouble("1.501409394622880E+08");
        double earthy = Double.parseDouble("-9.238096308876731E+0");

        double d = Math.sqrt((Math.pow((sunx - earthx), 2) + Math.pow((suny - earthy), 2)));
        double spaceshipx = DISTANCE_TO_SPACESHIP * (sunx - earthx) / d + earthx;
        double spaceshipy = DISTANCE_TO_SPACESHIP * (suny - earthy) / d + earthy;

        LOGGER.info("Executing Venus Mision ...");

        AlgorithmResults methodResults;

//        switch (baseArguments.getAlgorithm()) {
//            case GEAR_PREDICTOR:
//                methodResults = GearPredictor.execute(
//                    oscillatorParticle,
//                    new Particle.State(new Particle.Position(X_0, 0), V_0, 0),
//                    K,
//                    GAMMA,
//                    baseArguments.getDt(),
//                    baseArguments.getMaxTime()
//                );
//                break;
//            case BEEMAN:
//                methodResults = Beeman.execute(
//                    oscillatorParticle,
//                    new Particle.State(new Particle.Position(X_0, 0), V_0, 0),
//                    K,
//                    GAMMA,
//                    baseArguments.getDt(),
//                    baseArguments.getMaxTime()
//                );
//                break;
//            case VERLET_ORIGINAL:
//                methodResults = VerletOriginal.execute(
//                    oscillatorParticle,
//                    new Particle.State(new Particle.Position(X_0, 0), V_0, 0),
//                    K,
//                    GAMMA,
//                    baseArguments.getDt(),
//                    baseArguments.getMaxTime()
//                );
//                break;
//            default:
//                System.out.println("Invalid algorithm");
//                return;
//        }
//
//
//        LOGGER.info(String.format("Finished Venus Mision In %d Iterations",
//            methodResults.getIterations()));
//        LOGGER.info("Writing Results ...");
//        final File outResultsFile = new File(baseArguments.getOutResultsFilePath());
//        try (PrintWriter pw = new PrintWriter(outResultsFile)) {
//            methodResults.getParticlesStates()
//                .forEach((time, states) -> {
//                    pw.append(String.format("%f\n", time));
//                    states.forEach((particle, state) ->
//                        pw.printf("%d %f %f %f %f\n",
//                            particle.getId(),
//                            state.getPosition().getX(), state.getPosition().getY(),
//                            state.getVelocityX(), state.getVelocityY()));
//                });
//        }
//
//        final File outTimeFile = new File(baseArguments.getOutTimeFilePath());
//        try (PrintWriter pw = new PrintWriter(outTimeFile)) {
//            ActionLogger.logTimestamps(pw, methodResults.getExecutionTimestamps());
//        }
//
//        LOGGER.info("Done!");

    }

    private static BaseArguments getAndParseBaseArguments(final Properties properties) throws IllegalArgumentException {
        final String staticFilePath = getPropertyOrFail(properties, STATIC_FILE_PATH_P);
        final String dynamicFilePath = getPropertyOrFail(properties, DYNAMIC_FILE_PATH_P);

        final String outResultsFile = getPropertyOrFail(properties, RESULTS_OUT_PATH_P);
        final String timeFilePath = getPropertyOrFail(properties, TIME_OUT_PATH_P);
        final String delimiter = getPropertyOrDefault(properties, DELIMITER_P, DEFAULT_DELIMITER);

        final double dt = parseDouble(getPropertyOrFail(properties, DT_P));
        final double tf = parseDouble(getPropertyOrDefault(properties, TF_P, "5"));
        final Algorithm algorithm = Algorithm.valueOf(getPropertyOrDefault(properties, ALGORITHM_P, "GEAR_PREDICTOR"));

        final File staticFile = Paths.get(staticFilePath).toFile();
        final File dynamicFile = Paths.get(dynamicFilePath).toFile();

        return new BaseArguments(staticFile, dynamicFile, outResultsFile, timeFilePath, delimiter, tf, dt, algorithm);
    }

    private static void printClientUsage() {
        System.out.println("Invalid simulator invocation.\n" +
            "Usage: ./simulator -DstaticFile='path/to/static/file' -DdynamicFile='path/to/dynamic/file' " +
            " -DresultsFile=resultsFile -DtimeFile=timeFile [-Ddelimiter=delimiter] -Ddt=dt -Dtf=tf -Dalgorithm=algorithm "
        );
    }
}
