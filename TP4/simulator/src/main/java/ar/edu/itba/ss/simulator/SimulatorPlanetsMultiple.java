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
import static ar.edu.itba.ss.simulator.utils.ParseUtils.parseParticlesList;
import static java.lang.Double.parseDouble;

public class SimulatorPlanetsMultiple {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimulatorPlanetsMultiple.class);
    private static final String STATIC_FILE_PATH_P = "staticFile";
    private static final String DYNAMIC_FILES_PATH_P = "dynamicFilesDir";
    private static final String RESULTS_OUT_DIR_PATH_P = "resultsDir";
    private static final String TIME_OUT_PATH_P = "timeFile";
    private static final String ALGORITHM_P = "algorithm";
    private static final String DT_P = "dt";
    private static final String TF_P = "tf";
    private static final String DELIMITER_P = "delimiter";
    private static final String DEFAULT_DELIMITER = " ";

    public static void main(String[] args) throws IOException {
        LOGGER.info("SimulatorPlanetsMultiple Starting ...");
        final String staticFilePath;
        final String dynamicFilesPath;
        final String outResultsDirPath;
        final String timeFilePath;
        final String delimiter;
        final double dt;
        final double tf;
        final Algorithm algorithm;


        final Properties properties = System.getProperties();
        try {
            staticFilePath = getPropertyOrFail(properties, STATIC_FILE_PATH_P);
            dynamicFilesPath = getPropertyOrFail(properties, DYNAMIC_FILES_PATH_P);
            outResultsDirPath = getPropertyOrFail(properties, RESULTS_OUT_DIR_PATH_P);
            timeFilePath = getPropertyOrFail(properties, TIME_OUT_PATH_P);
            delimiter = getPropertyOrDefault(properties, DELIMITER_P, DEFAULT_DELIMITER);
            dt = parseDouble(getPropertyOrFail(properties, DT_P));
            tf = parseDouble(getPropertyOrDefault(properties, TF_P, "5"));
            algorithm = Algorithm.valueOf(getPropertyOrDefault(properties, ALGORITHM_P, "GEAR_PREDICTOR"));
        } catch (Exception E) {
            printClientUsage();
            return;
        }


        final File staticFile = Paths.get(staticFilePath).toFile();
        final File dynamicFileDir = new File(dynamicFilesPath);
        File[] dynamicFiles = dynamicFileDir.listFiles();
        if (dynamicFiles == null) {
            return;
        }

        for (int i = 0; i < dynamicFiles.length; i++) {
            LOGGER.info("Running File {}: {}", i + 1, dynamicFiles[i].getName());
            LOGGER.info("Parsing Particles ...");

            final File dynamicFile = dynamicFiles[i];
            final ParseUtils.ParticlesParserResult particlesParserResult = parseParticlesList(staticFile,
                    dynamicFile,
                    delimiter);

            LOGGER.info("Executing Venus Mision ...");

            final AlgorithmResults methodResults = SpaceMission.execute(
                    particlesParserResult.getParticlesPerTime().get(0),
                    dt,
                    tf
            );

            LOGGER.info(String.format("Finished Venus Mision In %d Iterations",
                    methodResults.getIterations()));
            LOGGER.info("Writing Results ...");
            final String fileName = dynamicFile.getName().split("_")[1];
            final String outResultsPath = String.format("%s/%s", outResultsDirPath, fileName);
            final File outResultsFile = new File(outResultsPath);
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

        }
        LOGGER.info("Done!");

    }

    private static void printClientUsage() {
        System.out.println("Invalid simulator invocation.\n" +
                "Usage: ./simulator -DstaticFile='path/to/static/file' -DdynamicFilesDir='path/to/dynamic/files/dir' " +
                " -DresultsDir=resultsFile [-Ddelimiter=delimiter] -Ddt=dt -Dtf=tf -Dalgorithm=algorithm "
        );
    }

}
