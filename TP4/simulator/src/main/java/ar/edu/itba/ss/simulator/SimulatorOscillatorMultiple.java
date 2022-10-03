package ar.edu.itba.ss.simulator;

import ar.edu.itba.ss.simulator.algorithms.Beeman;
import ar.edu.itba.ss.simulator.algorithms.GearPredictor;
import ar.edu.itba.ss.simulator.algorithms.VerletOriginal;
import ar.edu.itba.ss.simulator.utils.Algorithm;
import ar.edu.itba.ss.simulator.utils.AlgorithmResults;
import ar.edu.itba.ss.simulator.utils.BaseArguments;
import ar.edu.itba.ss.simulator.utils.Particle;
import ar.edu.itba.ss.simulator.utils.Particle.Position;
import ar.edu.itba.ss.simulator.utils.Particle.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrDefault;
import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrFail;
import static java.lang.Double.parseDouble;
import static java.lang.Math.pow;

public class SimulatorOscillatorMultiple {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimulatorOscillatorMultiple.class);
    // PARAMETERS
    private static final int MASS = 70;
    private static final int RADIUS = 1;
    private static final double K = pow(10, 4);
    private static final int GAMMA = 100;

    // INITIAL CONDITIONS
    private static final double X_0 = 1;
    private static final double A = 1;
    private static final double V_0 = -A * GAMMA / (2 * MASS);

    private static final double DT_START = 0.01;
    private static final List<Integer> STEPS = List.of(1, 1, 1, 10, 100);
    private static final int RUNS = 5;
    private static final String TF_P = "tf";
    private static final String TIME_OUT_PATH_P = "timeFile";
    private static final String RESULTS_OUT_DIR_P = "outResultsDir";
    private static final String BEEMAN_DIR = "Beeman";
    private static final String GEAR_PREDICTOR_DIR = "GearPredictor";
    private static final String VERLET_ORIGINAL_DIR = "VerletOriginal";


    public static void main(String[] args) throws IOException {
        LOGGER.info("SimulatorOscillatorMultiple Starting ...");

        final Properties properties = System.getProperties();
        final BaseArguments baseArguments;

        try {
            LOGGER.info("Parsing Arguments ...");
            baseArguments = getAndParseBaseArguments(properties);
        } catch (IllegalArgumentException e) {
            printClientUsage();
            return;
        }

        final Particle oscillatorParticle = new Particle(1, RADIUS, MASS);

        for (int i = 0; i < RUNS; i++) {
            double dt = BigDecimal.valueOf(DT_START * Math.pow(10, -i)).setScale(i + 2, RoundingMode.FLOOR).doubleValue();
            LOGGER.info("Running simulator with dt {}", dt);

            for (Algorithm algorithm : Algorithm.values()) {
                AlgorithmResults methodResults;

                switch (algorithm) {
                    case GEAR_PREDICTOR:
                        LOGGER.info("Running Gear Predictor");

                        methodResults = GearPredictor.execute(
                            oscillatorParticle,
                            new State(new Position(X_0, 0), V_0, 0),
                            K,
                            GAMMA,
                            dt,
                            baseArguments.getMaxTime()
                        );
                        writeToFile(GEAR_PREDICTOR_DIR, methodResults, dt, baseArguments.getOutResultsFilePath(), STEPS.get(i));
                        break;
                    case BEEMAN:
                        LOGGER.info("Running Beeman");
                        methodResults = Beeman.execute(
                            oscillatorParticle,
                            new State(new Position(X_0, 0), V_0, 0),
                            K,
                            GAMMA,
                            dt,
                            baseArguments.getMaxTime()
                        );
                        writeToFile(BEEMAN_DIR, methodResults, dt, baseArguments.getOutResultsFilePath(), STEPS.get(i));
                        break;
                    case VERLET_ORIGINAL:
                        LOGGER.info("Running Verlet");
                        methodResults = VerletOriginal.execute(
                            oscillatorParticle,
                            new State(new Position(X_0, 0), V_0, 0),
                            K,
                            GAMMA,
                            dt,
                            baseArguments.getMaxTime()
                        );
                        writeToFile(VERLET_ORIGINAL_DIR, methodResults, dt, baseArguments.getOutResultsFilePath(), STEPS.get(i));
                        break;
                    default:
                        System.out.println("Invalid algorithm");
                        return;
                }
            }

        }

        LOGGER.info("Done!");

    }

    private static BaseArguments getAndParseBaseArguments(final Properties properties) throws IllegalArgumentException {

        final String outResultsFile = getPropertyOrFail(properties, RESULTS_OUT_DIR_P);
        final double tf = parseDouble(getPropertyOrDefault(properties, TF_P, "5"));
        final String timeFilePath = getPropertyOrDefault(properties, TIME_OUT_PATH_P, "");
        return new BaseArguments(null, null, outResultsFile, timeFilePath, null, tf, 0, null);
    }

    private static void printClientUsage() {
        System.out.println("Invalid simulator invocation.\n" +
            "Usage: ./simulator -DoutresultsDir=resultsDir -Dtf=tf "
        );
    }

    private static void writeToFile(String algorithm, AlgorithmResults methodResults, double dt, String outputDir, int step) throws IOException {
        LOGGER.info(String.format("Finished Oscillation In %d Iterations", methodResults.getIterations()));
        LOGGER.info("Writing Results ...");
        String filePath = String.format("%s/%s/results_%f", outputDir, algorithm, dt);
        final File outResultsFile = new File(filePath);
        try (PrintWriter pw = new PrintWriter(outResultsFile)) {
            int i = 0;
            for (Map.Entry<Double, Map<Particle, State>> entry : methodResults.getParticlesStates().entrySet()) {
                if (i % step == 0) {
                    Double time = entry.getKey();
                    Map<Particle, State> states = entry.getValue();
                    pw.append(String.format("%f\n", time));
                    states.forEach((particle, state) ->
                        pw.printf("%d %.16f %.16f %.16f %.16f\n",
                            particle.getId(),
                            state.getPosition().getX(), state.getPosition().getY(),
                            state.getVelocityX(), state.getVelocityY()));
                }
                i++;
            }
        }

    }
}

