package ar.edu.itba.ss.simulator.simulation;

import ar.edu.itba.ss.simulator.utils.Pair;
import ar.edu.itba.ss.simulator.utils.Particle;
import ar.edu.itba.ss.simulator.utils.R;

import java.util.*;
import java.util.Map.Entry;

import static ar.edu.itba.ss.simulator.ParticlesGenerator.generateParticleState;
import static ar.edu.itba.ss.simulator.utils.R.values.*;
import static java.lang.Math.*;

class VibratedSiloUtils {

    private static final double GRAVITY = 5;
    private static final double OPENING_PARTICLE_RADIUS = 0.2;

    static void calculateInitialAccelerations(final Map<Particle, R> initialStates,
                                              final int W, final double D,
                                              final double kn, final double kt,
                                              final double w, final double A) {

        initialStates.forEach((p, r) -> {
            final Pair r2 = calculateAcceleration(p, r.get(R0.ordinal()), r.get(R1.ordinal()), initialStates, W, D, kn, kt, calculateWallR0Y(A, w, 0), calculateWallR1Y(A, w, 0));
            r.add(r2.getX(), r2.getY());
        });

    }

    static Map<Particle, R> euler(final Map<Particle, R> Rs,
                                  final double dt, final double t,
                                  final int W, final double D,
                                  final double kn, final double kt,
                                  final double w, final double A) {

        final Map<Particle, R> rStates = new HashMap<>();

        Rs.forEach((p, r) -> {
            final R eulerR = new R();
            final Pair r0 = r.get(R0.ordinal());
            final Pair r1 = r.get(R1.ordinal());
            final Pair r2 = r.get(R2.ordinal());

            // r0
            double r0x = r0.getX() + dt * r1.getX() + (dt * dt / (2 * p.getMass())) * r2.getX() * p.getMass();
            double r0y = r0.getY() + dt * r1.getY() + (dt * dt / (2 * p.getMass())) * r2.getY() * p.getMass();
            eulerR.add(r0x, r0y);

            // r1
            double r1x = r1.getX() + (dt / p.getMass()) * r2.getX() * p.getMass();
            double r1y = r1.getY() + (dt / p.getMass()) * r2.getY() * p.getMass();
            eulerR.add(r1x, r1y);

            final Pair eulerR2 = calculateAcceleration(p, eulerR.get(R0.ordinal()), eulerR.get(R1.ordinal()), Rs, W, D, kn, kt, calculateWallR0Y(A, w, t), calculateWallR1Y(A, w, t));
            eulerR.add(eulerR2.getX(), eulerR2.getY());

            rStates.put(p, eulerR);
        });

        return rStates;
    }

    static Map<Particle, R> calculateNextRs(final Map<Particle, R> prevRs,
                                            final Map<Particle, R> currentRs,
                                            final double t, final double dt,
                                            final int W, final double D,
                                            final double kn, final double kt,
                                            final double w, final double A) {

        final Map<Particle, R> rStates = new HashMap<>();

        final Iterator<Entry<Particle, R>> prevIt = prevRs.entrySet().iterator();
        final Iterator<Entry<Particle, R>> currentIt = currentRs.entrySet().iterator();

//        final Map<Particle, Set<Particle>> currentNeighbors = CellIndexMethod.calculateNeighbors(currentRs, N, M, L, W, INTERACTION_RADIUS, false);

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
            nextR.add(r0x, r0y);

            //Velocity predictions
            final double r1Px = currentR.get(R1.ordinal()).getX() +
                ((3.0 / 2) * currentR.get(R2.ordinal()).getX() - (1.0 / 2) * prevR.get(R2.ordinal()).getX()) * dt;
            final double r1Py = currentR.get(R1.ordinal()).getY() +
                ((3.0 / 2) * currentR.get(R2.ordinal()).getY() - (1.0 / 2) * prevR.get(R2.ordinal()).getY()) * dt;


            final Pair r2P = calculateAcceleration(particle, nextR.get(R0.ordinal()), new Pair(r1Px, r1Py), currentRs, W, D, kn, kt, calculateWallR0Y(A, w, t), calculateWallR1Y(A, w, t));

            //Velocity correction
            final double r1Cx = currentR.get(R1.ordinal()).getX() +
                ((1.0 / 3) * r2P.getX() + (5.0 / 6) * currentR.get(R2.ordinal()).getX() -
                    (1.0 / 6) * prevR.get(R2.ordinal()).getX()) * dt;
            final double r1Cy = currentR.get(R1.ordinal()).getY() +
                ((1.0 / 3) * r2P.getY() + (5.0 / 6) * currentR.get(R2.ordinal()).getY() -
                    (1.0 / 6) * prevR.get(R2.ordinal()).getY()) * dt;
            nextR.add(r1Cx, r1Cy);



            final Pair r2C = calculateAcceleration(particle, nextR.get(R0.ordinal()), nextR.get(R1.ordinal()), currentRs, W, D, kn, kt, calculateWallR0Y(A, w, t), calculateWallR1Y(A, w, t));
            nextR.add(r2C.getX(), r2C.getY());

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
                // FIXME: posible cuello de botella
                final R newR = generateParticleState(reenterMinHeight + p.getRadius(), reenterMaxHeight - p.getRadius(),
                    p.getRadius(), W - p.getRadius(), p, currentRs);

                particlesOutsideOpeningRs.put(p, newR);
            }
        });

        return particlesOutsideOpeningRs;
    }


    private static Pair calculateAcceleration(final Particle particleI,
                                              final Pair particleIR0, final Pair particleIR1,
                                              final Map<Particle, R> currentRs,
                                              final int W, final double D,
                                              final double kn, final double kt,
                                              final double wallR0Y, final double wallR1Y) {

        double fx = 0;
        double fy = 0;

        //Check collision with other particles
        for (Map.Entry<Particle, R> entry : currentRs.entrySet()) {
            Particle particleJ = entry.getKey();
            R particleJRs = entry.getValue();
            if (particleJ != particleI) {
                final Pair collisionForce = collisionForce(particleI.getRadius(), particleIR0, particleIR1, particleJ.getRadius(), particleJRs, kn, kt);
                fx += collisionForce.getX();
                fy += collisionForce.getY();
            }
        }
        // Check collision with walls

        // Left Wall
        final R leftWall = new R();
        leftWall.add(0, particleIR0.getY());
        leftWall.add(0, wallR1Y);
        Pair leftWallForce = collisionForce(particleI.getRadius(), particleIR0, particleIR1, 0, leftWall, kt, kn);

        // Right Wall
        final R rightWall = new R();
        rightWall.add(W, particleIR0.getY());
        rightWall.add(0, wallR1Y);
        Pair rightWallForce = collisionForce(particleI.getRadius(), particleIR0, particleIR1, 0, rightWall, kt, kn);

        // Bottom Wall
        Pair bottomWallForce = new Pair(0.0, 0.0);
        if (!isInOpening(particleIR0, W, D)) {
            final R bottomWall = new R();
            bottomWall.add(particleIR0.getX(), wallR0Y);
            bottomWall.add(0, wallR1Y);
            bottomWallForce = collisionForce(particleI.getRadius(), particleIR0, particleIR1, 0, bottomWall, kt, kn);
        }

        // Left Opening
        final R leftOpeningParticleRs = new R();
        leftOpeningParticleRs.add(W / 2.0 - D / 2.0, 0);
        leftOpeningParticleRs.add(0, wallR1Y);
        Pair leftOpeningParticleForce = collisionForce(particleI.getRadius(), particleIR0, particleIR1, OPENING_PARTICLE_RADIUS, leftOpeningParticleRs, kt, kn);

        // Right Opening
        final R rightOpeningParticleRs = new R();
        rightOpeningParticleRs.add(W / 2.0 + D / 2.0, 0);
        rightOpeningParticleRs.add(0, wallR1Y);
        Pair rightOpeningParticleForce = collisionForce(particleI.getRadius(), particleIR0, particleIR1, OPENING_PARTICLE_RADIUS, rightOpeningParticleRs, kt, kn);

        fx += leftWallForce.getX() + rightWallForce.getX() + bottomWallForce.getX() + leftOpeningParticleForce.getX() + rightOpeningParticleForce.getX();
        fy += leftWallForce.getY() + rightWallForce.getY() + bottomWallForce.getY() + leftOpeningParticleForce.getY() + rightOpeningParticleForce.getY();

        // Gravity Force
        fy -= particleI.getMass() * GRAVITY;

        return new Pair(fx / particleI.getMass(), fy / particleI.getMass());
    }


    private static Pair collisionForce(final double particleIRadius, final Pair particleIR0, final Pair particleIR1,
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
//        return 0;
        return A * w * cos(w * t);
    }

    private static double calculateWallR0Y(final double A, final double w, final double t) {
//        return 0;
        return A * sin(w * t);
    }

    private static boolean isInOpening(final Pair particleR0, final int W, final double D) {
        return particleR0.getX() > (W / 2.0 - D / 2.0) && particleR0.getX() < (W / 2.0 + D / 2.0);
    }

}
