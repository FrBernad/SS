package ar.edu.itba.ss.simulator.algorithms.vibratedsilo;

import ar.edu.itba.ss.simulator.algorithms.vibratedsilo.Collision.CollisionType;
import ar.edu.itba.ss.simulator.utils.AlgorithmResults;
import ar.edu.itba.ss.simulator.utils.ExecutionTimestamps;
import ar.edu.itba.ss.simulator.utils.Particle;
import ar.edu.itba.ss.simulator.utils.Particle.Position;

import java.time.LocalDateTime;
import java.util.*;

import static ar.edu.itba.ss.simulator.algorithms.vibratedsilo.VibratedSiloUtils.getClosestCollision;
import static ar.edu.itba.ss.simulator.algorithms.vibratedsilo.VibratedSiloUtils.updateParticlesStates;
import static ar.edu.itba.ss.simulator.utils.Particle.State;

public class VibratedSilo {

    public static AlgorithmResults execute(final Map<Particle, State> initialParticlesStates,
                                           final int L, final double reenterHeight,
                                           final double reenterMinHeight, final double reenterMaxHeight,
                                           final double kn, final double kt,
                                           final double w, final double dt, final double tf) {

        final ExecutionTimestamps executionTimestamps = new ExecutionTimestamps();
        executionTimestamps.setAlgorithmStart(LocalDateTime.now());

        final Map<Double, Map<Particle, State>> particlesStates = new TreeMap<>();

        int iterations = 0;
        int totalIterations = (int) Math.ceil(tf / dt);
        for (double t = dt; iterations < totalIterations; t += dt, iterations += 1) {
        }

        executionTimestamps.setAlgorithmEnd(LocalDateTime.now());

        return new AlgorithmResults(executionTimestamps, iterations, particlesStates);
    }

}
