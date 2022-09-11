package ar.edu.itba.ss.simulator;

import ar.edu.itba.ss.simulator.algorithms.brownianmotion.BrownianMotion;
import ar.edu.itba.ss.simulator.utils.ActionLogger;
import ar.edu.itba.ss.simulator.utils.BaseArguments;
import ar.edu.itba.ss.simulator.utils.BrownianMotionAlgorithmResults;
import ar.edu.itba.ss.simulator.utils.Particle;
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
import static ar.edu.itba.ss.simulator.utils.Particle.State;
import static java.lang.Integer.parseInt;

public class Simulator {
    private static final Logger LOGGER = LoggerFactory.getLogger(Simulator.class);
    private static final String STATIC_FILE_PATH_P = "staticFile";
    private static final String DYNAMIC_FILE_PATH_P = "dynamicFile";
    private static final String RESULTS_OUT_PATH_P = "resultsFile";
    private static final String TIME_OUT_PATH_P = "timeFile";
    private static final String MAX_ITERATIONS_P = "maxIterations";
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

        LOGGER.info("Executing Brownian Motion ...");
        BrownianMotionAlgorithmResults methodResults = BrownianMotion.execute(
            particlesParserResult.getParticlesPerTime().get(0),
            particlesParserResult.getL(),
            baseArguments.getMaxIterations()
        );

        LOGGER.info(String.format("Finished Brownian Motion In %d Iterations!", methodResults.getIterations()));

        LOGGER.info("Writing Results ...");
        final File outResultsFile = new File(baseArguments.getOutResultsFilePath());
        try (PrintWriter pw = new PrintWriter(outResultsFile)) {
            for (int i = 0; i < methodResults.getParticlesStates().size(); i++) {
                pw.append(String.format("%d\n", i));
                final Map<Particle, State> currentStates = methodResults.getParticlesStates().get(i);
                currentStates.forEach((particle, state) ->
                    pw.printf("%d %f %f %f %f\n",
                        particle.getId(),
                        state.getPosition().getX(), state.getPosition().getY(),
                        state.getVelocityX(), state.getVelocityY())
                );
            }
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

        final int maxIterations = parseInt(getPropertyOrDefault(properties, MAX_ITERATIONS_P, "3000"));

        final File staticFile = Paths.get(staticFilePath).toFile();
        final File dynamicFile = Paths.get(dynamicFilePath).toFile();


        return new BaseArguments(staticFile, dynamicFile, outResultsFile, timeFilePath, delimiter, maxIterations);
    }

    private static void printClientUsage() {
        System.out.println("Invalid simulator invocation.\n" +
            "Usage: ./simulator -DstaticFile='path/to/static/file' -DdynamicFile='path/to/dynamic/file' " +
            " -DresultsFile=resultsFile -DtimeFile=timeFile [-DmaxIterations=iters] "
        );
    }
}
