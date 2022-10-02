package ar.edu.itba.ss.simulator.algorithms;

import ar.edu.itba.ss.simulator.utils.*;

import java.time.LocalDateTime;
import java.util.*;

import static ar.edu.itba.ss.simulator.algorithms.AlgorithmsUtils.calculateInitialR;
import static ar.edu.itba.ss.simulator.utils.MathUtils.fact;
import static ar.edu.itba.ss.simulator.utils.Particle.*;
import static ar.edu.itba.ss.simulator.utils.R.values.*;
import static java.lang.Math.pow;
import static java.util.Map.entry;

public class GearPredictor {

    private static final Map<Integer, List<Double>> posCoefficients = Map.ofEntries(
        entry(2, Arrays.asList(0.0, 1.0, 1.0)),
        entry(3, Arrays.asList(1.0 / 6, 5.0 / 6, 1.0, 1.0 / 3)),
        entry(4, Arrays.asList(19.0 / 120, 3.0 / 4, 1.0, 1.0 / 2, 1.0 / 12)),
        entry(5, Arrays.asList(3.0 / 20, 251.0 / 360, 1.0, 11.0 / 18, 1.0 / 6, 1.0 / 60)));

    private static final Map<Integer, List<Double>> posSpeedCoefficients = Map.ofEntries(
        entry(2, Arrays.asList(0.0, 1.0, 1.0)),
        entry(3, Arrays.asList(1.0 / 6, 5.0 / 6, 1.0, 1.0 / 3)),
        entry(4, Arrays.asList(19.0 / 90, 3.0 / 4, 1.0, 1.0 / 2, 1.0 / 12)),
        entry(5, Arrays.asList(3.0 / 16, 251.0 / 360, 1.0, 11.0 / 18, 1.0 / 6, 1.0 / 60)));

    private static final int TOTAL_PREDICTIONS = 6;

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

        int iterations = 0;
        int totalIterations = (int) (tf / dt);
        for (double t = dt; iterations < totalIterations; t += dt, iterations += 1) {

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

    public static R predict(final R currentR, final double dt) {

        final R newPredictions = new R();

        for (int i = 0; i < TOTAL_PREDICTIONS; i++) {
            double rpx = 0;
            double rpy = 0;

            for (int j = i; j < TOTAL_PREDICTIONS; j++) {
                final Pair rj = currentR.get(j);
                rpx += rj.getX() * pow(dt, j - i) / fact(j - i);
                rpy += rj.getY() * pow(dt, j - i) / fact(j - i);
            }
            newPredictions.add(rpx, rpy);
        }

        return newPredictions;
    }


    private static Pair getDeltaR2(final double mass, final double k, final double gamma,
                                   final Pair r0p, final Pair r1p,
                                   final Pair r2p, final double dt) {
        //Evalúo la aceleracion con la posicion y velocidad predecida
        final double r2x = (-k * r0p.getX() - gamma * r1p.getX()) / mass;
        final double r2y = (-k * r0p.getY() - gamma * r1p.getY()) / mass;

        //Aceleración predecida
        final double r2px = r2p.getX();
        final double r2py = r2p.getY();

        final double deltar2x = r2x - r2px;
        final double deltar2y = r2y - r2py;

        final double deltaR2x = deltar2x * pow(dt, 2) / fact(2);
        final double deltaR2y = deltar2y * pow(dt, 2) / fact(2);

        return new Pair(deltaR2x, deltaR2y);
    }

    private static R correct(final R predictions,
                             final Pair deltaR2, final double dt) {
        final R corrections = new R();

        for (int i = 0; i < TOTAL_PREDICTIONS; i++) {
            Pair rpi = predictions.get(i);

            final double rcx = rpi.getX() + GearPredictor.posSpeedCoefficients.get(5).get(i) * deltaR2.getX() * fact(i) / pow(dt, i);
            final double rcy = rpi.getY() + GearPredictor.posSpeedCoefficients.get(5).get(i) * deltaR2.getY() * fact(i) / pow(dt, i);

            corrections.add(rcx, rcy);
        }

        return corrections;
    }


}
