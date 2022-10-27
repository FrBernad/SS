package ar.edu.itba.ss.simulator.simulation;

import ar.edu.itba.ss.simulator.Algorithms.CellIndex.Grid;
import ar.edu.itba.ss.simulator.utils.AlgorithmResults;
import ar.edu.itba.ss.simulator.utils.ExecutionTimestamps;
import ar.edu.itba.ss.simulator.utils.Particle;
import ar.edu.itba.ss.simulator.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static ar.edu.itba.ss.simulator.simulation.VibratedSiloUtils.*;
import static ar.edu.itba.ss.simulator.utils.R.values.R0;
import static ar.edu.itba.ss.simulator.utils.R.values.R1;


public class VibratedSilo {

    private static final Logger LOGGER = LoggerFactory.getLogger(VibratedSilo.class);

    public static final double INTERACTION_RADIUS = 0.1;
    public static final int GRID_HEIGHT_RESTRICTION = 30;

    public static AlgorithmResults execute(final Map<Particle, R> initialRs,
                                           final int L, final int W, final double D, final double exitDistance,
                                           final double reenterMinHeight, final double reenterMaxHeight,
                                           final double kn, final double kt,
                                           final double w, final double A,
                                           final double dt, final double tf,
                                           final double printStep, final PrintWriter resultsWriter,
                                           final PrintWriter exitTimeWriter) {

        final ExecutionTimestamps executionTimestamps = new ExecutionTimestamps();
        executionTimestamps.setAlgorithmStart(LocalDateTime.now());

        calculateInitialAccelerations(initialRs);
        printToFile(0.0, initialRs, resultsWriter);

        Map<Particle, R> prevRs = euler(initialRs, -dt);
        Map<Particle, R> currentRs = initialRs;

        int iterations = 0;
        int totalIterations = (int) Math.ceil(tf / dt);
        int loggingStep = (int) Math.floor(100 / dt);

        final double maxRadius = initialRs
            .keySet()
            .stream()
            .map(Particle::getRadius)
            .max(Double::compare).orElseThrow();

        int optimalM = getOptimalGridCondition(L - GRID_HEIGHT_RESTRICTION, maxRadius);
        int optimalN = getOptimalGridCondition(W, maxRadius);

        final Grid grid = new Grid(L - GRID_HEIGHT_RESTRICTION, W, optimalM, optimalN);
        final Set<Particle> particlesAlreadyOutside = new HashSet<>();

        for (double t = dt; iterations < totalIterations; t += dt, iterations += 1) {

            if ((iterations + 1) % loggingStep == 0) {
                LOGGER.info(String.format("Current Time: %.1f s", t));
            }

            final Map<Particle, R> nextRs = calculateNextRs(prevRs, currentRs, grid, t, dt, W, D, kn, kt, w, A);

            final Set<Particle> particlesJustOutside = new HashSet<>();
            final Map<Particle, R> particlesOutsideOpeningRs = respawnParticlesOutsideOpening(currentRs,
                particlesJustOutside, particlesAlreadyOutside, reenterMinHeight, reenterMaxHeight, exitDistance, W);
            calculateInitialAccelerations(particlesOutsideOpeningRs);

            nextRs.putAll(particlesOutsideOpeningRs);

            if (particlesJustOutside.size() > 0) {
                final Map<Particle, R> particlesJustOutsideRs = nextRs.entrySet()
                    .stream().filter((entry) -> particlesJustOutside.contains(entry.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                printToFile(t, particlesJustOutsideRs, exitTimeWriter);
            }

            if ((iterations + 1) % printStep == 0) {
                printToFile(t, nextRs, resultsWriter);
            }

            prevRs = currentRs;
            prevRs.putAll(euler(particlesOutsideOpeningRs, -dt));

            currentRs = nextRs;
        }

        executionTimestamps.setAlgorithmEnd(LocalDateTime.now());

        return new AlgorithmResults(executionTimestamps, iterations);
    }

    private static int getOptimalGridCondition(int distance, double maxRadius) {
        final double gridCondition = distance / (INTERACTION_RADIUS + 2 * maxRadius);
        int optimal = (int) Math.floor(gridCondition);
        if (gridCondition == (int) gridCondition) {
            optimal = (int) gridCondition - 1;
        }
        return optimal;
    }

    private static void printToFile(final Double time, final Map<Particle, R> particlesStates, final PrintWriter pw) {
        pw.append(String.format("%f\n", time));

        particlesStates.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach((entry) ->
                pw.printf("%d %.16f %.16f %.16f %.16f\n",
                    entry.getKey().getId(),
                    entry.getValue().get(R0.ordinal()).getX(), entry.getValue().get(R0.ordinal()).getY(),
                    entry.getValue().get(R1.ordinal()).getX(), entry.getValue().get(R1.ordinal()).getY()));
    }

}
