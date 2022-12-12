package ar.edu.itba.ss.simulator.simulation;

import ar.edu.itba.ss.simulator.Algorithms.CellIndex.CellIndexMethod;
import ar.edu.itba.ss.simulator.Algorithms.CellIndex.Grid;
import ar.edu.itba.ss.simulator.utils.Pair;
import ar.edu.itba.ss.simulator.utils.Particle;
import ar.edu.itba.ss.simulator.utils.R;
import ar.edu.itba.ss.simulator.utils.RandomGenerator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static ar.edu.itba.ss.simulator.ParticlesGenerator.generateParticleState;
import static ar.edu.itba.ss.simulator.simulation.VibratedSilo.INTERACTION_RADIUS;
import static ar.edu.itba.ss.simulator.utils.R.values.*;
import static java.lang.Math.*;

class VibratedSiloUtils {

    private static final double OPENING_PARTICLE_RADIUS = 0.0;

    static void calculateInitialAccelerations(final Map<Particle, R> initialStates, final double gravity) {
        initialStates.forEach((p, r) -> r.set(R2.ordinal(), 0.0, -gravity));
    }

    static Map<Particle, R> euler(final Map<Particle, R> Rs, final double dt, final double gravity) {

        final Map<Particle, R> rStates = new HashMap<>();

        Rs.forEach((p, r) -> {
            final R eulerR = new R();
            final Pair<Double, Double> r0 = r.get(R0.ordinal());
            final Pair<Double, Double> r1 = r.get(R1.ordinal());
            final Pair<Double, Double> r2 = r.get(R2.ordinal());

            // r0
            double r0x = r0.getKey() + dt * r1.getKey() + (dt * dt / (2 * p.getMass())) * r2.getKey() * p.getMass();
            double r0y = r0.getValue() + dt * r1.getValue() + (dt * dt / (2 * p.getMass())) * r2.getValue() * p.getMass();
            eulerR.set(R0.ordinal(), r0x, r0y);

            // r1
            double r1x = r1.getKey() + (dt / p.getMass()) * r2.getKey() * p.getMass();
            double r1y = r1.getValue() + (dt / p.getMass()) * r2.getValue() * p.getMass();
            eulerR.set(R1.ordinal(), r1x, r1y);

            eulerR.set(R2.ordinal(), 0.0, -gravity);

            rStates.put(p, eulerR);
        });

        return rStates;
    }

    static Map<Particle, R> calculateNextRs(final Map<Particle, R> prevRs,
                                            final Map<Particle, R> currentRs,
                                            final Grid grid,
                                            final double t, final double dt,
                                            final int W, final double D,
                                            final double kn, final double kt,
                                            final double w, final double A, final double gravity, final double gamma,
                                            final double mu, final Map<Pair<Particle, Particle>, Double> accumRelVelPred,
                                            final Map<Pair<Particle, Particle>, Double> accumRelVelCorr) {

        final Map<Particle, R> nextRs = new HashMap<>();

        final double wallR0Y = calculateWallR0Y(A, w, t);
        final double wallR1Y = calculateWallR1Y(A, w, t);

        Iterator<Entry<Particle, R>> prevIt = prevRs.entrySet().iterator();
        Iterator<Entry<Particle, R>> currentIt = currentRs.entrySet().iterator();
        while (prevIt.hasNext() && currentIt.hasNext()) {
            final Entry<Particle, R> prevPair = prevIt.next();
            final Entry<Particle, R> currentPair = currentIt.next();

            final Particle particle = prevPair.getKey();
            final R prevR = prevPair.getValue();
            final R currentR = currentPair.getValue();

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

            nextRs.put(particle, nextR);
        }

        final Map<Particle, Set<Particle>> neighbors = CellIndexMethod.calculateNeighbors(nextRs, grid, INTERACTION_RADIUS);

        prevIt = prevRs.entrySet().iterator();
        currentIt = currentRs.entrySet().iterator();
        while (prevIt.hasNext() && currentIt.hasNext()) {
            final Entry<Particle, R> prevPair = prevIt.next();
            final Entry<Particle, R> currentPair = currentIt.next();

            final Particle particle = prevPair.getKey();
            final R prevR = prevPair.getValue();
            final R currentR = currentPair.getValue();

            final R nextR = nextRs.get(particle);

            final Set<Particle> particleNeighbors = neighbors.get(particle);

            final Pair<Double, Double> r2P = calculateAcceleration(particle, particleNeighbors, nextRs, W, D, kn, kt, wallR0Y, wallR1Y, gravity, gamma, mu, dt, accumRelVelPred);

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
            final R nextR = nextRs.get(particle);

            final Set<Particle> particleNeighbors = neighbors.get(particle);

            final Pair<Double, Double> r2C = calculateAcceleration(particle, particleNeighbors, nextRs, W, D, kn, kt, wallR0Y, wallR1Y, gravity, gamma, mu, dt, accumRelVelCorr);
            nextR.set(R2.ordinal(), r2C.getKey(), r2C.getValue());

            nextRs.put(particle, nextR);
        }

        return nextRs;
    }

    static Map<Particle, R> respawnParticlesOutsideOpening(final Map<Particle, R> currentRs,
                                                           final Set<Particle> particlesJustOutside,
                                                           final Set<Particle> particlesAlreadyOutside,
                                                           final double reenterMinHeight, final double reenterMaxHeight,
                                                           final double exitDistance, final int W,
                                                           final double initialVx, final double initialVy) {

        final Map<Particle, R> particlesOutsideOpeningRs = new HashMap<>();
        currentRs.forEach((p, r) -> {
            Pair<Double, Double> position = r.get(R0.ordinal());
            if (position.getValue() < -p.getRadius() && !particlesAlreadyOutside.contains(p)) {
                particlesJustOutside.add(p);
                particlesAlreadyOutside.add(p);
            }

            if (position.getValue() - p.getRadius() < -exitDistance) {
                final double offset = RandomGenerator.getInstance().getRandom().nextDouble();
                final R newR = generateParticleState(reenterMinHeight + p.getRadius(), reenterMaxHeight - p.getRadius(),
                    p.getRadius() + offset, W - p.getRadius() - offset, initialVx, initialVy, p, currentRs);

                particlesAlreadyOutside.remove(p);
                particlesOutsideOpeningRs.put(p, newR);
            }
        });

        return particlesOutsideOpeningRs;
    }


    private static Pair<Double, Double> calculateAcceleration(final Particle particleI, final Set<Particle> neighbors,
                                                              final Map<Particle, R> currentRs,
                                                              final int W, final double D,
                                                              final double kn, final double kt,
                                                              final double wallR0Y, final double wallR1Y,
                                                              final double gravity, final double gamma, final double mu,
                                                              final double dt,
                                                              final Map<Pair<Particle, Particle>, Double> accumRelVel) {

        final R particleIRs = currentRs.get(particleI);
        final Pair<Double, Double> particleIR0 = particleIRs.get(R0.ordinal());
        final Pair<Double, Double> particleIR1 = particleIRs.get(R1.ordinal());

        double fx = 0;
        double fy = 0;

        //Check collision with other particles
        for (Particle particleJ : neighbors) {
            final R particleJRs = currentRs.get(particleJ);
            final Pair<Particle, Particle> particlesPair = new Pair<>(particleI, particleJ);
            final Pair<Double, Double> collisionForce = collisionForce(particleI.getRadius(), particleIR0, particleIR1,
                particleJ.getRadius(), particleJRs, kn, kt, gamma, mu, dt, accumRelVel, particlesPair);
            fx += collisionForce.getKey();
            fy += collisionForce.getValue();
        }

        // Check collision with walls
        Pair<Double, Double> leftWallForce = new Pair<>(0.0, 0.0);
        Pair<Double, Double> rightWallForce = new Pair<>(0.0, 0.0);
        Pair<Double, Double> bottomWallForce = new Pair<>(0.0, 0.0);
        Pair<Double, Double> leftOpeningParticleForce = new Pair<>(0.0, 0.0);
        Pair<Double, Double> rightOpeningParticleForce = new Pair<>(0.0, 0.0);

        // Left Wall
        final Pair<Particle, Particle> leftPair = new Pair<>(particleI, Particle.LEFT_WALL);
        if (particleIR0.getKey() <= particleI.getRadius()) {
            final R leftWall = new R();
            leftWall.set(R0.ordinal(), -particleI.getRadius(), particleIR0.getValue());
            leftWall.set(R1.ordinal(), 0, 0);
            leftWallForce = collisionForce(particleI.getRadius(), particleIR0, particleIR1, particleI.getRadius(), leftWall, kn, kt, gamma, mu, dt, accumRelVel, leftPair);
        } else {
            accumRelVel.remove(leftPair);
        }

        // Right Wall
        final Pair<Particle, Particle> rightPair = new Pair<>(particleI, Particle.RIGHT_WALL);
        if (particleIR0.getKey() + particleI.getRadius() >= W) {
            final R rightWall = new R();
            rightWall.set(R0.ordinal(), W + particleI.getRadius(), particleIR0.getValue());
            rightWall.set(R1.ordinal(), 0, 0);
            rightWallForce = collisionForce(particleI.getRadius(), particleIR0, particleIR1, particleI.getRadius(), rightWall, kn, kt, gamma, mu, dt, accumRelVel, rightPair);
        } else {
            accumRelVel.remove(rightPair);
        }

        // Bottom Wall
        final Pair<Particle, Particle> bottomPair = new Pair<>(particleI, Particle.BOTTOM_WALL);
        final Pair<Particle, Particle> bottomWallLeftOpeningPair = new Pair<>(particleI, Particle.BOTTOM_WALL_LEFT_OPENING);
        final Pair<Particle, Particle> bottomWallRightOpeningPair = new Pair<>(particleI, Particle.BOTTOM_WALL_RIGHT_OPENING);
        if (particleIR0.getValue() <= particleI.getRadius()) {
            // Outside Opening
            if (!isInOpening(particleIR0, W, D)) {
                accumRelVel.remove(bottomWallLeftOpeningPair);
                accumRelVel.remove(bottomWallRightOpeningPair);
                final R bottomWall = new R();
                bottomWall.set(R0.ordinal(), particleIR0.getKey(), wallR0Y - particleI.getRadius());
                bottomWall.set(R1.ordinal(), 0, wallR1Y);
                bottomWallForce = collisionForce(particleI.getRadius(), particleIR0, particleIR1, particleI.getRadius(), bottomWall, kn, kt, gamma, mu, dt, accumRelVel, bottomPair);
            }
            // Outside Opening
            else {
                // Left Opening
                final R leftOpeningParticleRs = new R();
                leftOpeningParticleRs.set(R0.ordinal(), W / 2.0 - D / 2.0, 0);
                leftOpeningParticleRs.set(R1.ordinal(), 0, wallR1Y);
                leftOpeningParticleForce = collisionForce(particleI.getRadius(), particleIR0, particleIR1, OPENING_PARTICLE_RADIUS, leftOpeningParticleRs, kn, kt, gamma, mu, dt, accumRelVel, bottomWallLeftOpeningPair);

                // Right Opening
                final R rightOpeningParticleRs = new R();
                rightOpeningParticleRs.set(R0.ordinal(), W / 2.0 + D / 2.0, 0);
                rightOpeningParticleRs.set(R1.ordinal(), 0, wallR1Y);
                rightOpeningParticleForce = collisionForce(particleI.getRadius(), particleIR0, particleIR1, OPENING_PARTICLE_RADIUS, rightOpeningParticleRs, kn, kt, gamma, mu, dt, accumRelVel, bottomWallRightOpeningPair);
            }
        } else {
            accumRelVel.remove(bottomPair);
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
                                                       final double kn, final double kt,
                                                       final double gamma, final double mu, final double dt,
                                                       final Map<Pair<Particle, Particle>, Double> accumRelVel,
                                                       final Pair<Particle, Particle> pairIJ) {
        double fx = 0;
        double fy = 0;

        final Pair<Double, Double> particleJR0 = particleJRs.get(R0.ordinal());
        final Pair<Double, Double> particleJR1 = particleJRs.get(R1.ordinal());

        final double deltaR0X = particleJR0.getKey() - particleIR0.getKey();
        final double deltaR0Y = particleJR0.getValue() - particleIR0.getValue();
        final double centerDistance = hypot(deltaR0X, deltaR0Y);

        final double radiusDistance = particleIRadius + particleJRadius;
        final double overlap = radiusDistance - centerDistance;
        Double currentAccumRelVel = accumRelVel.get(pairIJ);

        if (overlap > 0) {

            final double enX = deltaR0X / centerDistance;
            final double enY = deltaR0Y / centerDistance;

            final double etX = -enY;
            final double etY = enX;

            final double R1relX = particleIR1.getKey() - particleJR1.getKey();
            final double R1relY = particleIR1.getValue() - particleJR1.getValue();

            // Normal
            final double relVelN = R1relX * enX + R1relY * enY;
            final double fN = -kn * overlap - gamma * relVelN;

            // Tangential
            final double relVelT = R1relX * etX + R1relY * etY;

            if (currentAccumRelVel == null) {
                currentAccumRelVel = (double) 0;
            }
            currentAccumRelVel += (dt * relVelT);
            accumRelVel.put(pairIJ, currentAccumRelVel);

            final double eT = currentAccumRelVel;
            final double fT1 = -mu * Math.abs(fN) * Math.signum(relVelT);
            final double fT2 = -kt * eT;
//            final double fT = Math.min(fT1, fT2);
            final double fT = fT2;

            fx += fN * enX + fT * etX;
            fy += fN * enY + fT * etY;
        } else {
            accumRelVel.remove(pairIJ);
        }
        return new Pair<>(fx, fy);
    }

    private static double calculateWallR1Y(final double A, final double w, final double t) {
        return A * w * cos(w * t);
    }

    private static double calculateWallR0Y(final double A, final double w, final double t) {
        return A * sin(w * t);
    }

    private static boolean isInOpening(final Pair<Double, Double> particleR0, final int W, final double D) {
        return particleR0.getKey() > (W / 2.0 - D / 2.0) && particleR0.getKey() < (W / 2.0 + D / 2.0);
    }

}
