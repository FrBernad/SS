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
    private static final String INITIAL_VX_P = "vx";
    private static final String INITIAL_VY_P = "vy";
    private static final String w_P = "w";
    private static final String D_P = "D";
    private static final String KN_P = "kn";
    private static final String KT_P = "kt";
    private static final String A_P = "A";
    private static final String EXIT_DISTANCE_P = "exitDistance";
    private static final String REENTER_MIN_HEIGHT_P = "renterMinHeight";
    private static final String REENTER_MAX_HEIGHT_P = "renterMaxHeight";
    private static final String GRAVITY_P = "G";


    /*Default Properties*/
    private static final String DEFAULT_DELIMITER = " ";
    private static final String DEFAULT_L = "70";
    private static final String DEFAULT_W = "20";
    private static final String DEFAULT_DT = "0.001";
    private static final String DEFAULT_DT2 = "0.1";
    private static final String DEFAULT_TF = "1000";
    private static final String DEFAULT_INITIAL_VX = "0";
    private static final String DEFAULT_INITIAL_VY = "0";
    private static final String DEFAULT_w = "5";
    private static final String DEFAULT_D = "3";
    private static final String DEFAULT_KN = "250";
    private static final String DEFAULT_KT = "500";
    private static final String DEFAULT_A = "0.15";
    private static final String DEFAULT_EXIT_DISTANCE = "7";
    private static final String DEFAULT_REENTER_MIN_HEIGHT = "40";
    private static final String DEFAULT_REENTER_MAX_HEIGHT = "70";
    private static final String DEFAULT_GRAVITY = "5";


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

        LOGGER.info(String.format("Executing Simulator with %d particles / w = %f / d = %d", particlesParserResult.getN(),
                baseArguments.getFrequency(), baseArguments.getD()));
        LOGGER.info(String.format("Writing Results every %.2f seconds", printStep * baseArguments.getDt()));

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

        LOGGER.info(String.format("Finished Simulation In %d Iterations", methodResults.getIterations()));

        LOGGER.info("Done!");
    }

    private static BaseArguments getAndParseBaseArguments(final Properties properties) throws IllegalArgumentException {

        final String staticFilePath = getPropertyOrFail(properties, STATIC_FILE_PATH_P);
        final String dynamicFilePath = getPropertyOrFail(properties, DYNAMIC_FILE_PATH_P);
        final String outResultsFile = getPropertyOrFail(properties, RESULTS_OUT_PATH_P);
        final String outExitTimeFile = getPropertyOrFail(properties, EXIT_TIME_PATH_P);
        final String delimiter = getPropertyOrDefault(properties, DELIMITER_P, DEFAULT_DELIMITER);

        final int L = parseInt(getPropertyOrDefault(properties, L_P, DEFAULT_L));
        final int W = parseInt(getPropertyOrDefault(properties, W_P, DEFAULT_W));
        final int D = parseInt(getPropertyOrDefault(properties, D_P, DEFAULT_D));
        final double w = parseDouble(getPropertyOrDefault(properties, w_P, DEFAULT_w));
        final double kn = parseDouble(getPropertyOrDefault(properties, KN_P, DEFAULT_KN));
        final double kt = parseDouble(getPropertyOrDefault(properties, KT_P, DEFAULT_KT));
        final double A = parseDouble(getPropertyOrDefault(properties, A_P, DEFAULT_A));
        final double exitDistance = parseDouble(getPropertyOrDefault(properties, EXIT_DISTANCE_P, DEFAULT_EXIT_DISTANCE));
        final double reenterMinHeight = parseDouble(getPropertyOrDefault(properties, REENTER_MIN_HEIGHT_P, DEFAULT_REENTER_MIN_HEIGHT));
        final double reenterMaxHeight = parseDouble(getPropertyOrDefault(properties, REENTER_MAX_HEIGHT_P, DEFAULT_REENTER_MAX_HEIGHT));
        final double gravity = parseDouble(getPropertyOrDefault(properties, GRAVITY_P, DEFAULT_GRAVITY));
        final double dt = parseDouble(getPropertyOrDefault(properties, DT_P, DEFAULT_DT));
        final double dt2 = parseDouble(getPropertyOrDefault(properties, DT2_P, DEFAULT_DT2));
        final double tf = parseDouble(getPropertyOrDefault(properties, TF_P, DEFAULT_TF));
        final double vx = parseDouble(getPropertyOrDefault(properties, INITIAL_VX_P, DEFAULT_INITIAL_VX));
        final double vy = parseDouble(getPropertyOrDefault(properties, INITIAL_VY_P, DEFAULT_INITIAL_VY));

        final File staticFile = Paths.get(staticFilePath).toFile();
        final File dynamicFile = Paths.get(dynamicFilePath).toFile();

        return new BaseArguments(staticFile, dynamicFile, outResultsFile, outExitTimeFile, delimiter, L, W, D, w, kn, kt, A, exitDistance, reenterMinHeight, reenterMaxHeight, gravity, dt, dt2, tf, vx, vy);
    }

    private static void printClientUsage() {
        System.out.println("Invalid simulator invocation.\n" +
                "Usage: ./simulator -DstaticFile='path/to/static/file' -DdynamicFile='path/to/dynamic/file' " +
                "-DresultsFile='path/to/results/file' -DexitTimeFile='path/to/exitTime/file' " +
                "-DL=L -DW=W -DD=D -Dw=w -Dkn=kn -Dkt=kt -DA=A -DexitDistance=exitDistance" +
                "-DreenterMinHeight=reenterMinHeight -DreenterMaxHeight=reenterMaxHeight" +
                "-Dgravity=gravity -Ddt=dt -Ddt2=dt2 -Dtf=tf -Dvx=vx -Dvy=vy"
        );
    }
}
