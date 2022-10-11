package ar.edu.itba.ss.simulator;

import ar.edu.itba.ss.simulator.simulation.SpaceMission;
import ar.edu.itba.ss.simulator.utils.AlgorithmResults;
import ar.edu.itba.ss.simulator.utils.ParseUtils;
import ar.edu.itba.ss.simulator.utils.Particle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrDefault;
import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrFail;
import static ar.edu.itba.ss.simulator.utils.ParseUtils.parseParticlesList;
import static ar.edu.itba.ss.simulator.utils.Particle.Position;
import static ar.edu.itba.ss.simulator.utils.Particle.State;
import static java.lang.Double.parseDouble;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class SimulatorStepToVenus {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimulatorStepToVenus.class);
    private static final String STATIC_FILE_PATH_P = "staticFile";
    private static final String DYNAMIC_FILE_PATH_P = "dynamicFile";
    private static final String RESULTS_OUT_DIR_PATH_P = "resultsDir";
    private static final String TF_P = "tf";
    private static final String DELIMITER_P = "delimiter";
    private static final String DEFAULT_DELIMITER = " ";

    private static final List<Integer> STEPS = List.of(50, 300, 6000, 12000);

    private static final Integer MAX_STEP = 12000;

    public static void main(String[] args) throws IOException {
        LOGGER.info("SimulatorSpeedToVenus Starting ...");
        final String staticFilePath;
        final String dynamicFilePath;
        final String outResultsDirPath;
        final String delimiter;
        final double tf;

        final Properties properties = System.getProperties();
        try {
            staticFilePath = getPropertyOrFail(properties, STATIC_FILE_PATH_P);
            dynamicFilePath = getPropertyOrFail(properties, DYNAMIC_FILE_PATH_P);
            outResultsDirPath = getPropertyOrFail(properties, RESULTS_OUT_DIR_PATH_P);
            delimiter = getPropertyOrDefault(properties, DELIMITER_P, DEFAULT_DELIMITER);
            tf = parseDouble(getPropertyOrDefault(properties, TF_P, "5"));
        } catch (Exception E) {
            printClientUsage();
            return;
        }

        final File staticFile = Paths.get(staticFilePath).toFile();
        final File dynamicFile = Paths.get(dynamicFilePath).toFile();

        final ParseUtils.ParticlesParserResult particlesParserResult = parseParticlesList(staticFile,
            dynamicFile,
            delimiter);

        Map<Particle, State> particles = particlesParserResult.getParticlesPerTime().get(0);

        for (Integer step : STEPS) {

            //Velocity
            LOGGER.info(String.format("Step %d", step));
            LOGGER.info("   Executing Venus Mission ...");

            final AlgorithmResults methodResults = SpaceMission.execute(
                particles,
                step,
                tf
            );

            LOGGER.info(String.format("   Finished Venus Mission In %d Iterations",
                methodResults.getIterations()));
            LOGGER.info("   Writing Results ...");
            final String outResultsPath = String.format("%s/%d", outResultsDirPath, step);
            final File outResultsFile = new File(outResultsPath);

            int index = 0;
            int printStep = MAX_STEP / step;

            try (PrintWriter pw = new PrintWriter(outResultsFile)) {
                for (Map.Entry<Double, Map<Particle, State>> entry : methodResults.getParticlesStates().entrySet()) {
                    Double time = entry.getKey();
                    Map<Particle, State> states = entry.getValue();
                    if (index % printStep == 0) {
                        pw.append(String.format("%f\n", time));
                        states.forEach((particle, state) ->
                            pw.printf("%d %.16f %.16f %.16f %.16f\n",
                                particle.getId(),
                                state.getPosition().getX(), state.getPosition().getY(),
                                state.getVelocityX(), state.getVelocityY()));

                    }
                    index += 1;
                }
            }

            LOGGER.info("   Done!\n");
        }


    }

    private static void printClientUsage() {
        System.out.println("Invalid simulator invocation.\n" +
            "Usage: ./simulator -DstaticFile='path/to/static/file' -DdynamicFilesDir='path/to/dynamic/files/dir' " +
            " -DresultsDir=resultsDir [-Ddelimiter=delimiter] -Ddt=dt -Dtf=tf"
        );
    }

}
