package ar.edu.itba.ss.simulator.simulation;

import ar.edu.itba.ss.simulator.Algorithms.CellIndex.CellIndexMethod;
import ar.edu.itba.ss.simulator.Algorithms.CellIndex.Grid;
import ar.edu.itba.ss.simulator.utils.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static ar.edu.itba.ss.simulator.ParticlesGenerator.generateParticleState;
import static ar.edu.itba.ss.simulator.simulation.VibratedSilo.INTERACTION_RADIUS;
import static ar.edu.itba.ss.simulator.utils.R.values.*;
import static java.lang.Math.hypot;
import static java.lang.Math.sin;

class VibratedSiloUtilsPhase {

    private static final double OPENING_PARTICLE_RADIUS = 0.0;

    static void calculateInitialAccelerations(final Map<Particle, Pair<Double, R>> initialStates, final double gravity) {
        initialStates.forEach((p, r) -> r.getValue().set(R2.ordinal(), 0.0, -gravity));
    }

    static Map<Particle, Pair<Double, R>> euler(final Map<Particle, Pair<Double, R>> Rs, final double dt, final double t, final double gravity,
                                                final double A, final double w, final double radiusR0) {

        final Map<Particle, Pair<Double, R>> rStates = new HashMap<>();

        Rs.forEach((p, r) -> {
            final R eulerR = new R();
            final Pair<Double, Double> r0 = r.getValue().get(R0.ordinal());
            final Pair<Double, Double> r1 = r.getValue().get(R1.ordinal());
            final Pair<Double, Double> r2 = r.getValue().get(R2.ordinal());

            // r0
            double r0x = r0.getKey() + dt * r1.getKey() + (dt * dt / (2 * p.getMass())) * r2.getKey() * p.getMass();
            double r0y = r0.getValue() + dt * r1.getValue() + (dt * dt / (2 * p.getMass())) * r2.getValue() * p.getMass();
            eulerR.set(R0.ordinal(), r0x, r0y);

            // r1
            double r1x = r1.getKey() + (dt / p.getMass()) * r2.getKey() * p.getMass();
            double r1y = r1.getValue() + (dt / p.getMass()) * r2.getValue() * p.getMass();
            eulerR.set(R1.ordinal(), r1x, r1y);

            eulerR.set(R2.ordinal(), 0.0, -gravity);

            rStates.put(p, new Pair<>(calculateNewRadius(p, A, w, t, radiusR0), eulerR));
        });

        return rStates;
    }

    static Map<Particle, Pair<Double, R>> calculateNextRs(final Map<Particle, Pair<Double, R>> prevRs,
                                                          final Map<Particle, Pair<Double, R>> currentRs,
                                                          final Grid grid,
                                                          final double t, final double dt,
                                                          final int W, final double D,
                                                          final double kn, final double kt,
                                                          final double w, final double A, final double gravity, final double radiusR0) {

        final Map<Particle, Pair<Double, R>> nextRs = new HashMap<>();
        final Map<Particle, R> currentRPos = new HashMap<>();
        currentRs.forEach((k, v) -> currentRPos.put(k, v.getValue()));

        final Map<Particle, Set<Particle>> neighbors = CellIndexMethod.calculateNeighbors(currentRPos, grid, INTERACTION_RADIUS);

        Iterator<Entry<Particle, Pair<Double, R>>> prevIt = prevRs.entrySet().iterator();
        Iterator<Entry<Particle, Pair<Double, R>>> currentIt = currentRs.entrySet().iterator();
        while (prevIt.hasNext() && currentIt.hasNext()) {
            final Entry<Particle, Pair<Double, R>> prevPair = prevIt.next();
            final Entry<Particle, Pair<Double, R>> currentPair = currentIt.next();

            final Particle particle = prevPair.getKey();
            final R prevR = prevPair.getValue().getValue();
            final R currentR = currentPair.getValue().getValue();

            final R nextR = new R();

            final double r0x = currentR.get(R0.ordinal()).getKey() + currentR.get(R1.ordinal()).getKey() * dt +
                ((2.0 / 3) * currentR.get(R2.ordinal()).getKey() - (1.0 / 6) * prevR.get(R2.ordinal()).getKey()) * dt * dt;
            final double r0y = currentR.get(R0.ordinal()).getValue() + currentR.get(R1.ordinal()).getValue() * dt +
                ((2.0 / 3) * currentR.get(R2.ordinal()).getValue() - (1.0 / 6) * prevR.get(R2.ordinal()).getValue()) * dt * dt;

            nextR.set(R0.ordinal(), r0x, r0y);

            //Velocity predictions
            final double r1Px = currentR.get(R1.ordinal()).getKey() +
                ((3.0 / 2) * currentR.get(R2.ordinal()).getKey() - (1.0 / 2) * prevR.get(R2.ordinal()).getKey()) * dt;
            final double r1Py = currentR.get(R1.ordinal()).getValue() +
                ((3.0 / 2) * currentR.get(R2.ordinal()).getValue() - (1.0 / 2) * prevR.get(R2.ordinal()).getValue()) * dt;

            nextR.set(R1.ordinal(), r1Px, r1Py);

            nextRs.put(particle, new Pair<>(calculateNewRadius(particle, A, w, t, radiusR0), nextR));
        }

        prevIt = prevRs.entrySet().iterator();
        currentIt = currentRs.entrySet().iterator();
        while (prevIt.hasNext() && currentIt.hasNext()) {
            final Entry<Particle, Pair<Double, R>> prevPair = prevIt.next();
            final Entry<Particle, Pair<Double, R>> currentPair = currentIt.next();

            final Particle particle = prevPair.getKey();
            final R prevR = prevPair.getValue().getValue();
            final R currentR = currentPair.getValue().getValue();

            final R nextR = nextRs.get(particle).getValue();

            final Set<Particle> particleNeighbors = neighbors.get(particle);

            final Pair<Double, Double> r2P = calculateAcceleration(particle, particleNeighbors, nextRs, W, D, kn, kt, gravity);

            //Velocity correction
            final double r1Cx = currentR.get(R1.ordinal()).getKey() +
                ((1.0 / 3) * r2P.getKey() + (5.0 / 6) * currentR.get(R2.ordinal()).getKey() -
                    (1.0 / 6) * prevR.get(R2.ordinal()).getKey()) * dt;
            final double r1Cy = currentR.get(R1.ordinal()).getValue() +
                ((1.0 / 3) * r2P.getValue() + (5.0 / 6) * currentR.get(R2.ordinal()).getValue() -
                    (1.0 / 6) * prevR.get(R2.ordinal()).getValue()) * dt;

            nextR.set(R1.ordinal(), r1Cx, r1Cy);
        }

        for (Particle particle : currentRs.keySet()) {
            final Pair<Double, R> nextRRadius = nextRs.get(particle);

            final Set<Particle> particleNeighbors = neighbors.get(particle);

            final Pair<Double, Double> r2C = calculateAcceleration(particle, particleNeighbors, nextRs, W, D, kn, kt, gravity);
            nextRRadius.getValue().set(R2.ordinal(), r2C.getKey(), r2C.getValue());

            nextRs.put(particle, nextRRadius);
        }

        return nextRs;
    }

    private static Pair<Double, Double> calculateAcceleration(final Particle particleI, final Set<Particle> neighbors,
                                                              final Map<Particle, Pair<Double, R>> currentRs,
                                                              final int W, final double D,
                                                              final double kn, final double kt, final double gravity) {

        final Pair<Double, R> particleIRRadius = currentRs.get(particleI);
        final Pair<Double, Double> particleIR0 = particleIRRadius.getValue().get(R0.ordinal());
        final Pair<Double, Double> particleIR1 = particleIRRadius.getValue().get(R1.ordinal());

        double fx = 0;
        double fy = 0;

        //Check collision with other particles
        for (Particle particleJ : neighbors) {
            final Pair<Double, R> particleJRRadius = currentRs.get(particleJ);
            if (particleJ != particleI) {
                final Pair<Double, Double> collisionForce = collisionForce(particleIRRadius.getKey(), particleIR0, particleIR1,
                    particleJRRadius.getKey(), particleJRRadius.getValue(), kn, kt);
                fx += collisionForce.getKey();
                fy += collisionForce.getValue();
            }
        }

        // Check collision with walls
        Pair<Double, Double> leftWallForce = new Pair<>(0.0, 0.0);
        Pair<Double, Double> rightWallForce = new Pair<>(0.0, 0.0);
        Pair<Double, Double> bottomWallForce = new Pair<>(0.0, 0.0);
        Pair<Double, Double> leftOpeningParticleForce = new Pair<>(0.0, 0.0);
        Pair<Double, Double> rightOpeningParticleForce = new Pair<>(0.0, 0.0);

        // Left Wall
        if (particleIR0.getKey() <= particleIRRadius.getKey()) {
            final R leftWall = new R();
            leftWall.set(R0.ordinal(), 0, particleIR0.getValue());
            leftWall.set(R1.ordinal(), 0, 0);
            leftWallForce = collisionForce(particleIRRadius.getKey(), particleIR0, particleIR1, 0, leftWall, kt, kn);
        }

        // Right Wall
        if (particleIR0.getKey() + particleIRRadius.getKey() >= W) {
            final R rightWall = new R();
            rightWall.set(R0.ordinal(), W, particleIR0.getValue());
            rightWall.set(R1.ordinal(), 0, 0);
            rightWallForce = collisionForce(particleIRRadius.getKey(), particleIR0, particleIR1, 0, rightWall, kt, kn);
        }

        // Bottom Wall
        if (particleIR0.getValue() <= particleIRRadius.getKey()) {
            // Outside Opening
            if (!isInOpening(particleIR0, W, D)) {
                final R bottomWall = new R();
                bottomWall.set(R0.ordinal(), particleIR0.getKey(), 0);
                bottomWall.set(R1.ordinal(), 0, 0);
                bottomWallForce = collisionForce(particleIRRadius.getKey(), particleIR0, particleIR1, 0, bottomWall, kt, kn);
            }
            // Outside Opening
            else {
                // Left Opening
                final R leftOpeningParticleRs = new R();
                leftOpeningParticleRs.set(R0.ordinal(), W / 2.0 - D / 2.0, 0);
                leftOpeningParticleRs.set(R1.ordinal(), 0, 0);
                leftOpeningParticleForce = collisionForce(particleIRRadius.getKey(), particleIR0, particleIR1, OPENING_PARTICLE_RADIUS, leftOpeningParticleRs, kt, kn);

                // Right Opening
                final R rightOpeningParticleRs = new R();
                rightOpeningParticleRs.set(R0.ordinal(), W / 2.0 + D / 2.0, 0);
                rightOpeningParticleRs.set(R1.ordinal(), 0, 0);
                rightOpeningParticleForce = collisionForce(particleIRRadius.getKey(), particleIR0, particleIR1, OPENING_PARTICLE_RADIUS, rightOpeningParticleRs, kt, kn);
            }
        }

        fx += leftWallForce.getKey() + rightWallForce.getKey() + bottomWallForce.getKey() + leftOpeningParticleForce.getKey() + rightOpeningParticleForce.getKey();
        fy += leftWallForce.getValue() + rightWallForce.getValue() + bottomWallForce.getValue() + leftOpeningParticleForce.getValue() + rightOpeningParticleForce.getValue();

        // Gravity Force
        fy -= particleI.getMass() * gravity;

        return new Pair<>(fx / particleI.getMass(), fy / particleI.getMass());
    }


    private static Pair<Double, Double> collisionForce(final double particleIRadius, final Pair<Double, Double> particleIR0,
                                                       final Pair<Double, Double> particleIR1,
                                                       final double particleJRadius, final R particleJRs,
                                                       final double kn, final double kt) {
        double fx = 0;
        double fy = 0;

        final Pair<Double, Double> particleJR0 = particleJRs.get(R0.ordinal());
        final Pair<Double, Double> particleJR1 = particleJRs.get(R1.ordinal());

        final double deltaR0X = particleJR0.getKey() - particleIR0.getKey();
        final double deltaR0Y = particleJR0.getValue() - particleIR0.getValue();
        final double centerDistance = hypot(deltaR0X, deltaR0Y);

        final double radiusDistance = particleIRadius + particleJRadius;
        final double overlap = radiusDistance - centerDistance;

        if (overlap >= 0) {
            final double enX = deltaR0X / centerDistance;
            final double enY = deltaR0Y / centerDistance;

            final double etX = -enY;
            final double etY = enX;

            final double fN = -kn * overlap;

            final double R1relX = particleIR1.getKey() - particleJR1.getKey();
            final double R1relY = particleIR1.getValue() - particleJR1.getValue();

            final double scalarProd = R1relX * etX + R1relY * etY;

            final double fT = -kt * overlap * scalarProd;

            fx += fN * enX + fT * etX;
            fy += fN * enY + fT * etY;
        }
        return new Pair<>(fx, fy);
    }

    static Map<Particle, Pair<Double, R>> respawnParticlesOutsideOpening(final Map<Particle, Pair<Double, R>> currentRs,
                                                                         final Set<Particle> particlesJustOutside,
                                                                         final Set<Particle> particlesAlreadyOutside,
                                                                         final double reenterMinHeight, final double reenterMaxHeight,
                                                                         final double exitDistance, final int W,
                                                                         final double initialVx, final double initialVy) {

        final Map<Particle, Pair<Double, R>> particlesOutsideOpeningRs = new HashMap<>();
        final Map<Particle, R> currentRPos = new HashMap<>();
        currentRs.forEach((k, v) -> currentRPos.put(k, v.getValue()));

        currentRs.forEach((p, r) -> {
            Pair<Double, Double> position = r.getValue().get(R0.ordinal());
            if (position.getValue() < -p.getRadius() && !particlesAlreadyOutside.contains(p)) {
                particlesJustOutside.add(p);
                particlesAlreadyOutside.add(p);
            }

            if (position.getValue() - r.getKey() < -exitDistance) {
                final double offset = RandomGenerator.getInstance().getRandom().nextDouble();
                final R newR = generateParticleState(reenterMinHeight + p.getRadius(), reenterMaxHeight - p.getRadius(),
                    p.getRadius() + offset, W - p.getRadius() - offset, initialVx, initialVy, p, currentRPos);

                particlesAlreadyOutside.remove(p);
                r.setValue(newR);
                particlesOutsideOpeningRs.put(p, r);
            }
        });

        return particlesOutsideOpeningRs;
    }


    private static double calculateNewRadius(final Particle particle, final double A, final double w, final double t, final double radiusR0) {
        ParticleWithPhase p = (ParticleWithPhase) particle;
        return radiusR0 * (1 + A * sin(w * t + p.getPhase()));
    }

    private static boolean isInOpening(final Pair<Double, Double> particleR0, final int W, final double D) {
        return particleR0.getKey() > (W / 2.0 - D / 2.0) && particleR0.getKey() < (W / 2.0 + D / 2.0);
    }

}
