package ar.edu.itba.ss.simulator;

import ar.edu.itba.ss.simulator.algorithms.Beeman;
import ar.edu.itba.ss.simulator.algorithms.GearPredictor;
import ar.edu.itba.ss.simulator.algorithms.VerletOriginal;
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
import static java.lang.Double.parseDouble;
import static java.lang.Math.pow;

// FIXME: primer grafico q muestre los 3 algoritmos solapados y en la leyenda el algo y su ECM al costado
// FIXME: desps otro grafico barriendo magnitudes de 10^-1 a 10ˆ-x calculando ECM para cada algoritmo
public class SimulatorOscillator {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimulatorOscillator.class);
    // PARAMETERS
    private static final int MASS = 70;
    private static final int RADIUS = 1;
    private static final double K = pow(10, 4);
    private static final int GAMMA = 100;

    // INITIAL CONDITIONS
    private static final double X_0 = 1;
    private static final double A = 1;
    private static final double V_0 = -A * GAMMA / (2 * MASS);

    private static final String ALGORITHM_P = "algorithm";
    private static final String DT_P = "dt";
    private static final String TF_P = "tf";
    private static final String TIME_OUT_PATH_P = "timeFile";
    private static final String RESULTS_OUT_PATH_P = "resultsFile";


    public static void main(String[] args) throws IOException {
        LOGGER.info("SimulatorOscillator Starting ...");

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
        AlgorithmResults methodResults;

        switch (baseArguments.getAlgorithm()) {
            case GEAR_PREDICTOR:
                methodResults = GearPredictor.execute(
                    oscillatorParticle,
                    new State(new Position(X_0, 0), V_0, 0),
                    K,
                    GAMMA,
                    baseArguments.getDt(),
                    baseArguments.getMaxTime()
                );
                break;
            case BEEMAN:
                methodResults = Beeman.execute(
                    oscillatorParticle,
                    new State(new Position(X_0, 0), V_0, 0),
                    K,
                    GAMMA,
                    baseArguments.getDt(),
                    baseArguments.getMaxTime()
                );
                break;
            case VERLET_ORIGINAL:
                methodResults = VerletOriginal.execute(
                    oscillatorParticle,
                    new State(new Position(X_0, 0), V_0, 0),
                    K,
                    GAMMA,
                    baseArguments.getDt(),
                    baseArguments.getMaxTime()
                );
                break;
            default:
                System.out.println("Invalid algorithm");
                return;
        }


        LOGGER.info(String.format("Finished Oscillation In %d Iterations",
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

        final String outResultsFile = getPropertyOrFail(properties, RESULTS_OUT_PATH_P);
        final Algorithm algorithm = Algorithm.valueOf(getPropertyOrDefault(properties, ALGORITHM_P, "GEAR_PREDICTOR"));
        final double dt = parseDouble(getPropertyOrFail(properties, DT_P));
        final double tf = parseDouble(getPropertyOrDefault(properties, TF_P, "5"));
        final String timeFilePath = getPropertyOrFail(properties, TIME_OUT_PATH_P);

        return new BaseArguments(null, null, outResultsFile, timeFilePath, null, tf, dt, algorithm);
    }

    private static void printClientUsage() {
        System.out.println("Invalid simulator invocation.\n" +
            "Usage: ./simulator -DresultsFile=resultsFile -DtimeFile=timeFile -Dalgorithm=algorithm  -Ddt=dt -Dtf=tf "
        );
    }
}
