package ar.edu.itba.ss.simulator;

import ar.edu.itba.ss.simulator.simulation.SpaceMission;
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
import static ar.edu.itba.ss.simulator.utils.ParseUtils.ParticlesParserResult;
import static ar.edu.itba.ss.simulator.utils.ParseUtils.parseParticlesList;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class SimulatorPlanets {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimulatorPlanets.class);
    private static final String STATIC_FILE_PATH_P = "staticFile";
    private static final String DYNAMIC_FILE_PATH_P = "dynamicFile";
    private static final String RESULTS_OUT_PATH_P = "resultsFile";
    private static final String TIME_OUT_PATH_P = "timeFile";
    private static final String ALGORITHM_P = "algorithm";
    private static final String DT_P = "dt";
    private static final String TF_P = "tf";
    private static final double DISTANCE_TO_SPACESHIP = 1500;
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
        double earthy = Double.parseDouble("-9.238096308876731E+05");
        double earthvx = Double.parseDouble("-2.949925999285836E-01");
        double earthvy = Double.parseDouble("2.968579130065282E+01");
        double earthR = Double.parseDouble("6371.01");

        //https://math.stackexchange.com/questions/2045174/how-to-find-a-point-between-two-points-with-given-distance
        double d = sqrt((pow((earthx - sunx), 2) + pow((earthy - suny), 2)));
        // Componentes del versor que une el sol con la tierra (normal a la orbita)
        double rx = (earthx - sunx) / d;
        double ry = (earthy - suny) / d;

        // Componentes del versor tangencial a la orbita
        double ox = -ry;
        double oy = rx;

        //Position
        double spaceshipx = DISTANCE_TO_SPACESHIP * -rx + earthx + earthR;
        double spaceshipy = DISTANCE_TO_SPACESHIP * -ry + earthy + earthR;
        System.out.printf("x=%1.20E,y=%1.20E\n", spaceshipx, spaceshipy);

        //Velocity
        double vt = -7.12 - 8 + earthvx * ox + earthvy * oy;
        double spaceshipvx = ox * vt;
        double spaceshipvy = oy * vt;
        System.out.printf("vx=%1.20E,vy=%1.20E\n", spaceshipvx, spaceshipvy);

        LOGGER.info("Executing Venus Mision ...");

        final AlgorithmResults methodResults = SpaceMission.execute(
            particlesParserResult.getParticlesPerTime().get(0),
            baseArguments.getDt(),
            baseArguments.getMaxTime()
        );

        LOGGER.info(String.format("Finished Venus Mision In %d Iterations",
            methodResults.getIterations()));
        LOGGER.info("Writing Results ...");
        final File outResultsFile = new File(baseArguments.getOutResultsFilePath());
        try (PrintWriter pw = new PrintWriter(outResultsFile)) {
            methodResults.getParticlesStates()
                .forEach((time, states) -> {
                    pw.append(String.format("%f\n", time));
                    states.forEach((particle, state) ->
                        pw.printf("%d %f %f %f %f\n",
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
