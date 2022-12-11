package ar.edu.itba.ss.simulator.simulation;

import ar.edu.itba.ss.simulator.Algorithms.CellIndex.Grid;
import ar.edu.itba.ss.simulator.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static ar.edu.itba.ss.simulator.simulation.VibratedSiloUtilsPhase.*;
import static ar.edu.itba.ss.simulator.utils.R.values.R0;
import static ar.edu.itba.ss.simulator.utils.R.values.R1;


public class VibratedSiloPhase {

    private static final Logger LOGGER = LoggerFactory.getLogger(VibratedSiloPhase.class);
    public static final double INTERACTION_RADIUS = 0.0;
    public static final int GRID_HEIGHT_RESTRICTION = 0;

    public static AlgorithmResults execute(final Map<Particle, R> initialRs,
                                           final int L, final int W, final int D,
                                           final double exitDistance, final double reenterMinHeight,
                                           final double reenterMaxHeight, final double kn, final double kt,
                                           final double frequency, final double A,
                                           final double dt, final double tf,
                                           final double initialVx, final double initialVy,
                                           final double printStep, final PrintWriter resultsWriter,
                                           final PrintWriter exitTimeWriter, final double radiusR0,
                                           final double vd, final double tau) {

        final ExecutionTimestamps executionTimestamps = new ExecutionTimestamps();
        executionTimestamps.setAlgorithmStart(LocalDateTime.now());

        final Map<Particle, Pair<Double, R>> initialRWithPhases = new HashMap<>();
        initialRs.forEach((p, r) -> initialRWithPhases.put(p, new Pair<>(p.getRadius(), r)));

        calculateInitialAccelerations(initialRWithPhases, vd, tau);
        printToResultsFile(0.0, initialRWithPhases, resultsWriter);

        Map<Particle, Pair<Double, R>> prevRs = euler(initialRWithPhases, -dt, -dt, vd, tau, A, frequency, radiusR0);
        Map<Particle, Pair<Double, R>> currentRs = initialRWithPhases;

        int iterations = 0;
        int totalIterations = (int) Math.ceil(tf / dt);
        int loggingStep = (int) Math.floor(100 / dt);

        final double maxRadius = radiusR0 * (1 + A);

        int optimalM = getOptimalGridCondition(L - GRID_HEIGHT_RESTRICTION, maxRadius);
        int optimalN = getOptimalGridCondition(W, maxRadius);
        final Grid grid = new Grid(L - GRID_HEIGHT_RESTRICTION, W, optimalM, optimalN);

        final Set<Particle> particlesAlreadyOutside = new HashSet<>();

        for (double t = dt; iterations < totalIterations; t += dt, iterations += 1) {

            if ((iterations + 1) % loggingStep == 0) {
                LOGGER.info(String.format("Current Time: %.1f s", t));
            }

            final Map<Particle, Pair<Double, R>> nextRs = calculateNextRs(prevRs, currentRs, grid, t, dt, W, D, kn, kt, frequency, A, vd, tau, radiusR0);

            final Set<Particle> particlesJustOutside = new HashSet<>();
            final Map<Particle, Pair<Double, R>> particlesOutsideOpeningRs = respawnParticlesOutsideOpening(currentRs,
                particlesJustOutside, particlesAlreadyOutside, reenterMinHeight, reenterMaxHeight, exitDistance,
                W, initialVx, initialVy);

            calculateInitialAccelerations(particlesOutsideOpeningRs, vd, tau);

            nextRs.putAll(particlesOutsideOpeningRs);

            if (particlesJustOutside.size() > 0) {
                final Map<Particle, Pair<Double, R>> particlesJustOutsideRs = nextRs.entrySet()
                    .stream().filter((entry) -> particlesJustOutside.contains(entry.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                printToExitFile(t, particlesJustOutsideRs, exitTimeWriter);
            }

            if ((iterations + 1) % printStep == 0) {
                printToResultsFile(t, nextRs, resultsWriter);
            }

            prevRs = currentRs;
            prevRs.putAll(euler(particlesOutsideOpeningRs, -dt, t - dt, vd, tau, A, frequency, radiusR0));

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


    private static void printToResultsFile(final Double time, final Map<Particle, Pair<Double, R>> particlesStates, final PrintWriter pw) {
        pw.append(String.format("%f\n", time));

        particlesStates.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach((entry) ->
                pw.printf("%.16f %.16f %.16f %.16f %.16f\n",
                    entry.getValue().getValue().get(R0.ordinal()).getKey(), entry.getValue().getValue().get(R0.ordinal()).getValue(),
                    entry.getValue().getValue().get(R1.ordinal()).getKey(), entry.getValue().getValue().get(R1.ordinal()).getValue(),
                    entry.getValue().getKey()));
    }

    private static void printToExitFile(final Double time, final Map<Particle, Pair<Double, R>> particlesStates, final PrintWriter pw) {
        pw.append(String.format("%f\n", time));
    }

}
