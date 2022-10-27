package ar.edu.itba.ss.simulator.simulation;

import ar.edu.itba.ss.simulator.Algorithms.CellIndex.CellIndexMethod;
import ar.edu.itba.ss.simulator.Algorithms.CellIndex.Grid;
import ar.edu.itba.ss.simulator.utils.Pair;
import ar.edu.itba.ss.simulator.utils.Particle;
import ar.edu.itba.ss.simulator.utils.R;

import java.util.*;
import java.util.Map.Entry;

import static ar.edu.itba.ss.simulator.ParticlesGenerator.generateParticleState;
import static ar.edu.itba.ss.simulator.simulation.VibratedSilo.INTERACTION_RADIUS;
import static ar.edu.itba.ss.simulator.utils.R.values.*;
import static java.lang.Math.*;

class VibratedSiloUtils {

    private static final double GRAVITY = 5;
    private static final double OPENING_PARTICLE_RADIUS = 0.1;

    static void calculateInitialAccelerations(final Map<Particle, R> initialStates) {
        initialStates.forEach((p, r) -> r.set(R2.ordinal(), 0.0, -GRAVITY));
    }

    static Map<Particle, R> euler(final Map<Particle, R> Rs, final double dt) {

        final Map<Particle, R> rStates = new HashMap<>();

        Rs.forEach((p, r) -> {
            final R eulerR = new R();
            final Pair r0 = r.get(R0.ordinal());
            final Pair r1 = r.get(R1.ordinal());
            final Pair r2 = r.get(R2.ordinal());

            // r0
            double r0x = r0.getX() + dt * r1.getX() + (dt * dt / (2 * p.getMass())) * r2.getX() * p.getMass();
            double r0y = r0.getY() + dt * r1.getY() + (dt * dt / (2 * p.getMass())) * r2.getY() * p.getMass();
            eulerR.set(R0.ordinal(), r0x, r0y);

            // r1
            double r1x = r1.getX() + (dt / p.getMass()) * r2.getX() * p.getMass();
            double r1y = r1.getY() + (dt / p.getMass()) * r2.getY() * p.getMass();
            eulerR.set(R1.ordinal(), r1x, r1y);

            eulerR.set(R2.ordinal(), 0.0, -GRAVITY);

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
                                            final double w, final double A) {

        final Map<Particle, R> rStates = new HashMap<>();

        final Map<Particle, Set<Particle>> neighbors = CellIndexMethod.calculateNeighbors(currentRs, grid, INTERACTION_RADIUS);

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

            final double r0x = currentR.get(R0.ordinal()).getX() + currentR.get(R1.ordinal()).getX() * dt +
                ((2.0 / 3) * currentR.get(R2.ordinal()).getX() - (1.0 / 6) * prevR.get(R2.ordinal()).getX()) * dt * dt;
            final double r0y = currentR.get(R0.ordinal()).getY() + currentR.get(R1.ordinal()).getY() * dt +
                ((2.0 / 3) * currentR.get(R2.ordinal()).getY() - (1.0 / 6) * prevR.get(R2.ordinal()).getY()) * dt * dt;

            nextR.set(R0.ordinal(), r0x, r0y);

            //Velocity predictions
            final double r1Px = currentR.get(R1.ordinal()).getX() +
                ((3.0 / 2) * currentR.get(R2.ordinal()).getX() - (1.0 / 2) * prevR.get(R2.ordinal()).getX()) * dt;
            final double r1Py = currentR.get(R1.ordinal()).getY() +
                ((3.0 / 2) * currentR.get(R2.ordinal()).getY() - (1.0 / 2) * prevR.get(R2.ordinal()).getY()) * dt;

            nextR.set(R1.ordinal(), r1Px, r1Py);

            rStates.put(particle, nextR);
        }

        prevIt = prevRs.entrySet().iterator();
        currentIt = currentRs.entrySet().iterator();
        while (prevIt.hasNext() && currentIt.hasNext()) {
            final Entry<Particle, R> prevPair = prevIt.next();
            final Entry<Particle, R> currentPair = currentIt.next();

            final Particle particle = prevPair.getKey();
            final R prevR = prevPair.getValue();
            final R currentR = currentPair.getValue();

            final R nextR = rStates.get(particle);

            final Set<Particle> particleNeighbors = neighbors.get(particle);

            final Pair r2P = calculateAcceleration(particle, particleNeighbors, currentRs, W, D, kn, kt, wallR0Y, wallR1Y);

            //Velocity correction
            final double r1Cx = currentR.get(R1.ordinal()).getX() +
                ((1.0 / 3) * r2P.getX() + (5.0 / 6) * currentR.get(R2.ordinal()).getX() -
                    (1.0 / 6) * prevR.get(R2.ordinal()).getX()) * dt;
            final double r1Cy = currentR.get(R1.ordinal()).getY() +
                ((1.0 / 3) * r2P.getY() + (5.0 / 6) * currentR.get(R2.ordinal()).getY() -
                    (1.0 / 6) * prevR.get(R2.ordinal()).getY()) * dt;

            nextR.set(R1.ordinal(), r1Cx, r1Cy);

        }

        for (Particle particle : currentRs.keySet()) {
            final R nextR = rStates.get(particle);

            final Set<Particle> particleNeighbors = neighbors.get(particle);

            final Pair r2C = calculateAcceleration(particle, particleNeighbors, currentRs, W, D, kn, kt, wallR0Y, wallR1Y);
            nextR.set(R2.ordinal(), r2C.getX(), r2C.getY());

            rStates.put(particle, nextR);
        }

        return rStates;
    }

    static Map<Particle, R> respawnParticlesOutsideOpening(final Map<Particle, R> currentRs,
                                                           final double reenterMinHeight, final double reenterMaxHeight,
                                                           final double exitDistance, final int W) {

        final Map<Particle, R> particlesOutsideOpeningRs = new HashMap<>();
        currentRs.forEach((p, r) -> {
            Pair position = r.get(R0.ordinal());
            if (position.getY() - p.getRadius() < -exitDistance) {
                final R newR = generateParticleState(reenterMinHeight + p.getRadius(), reenterMaxHeight - p.getRadius(),
                    p.getRadius(), W - p.getRadius(), p, currentRs);

                particlesOutsideOpeningRs.put(p, newR);
            }
        });

        return particlesOutsideOpeningRs;
    }


    private static Pair calculateAcceleration(final Particle particleI, final Set<Particle> neighbors,
                                              final Map<Particle, R> currentRs,
                                              final int W, final double D,
                                              final double kn, final double kt,
                                              final double wallR0Y, final double wallR1Y) {

        final R particleIRs = currentRs.get(particleI);
        final Pair particleIR0 = particleIRs.get(R0.ordinal());
        final Pair particleIR1 = particleIRs.get(R1.ordinal());

        double fx = 0;
        double fy = 0;

        //Check collision with other particles
        for (Particle particleJ : neighbors) {
            final R particleJRs = currentRs.get(particleJ);
            if (particleJ != particleI) {
                final Pair collisionForce = collisionForce(particleI.getRadius(), particleIR0, particleIR1,
                    particleJ.getRadius(), particleJRs, kn, kt);
                fx += collisionForce.getX();
                fy += collisionForce.getY();
            }
        }

        // Check collision with walls
        Pair leftWallForce = new Pair(0.0, 0.0);
        Pair rightWallForce = new Pair(0.0, 0.0);
        Pair bottomWallForce = new Pair(0.0, 0.0);
        Pair leftOpeningParticleForce = new Pair(0.0, 0.0);
        Pair rightOpeningParticleForce = new Pair(0.0, 0.0);

        // Left Wall
        if (particleIR0.getX() <= particleI.getRadius()) {
            final R leftWall = new R();
            leftWall.set(R0.ordinal(), 0, particleIR0.getY());
            leftWall.set(R1.ordinal(), 0, wallR1Y);
            leftWallForce = collisionForce(particleI.getRadius(), particleIR0, particleIR1, 0, leftWall, kt, kn);
        }

        // Right Wall
        if (particleIR0.getX() + particleI.getRadius() >= W) {
            final R rightWall = new R();
            rightWall.set(R0.ordinal(), W, particleIR0.getY());
            rightWall.set(R1.ordinal(), 0, wallR1Y);
            rightWallForce = collisionForce(particleI.getRadius(), particleIR0, particleIR1, 0, rightWall, kt, kn);
        }

        // Bottom Wall
        if (particleIR0.getY() <= particleI.getRadius()) {
            // Outside Opening
            if (!isInOpening(particleIR0, W, D)) {
                final R bottomWall = new R();
                bottomWall.set(R0.ordinal(), particleIR0.getX(), wallR0Y);
                bottomWall.set(R1.ordinal(), 0, wallR1Y);
                bottomWallForce = collisionForce(particleI.getRadius(), particleIR0, particleIR1, 0, bottomWall, kt, kn);
            }
            // Outside Opening
            else {
                // Left Opening
                final R leftOpeningParticleRs = new R();
                leftOpeningParticleRs.set(R0.ordinal(), W / 2.0 - D / 2.0, 0);
                leftOpeningParticleRs.set(R1.ordinal(), 0, wallR1Y);
                leftOpeningParticleForce = collisionForce(particleI.getRadius(), particleIR0, particleIR1, OPENING_PARTICLE_RADIUS, leftOpeningParticleRs, kt, kn);

                // Right Opening
                final R rightOpeningParticleRs = new R();
                rightOpeningParticleRs.set(R0.ordinal(), W / 2.0 + D / 2.0, 0);
                rightOpeningParticleRs.set(R1.ordinal(), 0, wallR1Y);
                rightOpeningParticleForce = collisionForce(particleI.getRadius(), particleIR0, particleIR1, OPENING_PARTICLE_RADIUS, rightOpeningParticleRs, kt, kn);
            }
        }

        fx += leftWallForce.getX() + rightWallForce.getX() + bottomWallForce.getX() + leftOpeningParticleForce.getX() + rightOpeningParticleForce.getX();
        fy += leftWallForce.getY() + rightWallForce.getY() + bottomWallForce.getY() + leftOpeningParticleForce.getY() + rightOpeningParticleForce.getY();

        // Gravity Force
        fy -= particleI.getMass() * GRAVITY;

        return new Pair(fx / particleI.getMass(), fy / particleI.getMass());
    }


    private static Pair collisionForce(final double particleIRadius, final Pair particleIR0,
                                       final Pair particleIR1,
                                       final double particleJRadius, final R particleJRs,
                                       final double kn, final double kt) {
        double fx = 0;
        double fy = 0;

        final Pair particleJR0 = particleJRs.get(R0.ordinal());
        final Pair particleJR1 = particleJRs.get(R1.ordinal());

        final double deltaR0X = particleJR0.getX() - particleIR0.getX();
        final double deltaR0Y = particleJR0.getY() - particleIR0.getY();
        final double centerDistance = hypot(deltaR0X, deltaR0Y);

        final double radiusDistance = particleIRadius + particleJRadius;
        final double overlap = radiusDistance - centerDistance;

        if (overlap >= 0) {
            final double enX = deltaR0X / centerDistance;
            final double enY = deltaR0Y / centerDistance;

            final double etX = -enY;
            final double etY = enX;

            final double fN = -kn * overlap;

            final double R1relX = particleIR1.getX() - particleJR1.getX();
            final double R1relY = particleIR1.getY() - particleJR1.getY();

            final double scalarProd = R1relX * etX + R1relY * etY;

            final double fT = -kt * overlap * scalarProd;

            fx += fN * enX + fT * etX;
            fy += fN * enY + fT * etY;
        }
        return new Pair(fx, fy);
    }

    private static double calculateWallR1Y(final double A, final double w, final double t) {
        return A * w * cos(w * t);
    }

    private static double calculateWallR0Y(final double A, final double w, final double t) {
        return A * sin(w * t);
    }

    private static boolean isInOpening(final Pair particleR0, final int W, final double D) {
        return particleR0.getX() > (W / 2.0 - D / 2.0) && particleR0.getX() < (W / 2.0 + D / 2.0);
    }

}
