package ar.edu.itba.ss.simulator.algorithms;

import ar.edu.itba.ss.simulator.utils.*;
import ar.edu.itba.ss.simulator.utils.Particle.Position;
import ar.edu.itba.ss.simulator.utils.Particle.State;

import java.time.LocalDateTime;
import java.util.*;

import static ar.edu.itba.ss.simulator.algorithms.AlgorithmsUtils.calculateForce;
import static ar.edu.itba.ss.simulator.algorithms.AlgorithmsUtils.calculateInitialR;
import static ar.edu.itba.ss.simulator.utils.R.values.*;
import static java.lang.Math.pow;

public class VerletOriginal {

    public static AlgorithmResults execute(final Particle particle, final State initialState,
                                           final double k, final double gamma,
                                           final double dt, final double tf) {

        final ExecutionTimestamps executionTimestamps = new ExecutionTimestamps();
        executionTimestamps.setAlgorithmStart(LocalDateTime.now());

        final Map<Double, Map<Particle, State>> particlesStates = new TreeMap<>();

        final R initialR = calculateInitialR(particle.getMass(), initialState, k, gamma);
        final Pair initialr0 = initialR.get(R0.ordinal());
        final Pair initialr1 = initialR.get(R1.ordinal());
        particlesStates.put(0.0, Map.of(particle, new State(new Position(initialr0.getX(), initialr0.getY()), initialr1.getX(), initialr1.getY())));

        final List<R> RStates = new ArrayList<>();
        RStates.add(initialR);

        int iterations = 0;
        for (double t = dt; t <= tf; t += dt, iterations += 1) {
            final R prevR = RStates.get(Math.max(iterations - 1, 0));
            final R currentR = RStates.get(iterations);

            final R nextR = calculateNextR(prevR, currentR, particle.getMass(), k, gamma, dt);
            RStates.add(nextR);
        }

        R currentR = RStates.get(RStates.size() - 1);
        final R prevR = RStates.get(RStates.size() - 2);
        // Se updatea la última velocidad
        calculateNextR(prevR, currentR, particle.getMass(), k, gamma, dt);

        iterations = 0;
        for (double t = dt; t <= tf; t += dt, iterations += 1) {
            currentR = RStates.get(iterations);

            final Pair r0 = currentR.get(R0.ordinal());
//            final Pair r1 = currentR.get(R1.ordinal());

            particlesStates.put(t, Map.of(particle, new State(new Position(r0.getX(), r0.getY()), r0.getX(), r0.getY())));
        }

        executionTimestamps.setAlgorithmEnd(LocalDateTime.now());

        return new AlgorithmResults(executionTimestamps, iterations, particlesStates);

    }

    private static R calculateNextR(final R prevR, final R currentR,
                                    final double mass, final double k,
                                    final double gamma, final double dt) {

        final R nextR = new R();

        final Pair currentForce = calculateForce(mass, currentR.get(R0.ordinal()), new Pair(0.0, 0.0), k, gamma);

        final double r0x = 2 * currentR.get(R0.ordinal()).getX() - prevR.get(R0.ordinal()).getX() + (pow(dt, 2) / mass) * currentForce.getX();
        final double r0y = 2 * currentR.get(R0.ordinal()).getY() - prevR.get(R0.ordinal()).getY() + (pow(dt, 2) / mass) * currentForce.getY();
        nextR.add(r0x, r0y);

        return nextR;
    }

}
