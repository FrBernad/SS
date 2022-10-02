package ar.edu.itba.ss.simulator.algorithms;

import ar.edu.itba.ss.simulator.utils.*;
import ar.edu.itba.ss.simulator.utils.Particle.Position;
import ar.edu.itba.ss.simulator.utils.Particle.State;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static ar.edu.itba.ss.simulator.algorithms.AlgorithmsUtils.*;
import static ar.edu.itba.ss.simulator.utils.R.values.*;
import static java.lang.Math.pow;

public class Beeman {

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
        int totalIterations = (int) Math.ceil(tf / dt);
        for (double t = dt; iterations < totalIterations; t += dt, iterations += 1) {

            final R currentR = RStates.get(iterations);

            final R nextR = calculateNextR(prevR, currentR, dt, particle.getMass(), k, gamma);

            final Pair r0 = nextR.get(R0.ordinal());
            final Pair r1 = nextR.get(R1.ordinal());
            if (Double.compare(t, tf - 2 * dt) == 0) {
                System.out.println();
            }
            particlesStates.put(t, Map.of(particle, new State(new Position(r0.getX(), r0.getY()), r1.getX(), r1.getY())));

            RStates.add(nextR);
            prevR = currentR;
        }

        executionTimestamps.setAlgorithmEnd(LocalDateTime.now());

        return new AlgorithmResults(executionTimestamps, iterations, particlesStates);
    }

    private static R calculateNextR(final R prevR, final R currentR, double dt, double mass, double k, double gamma) {
        final R nextR = new R();

        final double r0x = currentR.get(R0.ordinal()).getX() + currentR.get(R1.ordinal()).getX() * dt +
            ((2.0 / 3) * currentR.get(R2.ordinal()).getX() - (1.0 / 6) * prevR.get(R2.ordinal()).getX()) * pow(dt, 2);
        final double r0y = currentR.get(R0.ordinal()).getY() + currentR.get(R1.ordinal()).getY() * dt +
            ((2.0 / 3) * currentR.get(R2.ordinal()).getY() - (1.0 / 6) * prevR.get(R2.ordinal()).getY()) * pow(dt, 2);
        nextR.add(r0x, r0y);

        //Velocity predictions
        final double r1Px = currentR.get(R1.ordinal()).getX() +
            ((3.0 / 2) * currentR.get(R2.ordinal()).getX() - (1.0 / 2) * prevR.get(R2.ordinal()).getX()) * dt;
        final double r1Py = currentR.get(R1.ordinal()).getY() +
            ((3.0 / 2) * currentR.get(R2.ordinal()).getY() - (1.0 / 2) * prevR.get(R2.ordinal()).getY()) * dt;

        final Pair r2P = calculateAcceleration(mass, new Pair(r0x, r0y), new Pair(r1Px, r1Py), k, gamma);

        //Velocity correction
        final double r1Cx = currentR.get(R1.ordinal()).getX() +
            ((1.0 / 3) * r2P.getX() + (5.0 / 6) * currentR.get(R2.ordinal()).getX() -
                (1.0 / 6) * prevR.get(R2.ordinal()).getX()) * dt;
        final double r1Cy = currentR.get(R1.ordinal()).getY() +
            ((1.0 / 3) * r2P.getY() + (5.0 / 6) * currentR.get(R2.ordinal()).getY() -
                (1.0 / 6) * prevR.get(R2.ordinal()).getY()) * dt;
        nextR.add(r1Cx, r1Cy);

        final Pair r2C = calculateAcceleration(mass, new Pair(r0x, r0y), new Pair(r1Cx, r1Cy), k, gamma);
        nextR.add(r2C.getX(), r2C.getY());

        return nextR;

    }
}
