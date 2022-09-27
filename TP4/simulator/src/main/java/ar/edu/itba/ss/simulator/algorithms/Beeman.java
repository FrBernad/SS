package ar.edu.itba.ss.simulator.algorithms;

import ar.edu.itba.ss.simulator.utils.*;
import ar.edu.itba.ss.simulator.utils.Particle.Position;
import ar.edu.itba.ss.simulator.utils.Particle.State;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static ar.edu.itba.ss.simulator.utils.R.values.*;

public class Beeman {

    public static AlgorithmResults execute(final Particle particle, final State initialState,
                                           final double k, final double gamma,
                                           final double dt, final double tf) {

        final ExecutionTimestamps executionTimestamps = new ExecutionTimestamps();
        executionTimestamps.setAlgorithmStart(LocalDateTime.now());

        int iterations = 0;

        final Map<Double, Map<Particle, State>> particlesStates = new TreeMap<>();

        final R initialR = calculateInitialR(particle.getMass(), initialState, k, gamma);
        final Pair initialr0 = initialR.get(R0.ordinal());
        final Pair initialr1 = initialR.get(R1.ordinal());
        particlesStates.put(0.0, Map.of(particle, new State(new Position(initialr0.getX(), initialr0.getY()), initialr1.getX(), initialr1.getY())));

        final List<R> RStates = new ArrayList<>();
        RStates.add(initialR);

        for (double t = dt; t <= tf; t += dt, iterations += 1) {

            final R currentR = RStates.get(iterations);

            final R predictions = predict(currentR, dt);

            final Pair deltaR2 = getDeltaR2(particle.getMass(), k, gamma,
                predictions.get(R0.ordinal()), predictions.get(R1.ordinal()),
                predictions.get(R2.ordinal()), dt);

            final R corrections = correct(predictions, deltaR2, dt);

            final Pair r0 = corrections.get(R0.ordinal());
            final Pair r1 = corrections.get(R1.ordinal());

            RStates.add(corrections);
            particlesStates.put(t, Map.of(particle, new State(new Position(r0.getX(), r0.getY()), r1.getX(), r1.getY())));
        }

        executionTimestamps.setAlgorithmEnd(LocalDateTime.now());

        return new AlgorithmResults(executionTimestamps, iterations, particlesStates);
    }
}
