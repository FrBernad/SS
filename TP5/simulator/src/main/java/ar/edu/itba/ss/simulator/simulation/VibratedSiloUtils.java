package ar.edu.itba.ss.simulator.simulation;

import ar.edu.itba.ss.simulator.utils.Pair;
import ar.edu.itba.ss.simulator.utils.Particle;
import ar.edu.itba.ss.simulator.utils.R;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import static ar.edu.itba.ss.simulator.utils.Particle.Position;
import static ar.edu.itba.ss.simulator.utils.Particle.State;
import static ar.edu.itba.ss.simulator.utils.R.values.*;
import static java.lang.Math.*;

class VibratedSiloUtils {

    private static final double GRAVITY = 9.8 * 100;

    static Map<Particle, R> calculateInitialRs(final Map<Particle, State> initialStates, final int L,
                                               final double kn, final double kt,
                                               final double w, final double A) {

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
            final Pair r2 = calculateAcceleration(p, initialR.get(R0.ordinal()), initialR.get(R1.ordinal()), rStates, L, kn, kt, calculateWallR1Y(A, w, 0));
            initialR.add(r2.getX(), r2.getY());

            initialRs.put(p, initialR);
        });

        return initialRs;
    }

    static Map<Particle, R> calculateNextRs(final Map<Particle, R> prevRs,
                                            final Map<Particle, R> currentRs,
                                            final double dt, final int L,
                                            final double kn, final double kt,
                                            final double w, final double A) {

        final Map<Particle, R> rStates = new HashMap<>();

        final Iterator<Entry<Particle, R>> prevIt = prevRs.entrySet().iterator();
        final Iterator<Entry<Particle, R>> currentIt = currentRs.entrySet().iterator();

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

            final Pair r2P = calculateAcceleration(particle, nextR.get(R0.ordinal()), new Pair(r1Px, r1Py), currentRs, L, kn, kt, calculateWallR1Y(A, w, dt));

            //Velocity correction
            final double r1Cx = currentR.get(R1.ordinal()).getX() +
                    ((1.0 / 3) * r2P.getX() + (5.0 / 6) * currentR.get(R2.ordinal()).getX() -
                            (1.0 / 6) * prevR.get(R2.ordinal()).getX()) * dt;
            final double r1Cy = currentR.get(R1.ordinal()).getY() +
                    ((1.0 / 3) * r2P.getY() + (5.0 / 6) * currentR.get(R2.ordinal()).getY() -
                            (1.0 / 6) * prevR.get(R2.ordinal()).getY()) * dt;
            nextR.add(r1Cx, r1Cy);

            final Pair r2C = calculateAcceleration(particle, nextR.get(R0.ordinal()), nextR.get(R1.ordinal()), currentRs, L, kn, kt, calculateWallR1Y(A, w, dt));
            nextR.add(r2C.getX(), r2C.getY());

            rStates.put(particle, nextR);
        }

        return rStates;
    }


    static void storeStates(final Map<Double, Map<Particle, State>> particlesState,
                            final Map<Particle, R> rMap,
                            final double instant) {

        Map<Particle, State> state = new HashMap<>();

        rMap.forEach((p, r) -> {
            Position position = new Position(r.get(R0.ordinal()).getX(), r.get(R0.ordinal()).getY());
            state.put(p, new State(position, r.get(R1.ordinal()).getX(), r.get(R1.ordinal()).getY()));
        });

        particlesState.put(instant, state);
    }

    static Map<Particle, R> euler(final Map<Particle, R> Rs,
                                  final double dt, final int L,
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

            final Pair eulerR2 = calculateAcceleration(p, eulerR.get(R0.ordinal()), eulerR.get(R1.ordinal()), Rs, L, kn, kt, calculateWallR1Y(A, w, dt), calculateWallR0Y(A, w, dt));
            eulerR.add(eulerR2.getX(), eulerR2.getY());

            rStates.put(p, eulerR);
        });

        return rStates;
    }


    private static Pair calculateAcceleration(final Particle particleI,
                                              final Pair particleIR0, final Pair particleIR1,
                                              final Map<Particle, R> currentRs, final int L,
                                              final double kn, final double kt, final double wallR1Y, final double wallR0Y) {
        double fx = 0;
        double fy = 0;

        //Check collision with other particles
        for (Map.Entry<Particle, R> entry : currentRs.entrySet()) {
            Particle particleJ = entry.getKey();
            R particleJRs = entry.getValue();
            if (particleJ != particleI) {
                final double deltaR0X = particleJRs.get(R0.ordinal()).getX() - particleIR0.getX();
                final double deltaR0Y = particleJRs.get(R0.ordinal()).getY() - particleIR0.getY();
                final double centerDistance = hypot(deltaR0X, deltaR0Y);

                final double radiusDistance = particleI.getRadius() + particleJ.getRadius();

                if (centerDistance <= radiusDistance) {

                    final double enX = deltaR0X / centerDistance;
                    final double enY = deltaR0Y / centerDistance;

                    final double etX = -enY;
                    final double etY = enX;

                    final double overlap = radiusDistance - centerDistance;

                    final double fNX = -kn * overlap * enX;
                    final double fNY = -kn * overlap * enY;

                    // Segun teorica es i - j pero la colision en la diapo q sigue es j - i
                    final double R1relX = particleIR1.getX() - particleJRs.get(R1.ordinal()).getX();
                    final double R1relY = particleIR1.getY() - particleJRs.get(R1.ordinal()).getY();

                    final double fTX = -kt * overlap * (R1relX * etX) * etX;
                    final double fTY = -kt * overlap * (R1relY * etY) * etY;

                    fx += fNX * enX + fTX * etX;
                    fy += fNY * enY + fTY * etY;
                }
            }
        }


        // Check collision with walls

        // Left Wall
        final State leftWall = new State(new Position(0, particleIR0.getY()), 0, wallR1Y);
        Pair leftWallForce = calculateWallForce(leftWall, particleI, particleIR0, particleIR1, kt, kn);

        // Right Wall
        final State rightWall = new State(new Position(L, particleIR0.getY()), 0, wallR1Y);
        Pair rightWallForce = calculateWallForce(rightWall, particleI, particleIR0, particleIR1, kt, kn);

        // Bottom Wall
        final State bottomWall = new State(new Position(particleIR0.getX(), wallR0Y), 0, wallR1Y);
        Pair bottomWallForce = calculateWallForce(bottomWall, particleI, particleIR0, particleIR1, kt, kn);

        fx += leftWallForce.getX() + rightWallForce.getX() + bottomWallForce.getX();
        fy += leftWallForce.getY() + rightWallForce.getY() + bottomWallForce.getY();

        // Gravity Force
        fy -= particleI.getMass() * GRAVITY;

//      if (particleIR0.getX() > (L / 2.0 - D / 2.0) && particleIR0.getX() < (L / 2.0 + D / 2.0)) {

        return new Pair(fx / particleI.getMass(), fy / particleI.getMass());
    }

    private static Pair calculateWallForce(State wall, Particle particle, Pair particleIR0, Pair particleIR1, double kt, double kn) {
        final double deltaR0XWall = wall.getPosition().getX() - particleIR0.getX();
        final double deltaR0YWall = wall.getPosition().getY() - particleIR0.getY();
        final double centerDistanceWall = hypot(deltaR0XWall, deltaR0YWall);
        final double overlap = particle.getRadius() - centerDistanceWall;

        double fx = 0;
        double fy = 0;

        if (overlap > 0) {
            final double enX = deltaR0XWall / centerDistanceWall;
            final double enY = deltaR0YWall / centerDistanceWall;
            final double etX = -enY;
            final double etY = enX;
            final double fNX = -kn * overlap * enX;
            final double fNY = -kn * overlap * enY;

            // Segun teorica es i - j pero la colision en la diapo q sigue es j - i
            final double R1relX = particleIR1.getX() - wall.getVelocityX();
            final double R1relY = particleIR1.getY() - wall.getVelocityY();

            final double fTX = -kt * overlap * (R1relX * etX) * etX;
            final double fTY = -kt * overlap * (R1relY * etY) * etY;
            fx = fNX * enX + fTX * etX;
            fy = fNY * enY + fTY * etY;

        }

        return new Pair(fx, fy);
    }


    private static double calculateWallR1Y(final double A, final double w, final double t) {
        return A * w * cos(w * t);
    }

    private static double calculateWallR0Y(final double A, final double w, final double t) {
        return A * sin(w * t);
    }
}
