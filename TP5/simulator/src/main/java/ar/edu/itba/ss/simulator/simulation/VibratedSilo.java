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
import static ar.edu.itba.ss.simulator.utils.Particle.State;


public class VibratedSilo {

    private static final Logger LOGGER = LoggerFactory.getLogger(VibratedSilo.class);

    public static AlgorithmResults execute(final Map<Particle, State> initialStates,
                                           final int L, final double reenterHeight,
                                           final double reenterMinHeight, final double reenterMaxHeight,
                                           final double kn, final double kt,
                                           final double w, final double A,
                                           final double dt, final double tf) {

        final ExecutionTimestamps executionTimestamps = new ExecutionTimestamps();
        executionTimestamps.setAlgorithmStart(LocalDateTime.now());

        final Map<Double, Map<Particle, State>> particlesStates = new TreeMap<>();

        final Map<Particle, R> initialRs = calculateInitialRs(initialStates, L, kn, kt, w, A);
        storeStates(particlesStates, initialRs, 0.0);

        Map<Particle, R> prevRs = euler(initialRs, -dt, L, kn, kt, w, A);
        Map<Particle, R> currentRs = initialRs;

        int iterations = 0;
        int totalIterations = (int) Math.ceil(tf / dt);
        int loggingStep = (int) Math.floor(50 / dt);
        for (double t = dt; iterations < totalIterations; t += dt, iterations += 1) {

            if ((iterations + 1) % loggingStep == 0) {
                LOGGER.info(String.format("Current Time: %f", t));
            }

            final Map<Particle, R> nextRs = calculateNextRs(prevRs, currentRs, dt, L, kn, kt, w, A);

            storeStates(particlesStates, nextRs, t);

            prevRs = currentRs;
            currentRs = nextRs;
        }

        executionTimestamps.setAlgorithmEnd(LocalDateTime.now());

        return new AlgorithmResults(executionTimestamps, iterations, particlesStates);
    }

}
