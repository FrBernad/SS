package ar.edu.itba.ss.simulator.algorithms;

import ar.edu.itba.ss.simulator.utils.*;
import ar.edu.itba.ss.simulator.utils.Particle.Position;
import ar.edu.itba.ss.simulator.utils.Particle.State;

import java.time.LocalDateTime;
import java.util.*;

import static ar.edu.itba.ss.simulator.algorithms.AlgorithmsUtils.*;
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
        final List<R> RStates = new ArrayList<>();
        RStates.add(initialR);

        final Pair initialr0 = initialR.get(R0.ordinal());
        final Pair initialr1 = initialR.get(R1.ordinal());
        particlesStates.put(0.0, Map.of(particle, new State(new Position(initialr0.getX(), initialr0.getY()), initialr1.getX(), initialr1.getY())));

        R prevR = euler(initialR, -dt, particle.getMass(), k, gamma);

        int iterations = 0;
        int totalIterations = (int) (tf / dt);
        for (double t = dt; iterations < totalIterations; t += dt, iterations += 1) {

            final R currentR = RStates.get(iterations);

            final R nextR = calculateNextR(prevR, currentR, particle.getMass(), k, gamma, dt);

            final Pair r0 = nextR.get(R0.ordinal());
            final Pair r1 = nextR.get(R1.ordinal());
            particlesStates.put(t, Map.of(particle, new State(new Position(r0.getX(), r0.getY()), r1.getX(), r1.getY())));

            RStates.add(nextR);
            prevR = currentR;
        }

        executionTimestamps.setAlgorithmEnd(LocalDateTime.now());

        return new AlgorithmResults(executionTimestamps, iterations, particlesStates);

    }

    private static R calculateNextR(final R prevR, final R currentR,
                                    final double mass, final double k,
                                    final double gamma, final double dt) {

        final R nextR = new R();

        final Pair r2 = calculateAcceleration(mass, currentR.get(R0.ordinal()), currentR.get(R1.ordinal()), k, gamma);

        final double r0x = 2 * currentR.get(R0.ordinal()).getX() - prevR.get(R0.ordinal()).getX() + (pow(dt, 2) / mass) * mass * r2.getX();
        final double r0y = 2 * currentR.get(R0.ordinal()).getY() - prevR.get(R0.ordinal()).getY() + (pow(dt, 2) / mass) * mass * r2.getY();
        nextR.add(r0x, r0y);

        final double r1x = (r0x - prevR.get(R0.ordinal()).getX()) / (2 * dt);
        final double r1y = (r0y - prevR.get(R0.ordinal()).getY()) / (2 * dt);
        nextR.add(r1x, r1y);

        return nextR;
    }

}
