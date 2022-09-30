package ar.edu.itba.ss.simulator.simulation;

import ar.edu.itba.ss.simulator.utils.*;
import ar.edu.itba.ss.simulator.utils.Particle.State;

import java.time.LocalDateTime;
import java.util.*;

import static ar.edu.itba.ss.simulator.utils.MathUtils.fact;
import static ar.edu.itba.ss.simulator.utils.Particle.*;
import static ar.edu.itba.ss.simulator.utils.R.values.*;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.util.Map.entry;

public class SpaceMission {

    private static final Map<Integer, List<Double>> posCoefficients = Map.ofEntries(
        entry(2, Arrays.asList(0.0, 1.0, 1.0)),
        entry(3, Arrays.asList(1.0 / 6, 5.0 / 6, 1.0, 1.0 / 3)),
        entry(4, Arrays.asList(19.0 / 120, 3.0 / 4, 1.0, 1.0 / 2, 1.0 / 12)),
        entry(5, Arrays.asList(3.0 / 20, 251.0 / 360, 1.0, 11.0 / 18, 1.0 / 6, 1.0 / 60)));

    private static final Double G = 6.693 * pow(10, -11) / pow(10, 9); //Divido para pasarla a km
    private static final int TOTAL_PREDICTIONS = 6;

    public static AlgorithmResults execute(final Map<Particle, State> initialStates,
                                           final double dt, final double tf) {
        final ExecutionTimestamps executionTimestamps = new ExecutionTimestamps();
        executionTimestamps.setAlgorithmStart(LocalDateTime.now());

        final Map<Double, Map<Particle, State>> particlesStates = new TreeMap<>();

        int iterations = 0;
        final Map<Particle, R> initialRs = calculateInitialRs(initialStates);
        storeStates(particlesStates, initialRs, 0.0);

        Map<Particle, R> currentRs = initialRs;

        for (double t = dt; t <= tf; t += dt, iterations += 1) {
            // Predict
            final Map<Particle, R> predictions = predict(currentRs, dt);

            // Calculate DeltaR
            final Map<Particle, Pair> deltasR2 = getDeltaR2(predictions, dt);

            // Correct
            currentRs = correct(predictions, deltasR2, dt);

            storeStates(particlesStates, currentRs, t);
        }

        executionTimestamps.setAlgorithmEnd(LocalDateTime.now());

        return new AlgorithmResults(executionTimestamps, iterations, particlesStates);
    }

    private static Map<Particle, R> calculateInitialRs(final Map<Particle, State> initialStates) {
        final Map<Particle, R> rStates = new HashMap<>();
        initialStates.forEach((p, s) -> {
            final R r = new R();
            r.add(s.getPosition().getX(), s.getPosition().getY());
            r.add(s.getVelocityX(), s.getVelocityY());
            rStates.put(p, r);
        });

        final Map<Particle, R> initialRs = new HashMap<>();

        initialStates.forEach((p, s) -> {
            final R initialR = new R();
            //r0
            initialR.add(s.getPosition().getX(), s.getPosition().getY());
            //r1
            initialR.add(s.getVelocityX(), s.getVelocityY());
            //r2
            final Pair r2 = calculateAcceleration(p, initialR.get(R0.ordinal()), rStates);
            initialR.add(r2.getX(), r2.getY());
            //r3
            initialR.add(0, 0);
            //r4
            initialR.add(0, 0);
            //r5
            initialR.add(0, 0);

            initialRs.put(p, initialR);
        });

        return initialRs;
    }

    public static Map<Particle, R> predict(Map<Particle, R> currentRs, final double dt) {

        final Map<Particle, R> predictions = new HashMap<>();

        currentRs.forEach((p, r) -> {
            final R prediction = new R();

            for (int i = 0; i < TOTAL_PREDICTIONS; i++) {
                double rpx = 0;
                double rpy = 0;

                for (int j = i; j < TOTAL_PREDICTIONS; j++) {
                    final Pair rj = r.get(j);
                    rpx += rj.getX() * pow(dt, j - i) / fact(j - i);
                    rpy += rj.getY() * pow(dt, j - i) / fact(j - i);
                }
                prediction.add(rpx, rpy);
            }
            predictions.put(p, prediction);
        });

        return predictions;
    }

    private static Map<Particle, Pair> getDeltaR2(Map<Particle, R> predictionsRs, double dt) {

        Map<Particle, Pair> deltaR2 = new HashMap<>();

        //Por cada partícula hay que evaluar la aceleración
        predictionsRs.forEach((p, rp) -> {

            //Evalúo la aceleración con la posición predecida
            final Pair r2 = calculateAcceleration(p, rp.get(R0.ordinal()), predictionsRs);

            //Aceleración predecida
            final double r2px = rp.get(R2.ordinal()).getX();
            final double r2py = rp.get(R2.ordinal()).getY();

            final double deltar2x = r2.getX() - r2px;
            final double deltar2y = r2.getY() - r2py;

            final double deltaR2x = deltar2x * pow(dt, 2) / fact(2);
            final double deltaR2y = deltar2y * pow(dt, 2) / fact(2);

            deltaR2.put(p, new Pair(deltaR2x, deltaR2y));
        });

        return deltaR2;
    }

    private static Map<Particle, R> correct(Map<Particle, R> predictions, final Map<Particle, Pair> deltasR2, final double dt) {
        final Map<Particle, R> corrections = new HashMap<>();

        predictions.forEach((p, r) -> {
            final R correction = new R();
            for (int i = 0; i < TOTAL_PREDICTIONS; i++) {
                final Pair rpi = predictions.get(p).get(i);

                final double rcx = rpi.getX() + posCoefficients.get(5).get(i) * deltasR2.get(p).getX() * fact(i) / pow(dt, i);
                final double rcy = rpi.getY() + posCoefficients.get(5).get(i) * deltasR2.get(p).getY() * fact(i) / pow(dt, i);

                correction.add(rcx, rcy);
            }
            corrections.put(p, correction);
        });

        return corrections;
    }

    private static Pair calculateAcceleration(final Particle currentParticle,
                                              final Pair currentParticleR0,
                                              final Map<Particle, R> initialStates) {
        double fx = 0;
        double fy = 0;

        for (Map.Entry<Particle, R> entry : initialStates.entrySet()) {
            Particle p = entry.getKey();
            R rs = entry.getValue();
            if (p != currentParticle) {
                double deltaR0x = rs.get(R0.ordinal()).getX() - currentParticleR0.getX();
                double deltaR0y = rs.get(R0.ordinal()).getY() - currentParticleR0.getY();
                double distance = sqrt(pow((deltaR0x), 2) + pow((deltaR0y), 2));
                double ex = deltaR0x / distance;
                double ey = deltaR0y / distance;
                fx += G * currentParticle.getMass() * p.getMass() * ex / pow(distance, 2);
                fy += G * currentParticle.getMass() * p.getMass() * ey / pow(distance, 2);
            }
        }

        return new Pair(fx / currentParticle.getMass(), fy / currentParticle.getMass());
    }

    private static void storeStates(final Map<Double, Map<Particle, State>> particlesState,
                                    final Map<Particle, R> rMap,
                                    final double instant) {

        Map<Particle, State> state = new HashMap<>();

        rMap.forEach((p, r) -> {
            Position position = new Position(r.get(R0.ordinal()).getX(), r.get(R0.ordinal()).getY());
            state.put(p, new State(position, r.get(R1.ordinal()).getX(), r.get(R1.ordinal()).getY()));
        });

        particlesState.put(instant, state);
    }

}
