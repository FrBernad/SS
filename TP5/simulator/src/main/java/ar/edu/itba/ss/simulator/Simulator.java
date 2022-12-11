package ar.edu.itba.ss.simulator;

import ar.edu.itba.ss.simulator.simulation.VibratedSilo;
import ar.edu.itba.ss.simulator.utils.AlgorithmResults;
import ar.edu.itba.ss.simulator.utils.BaseArguments;
import ar.edu.itba.ss.simulator.utils.RandomGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Properties;

import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrDefault;
import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrFail;
import static ar.edu.itba.ss.simulator.utils.ParseUtils.ParticlesParserResult;
import static ar.edu.itba.ss.simulator.utils.ParseUtils.parseParticlesList;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class Simulator {
    private static final Logger LOGGER = LoggerFactory.getLogger(Simulator.class);

    /*Java Properties*/
    private static final String STATIC_FILE_PATH_P = "staticFile";
    private static final String DYNAMIC_FILE_PATH_P = "dynamicFile";
    private static final String EXIT_TIME_PATH_P = "exitTimeFile";
    private static final String RESULTS_OUT_PATH_P = "resultsFile";
    private static final String DELIMITER_P = "delimiter";
    private static final String L_P = "L";
    private static final String W_P = "W";
    private static final String DT_P = "dt";
    private static final String DT2_P = "dt2";
    private static final String TF_P = "tf";
    private static final String w_P = "w";
    private static final String D_P = "D";
    private static final String KN_P = "kn";
    private static final String KT_P = "kt";
    private static final String A_P = "A";
    private static final String EXIT_DISTANCE_P = "exitDistance";
    private static final String REENTER_MIN_HEIGHT_P = "reenterMinHeight";
    private static final String REENTER_MAX_HEIGHT_P = "reenterMaxHeight";
    private static final String INITIAL_VX_P = "vx";
    private static final String INITIAL_VY_P = "vy";
    private static final String GRAVITY_P = "gravity";
    private static final String SEED_P = "seed";


    /*Default Properties*/
    private static final String DEFAULT_DELIMITER = " ";


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

        final File outResultsFile = new File(baseArguments.getOutResultsFile());
        final File outExitTimeFile = new File(baseArguments.getOutExitTimeFile());

        double secondsStep = baseArguments.getDt2();
        double printStep = secondsStep / baseArguments.getDt();

        PrintWriter resultsWriter = new PrintWriter(outResultsFile);
        PrintWriter exitTimeWriter = new PrintWriter(outExitTimeFile);

        LOGGER.info(String.format("Executing Simulator with N = %d / w = %f / d = %d",
            particlesParserResult.getN(), baseArguments.getFrequency(), baseArguments.getD()));
        LOGGER.info(String.format("Writing Results every %.2f seconds", printStep * baseArguments.getDt()));

        RandomGenerator.setInstance(baseArguments.getSeed());

        final AlgorithmResults methodResults = VibratedSilo.execute(
            particlesParserResult.getParticlesPerTime().get(0),
            baseArguments.getL(), baseArguments.getW(), baseArguments.getD(),
            baseArguments.getExitDistance(), baseArguments.getReenterMinHeight(),
            baseArguments.getReenterMaxHeight(), baseArguments.getKn(), baseArguments.getKt(),
            baseArguments.getFrequency(), baseArguments.getA(), baseArguments.getGravity(),
            baseArguments.getDt(), baseArguments.getMaxTime(),
            baseArguments.getVx(), baseArguments.getVy(),
            printStep, resultsWriter, exitTimeWriter
        );

        resultsWriter.close();
        exitTimeWriter.close();

        final Duration duration = methodResults.getExecutionTimestamps().getAlgorithmTotalTime();
        LOGGER.info(String.format("Finished Simulation In %d Iterations / %d days %d hrs %d mins %d secs ",
            methodResults.getIterations(), duration.toDaysPart(), duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart()));

        LOGGER.info("Done!");
    }

    private static BaseArguments getAndParseBaseArguments(final Properties properties) throws IllegalArgumentException {

        final String staticFilePath = getPropertyOrFail(properties, STATIC_FILE_PATH_P);
        final String dynamicFilePath = getPropertyOrFail(properties, DYNAMIC_FILE_PATH_P);
        final String outResultsFile = getPropertyOrFail(properties, RESULTS_OUT_PATH_P);
        final String outExitTimeFile = getPropertyOrFail(properties, EXIT_TIME_PATH_P);
        final String delimiter = getPropertyOrDefault(properties, DELIMITER_P, DEFAULT_DELIMITER);

        final int L = parseInt(getPropertyOrFail(properties, L_P));
        final int W = parseInt(getPropertyOrFail(properties, W_P));
        final int D = parseInt(getPropertyOrFail(properties, D_P));
        final double w = parseDouble(getPropertyOrFail(properties, w_P));
        final double kn = parseDouble(getPropertyOrFail(properties, KN_P));
        final double kt = parseDouble(getPropertyOrFail(properties, KT_P));
        final double A = parseDouble(getPropertyOrFail(properties, A_P));
        final double exitDistance = parseDouble(getPropertyOrFail(properties, EXIT_DISTANCE_P));
        final double reenterMinHeight = parseDouble(getPropertyOrFail(properties, REENTER_MIN_HEIGHT_P));
        final double reenterMaxHeight = parseDouble(getPropertyOrFail(properties, REENTER_MAX_HEIGHT_P));
        final double gravity = parseDouble(getPropertyOrFail(properties, GRAVITY_P));
        final double dt = parseDouble(getPropertyOrFail(properties, DT_P));
        final double dt2 = parseDouble(getPropertyOrFail(properties, DT2_P));
        final double tf = parseDouble(getPropertyOrFail(properties, TF_P));
        final double vx = parseDouble(getPropertyOrFail(properties, INITIAL_VX_P));
        final double vy = parseDouble(getPropertyOrFail(properties, INITIAL_VY_P));

        Long seed;
        try {
            seed = Long.parseLong(getPropertyOrFail(properties, SEED_P));
        } catch (Exception e) {
            seed = null;
        }

        final File staticFile = Paths.get(staticFilePath).toFile();
        final File dynamicFile = Paths.get(dynamicFilePath).toFile();

        return new BaseArguments(staticFile, dynamicFile, outResultsFile, outExitTimeFile, delimiter, L, W, D, w, kn,
            kt, A, exitDistance, reenterMinHeight, reenterMaxHeight, gravity, dt, dt2, tf, vx, vy, seed);
    }

    private static void printClientUsage() {
        System.out.println("Invalid simulator invocation.\n" +
            "Usage: ./simulator -DstaticFile='path/to/static/file' -DdynamicFile='path/to/dynamic/file' " +
            "-DresultsFile='path/to/results/file' -DexitTimeFile='path/to/exitTime/file' " +
            "-DL=L -DW=W -DD=D -Dw=w -Dkn=kn -Dkt=kt -DA=A -DexitDistance=exitDistance " +
            "-DreenterMinHeight=reenterMinHeight -DreenterMaxHeight=reenterMaxHeight " +
            "-Dgravity=gravity -Ddt=dt -Ddt2=dt2 -Dtf=tf -Dvx=vx -Dvy=vy -Dseed=seed"
        );
    }
}
