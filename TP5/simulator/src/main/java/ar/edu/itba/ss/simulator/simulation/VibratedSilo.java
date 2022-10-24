package ar.edu.itba.ss.simulator.simulation;

import ar.edu.itba.ss.simulator.utils.AlgorithmResults;
import ar.edu.itba.ss.simulator.utils.ExecutionTimestamps;
import ar.edu.itba.ss.simulator.utils.Particle;
import ar.edu.itba.ss.simulator.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

import static ar.edu.itba.ss.simulator.simulation.VibratedSiloUtils.*;


public class VibratedSilo {

    private static final Logger LOGGER = LoggerFactory.getLogger(VibratedSilo.class);

    public static AlgorithmResults execute(final Map<Particle, R> initialRs,
                                           final int L, final int W, final double D, final double exitDistance,
                                           final double reenterMinHeight, final double reenterMaxHeight,
                                           final double kn, final double kt,
                                           final double w, final double A,
                                           final double dt, final double tf) {

        final ExecutionTimestamps executionTimestamps = new ExecutionTimestamps();
        executionTimestamps.setAlgorithmStart(LocalDateTime.now());

        final Map<Double, Map<Particle, R>> particlesStates = new TreeMap<>();

        calculateInitialAccelerations(initialRs, W, D, kn, kt, w, A);
        particlesStates.put(0.0, initialRs);

        Map<Particle, R> prevRs = euler(initialRs, -dt, -dt, W, D, kn, kt, w, A);
        Map<Particle, R> currentRs = initialRs;

        int iterations = 0;
        int totalIterations = (int) Math.ceil(tf / dt);
        int loggingStep = (int) Math.floor(50 / dt);
        for (double t = dt; iterations < totalIterations; t += dt, iterations += 1) {

            if ((iterations + 1) % loggingStep == 0) {
                LOGGER.info(String.format("Current Time: %.1f s", t));
            }

            final Map<Particle, R> nextRs = calculateNextRs(prevRs, currentRs, t, dt, W, D, kn, kt, w, A);

            final Map<Particle, R> particlesOutsideOpeningRs = respawnParticlesOutsideOpening(currentRs, reenterMinHeight, reenterMaxHeight, exitDistance, W);
            calculateInitialAccelerations(particlesOutsideOpeningRs, W, D, kn, kt, w, A);

            nextRs.putAll(particlesOutsideOpeningRs);
            particlesStates.put(t, nextRs);

            prevRs = currentRs;
//          FIXME: asumimos que no choca con ninguna particula al ponerlos
            prevRs.putAll(euler(particlesOutsideOpeningRs, -dt, t - dt, W, D, kn, kt, w, A));

            currentRs = nextRs;
        }

        executionTimestamps.setAlgorithmEnd(LocalDateTime.now());

        return new AlgorithmResults(executionTimestamps, iterations, particlesStates);
    }

}
