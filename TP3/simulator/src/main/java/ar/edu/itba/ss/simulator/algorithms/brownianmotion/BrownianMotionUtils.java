package ar.edu.itba.ss.simulator.algorithms.brownianmotion;

import ar.edu.itba.ss.simulator.algorithms.brownianmotion.Collision.CollisionType;
import ar.edu.itba.ss.simulator.utils.Particle;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static ar.edu.itba.ss.simulator.algorithms.brownianmotion.Collision.CollisionType.*;
import static ar.edu.itba.ss.simulator.utils.Particle.State;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

class BrownianMotionUtils {


    static Collision getClosestCollision(final Particle currentParticle,
                                         final State currentParticleState,
                                         final Map<Particle, State> particlesState,
                                         final int L) {

        Collision closestCollision = Collision.NONE;

        // Check collision with other particles
        for (Map.Entry<Particle, State> entry : particlesState.entrySet()) {
            final Particle particle = entry.getKey();
            final State state = entry.getValue();

            if (particle != currentParticle) {
                final Collision collision = calculateParticleCollision(currentParticle, currentParticleState, particle, state);
                if (collision.getType() != NONE && collision.compareTo(closestCollision) < 0) {
                    closestCollision = collision;
                }
            }
        }

        // Check collision with walls
        final Collision wallCollision = calculateWallCollision(currentParticle, currentParticleState, L);
        if (wallCollision.getType() != NONE && wallCollision.compareTo(closestCollision) < 0) {
            closestCollision = wallCollision;
        }

        return closestCollision;
    }

    static Map<Particle, State> updateParticlesStates(final Collision currentCollision,
                                                      final Set<Particle> particlesWithCollision,
                                                      final Set<Collision> closestCollisions,
                                                      final Map<Particle, State> currentStates) {

        // For each particle update its state
        Map<Particle, State> newStates = new TreeMap<>();

        // Update particles positions
        currentStates.forEach((particle, state) -> {

                final State s = State.nextInstant(state, currentCollision.getCollisionTime());
                newStates.put(particle, s);

                //FIXME: ERRORRR!!!
                if (s.getPosition().getX() < 0 || s.getPosition().getX() > 6 || s.getPosition().getY() < 0 || s.getPosition().getY() > 6) {
                    throw new RuntimeException();
                }

            }
        );

        // Update collision particles velocity
        updateCollisionParticlesVelocity(currentCollision, newStates);

        updateClosestCollisions(currentCollision, particlesWithCollision, closestCollisions);

        return newStates;
    }

    private static void updateClosestCollisions(final Collision currentCollision,
                                                final Set<Particle> particlesWithCollision,
                                                final Set<Collision> closestCollisions) {
        final Particle particleI = currentCollision.getParticleI();
        final Particle particleJ = currentCollision.getParticleJ();

        particlesWithCollision.remove(particleI);
        particlesWithCollision.remove(particleJ);

        final Set<Collision> updatedCollisions = new HashSet<>();

        closestCollisions.forEach(collision -> {
            if (!collision.containsParticle(particleI) && !collision.containsParticle(particleJ)) {
                updatedCollisions.add(Collision.withUpdatedTime(collision, currentCollision.getCollisionTime()));
            } else {
                particlesWithCollision.remove(collision.getParticleI());
                particlesWithCollision.remove(collision.getParticleJ());
            }
        });

        closestCollisions.clear();
        closestCollisions.addAll(updatedCollisions);
    }

    private static void updateCollisionParticlesVelocity(Collision collision, Map<Particle, State> newStates) {
        final Particle particleI = collision.getParticleI();
        final Particle particleJ = collision.getParticleJ();

        if (collision.isWall()) {
            if (particleI != null) {
                wallCollisionState(collision, newStates.get(particleI));
            } else {
                wallCollisionState(collision, newStates.get(particleJ));
            }
        } else {
            particlesCollisionStates(collision, newStates.get(particleI), newStates.get(particleJ));
        }
    }

    private static Collision calculateParticleCollision(final Particle particleXi,
                                                        final State stateXi,
                                                        final Particle particleXj,
                                                        final State stateXj) {

        final double deltaX = stateXj.getPosition().getX() - stateXi.getPosition().getX();
        final double deltaY = stateXj.getPosition().getY() - stateXi.getPosition().getY();
        final double deltaVx = stateXj.getVelocityX() - stateXi.getVelocityX();
        final double deltaVy = stateXj.getVelocityY() - stateXi.getVelocityY();

        final double vr = deltaVx * deltaX + deltaVy * deltaY;

        if (vr >= 0) {
            return Collision.NONE;
        }

        final double rr = pow(deltaX, 2) + pow(deltaY, 2);
        final double vv = pow(deltaVx, 2) + pow(deltaVy, 2);
        final double sigma = particleXi.getRadius() + particleXj.getRadius();

        final double d = pow(vr, 2) - vv * (rr - pow(sigma, 2));
        if (d < 0) {
            return Collision.NONE;
        }

        final double collisionTime = -(vr + sqrt(d)) / vv;
        //FIXME:
        if (collisionTime < 0) {
            throw new RuntimeException();
        }

        // Discard previous collision
        if (Double.compare(collisionTime, 0) == 0) {
            return Collision.NONE;
        }

        return new Collision(collisionTime, particleXi, particleXj, CollisionType.PARTICLES);
    }

    private static Collision calculateWallCollision(final Particle particle,
                                                    final State state,
                                                    final int L) {

        if (state.getVelocityX() == 0 && state.getVelocityY() == 0) {
            return Collision.NONE;
        }

        //Check vertical walls
        Double closestTimeX = null;
        if (state.getVelocityX() != 0) {
            if (state.getVelocityX() > 0) {
                closestTimeX = (L - particle.getRadius() - state.getPosition().getX()) / state.getVelocityX();
            } else {
                closestTimeX = (particle.getRadius() - state.getPosition().getX()) / state.getVelocityX();
            }
        }
        //FIXME:
        if (closestTimeX != null && closestTimeX < 0) {
            throw new RuntimeException();
        }

        //Check horizontal walls
        Double closestTimeY = null;
        if (state.getVelocityY() != 0) {
            if (state.getVelocityY() > 0) {
                closestTimeY = (L - particle.getRadius() - state.getPosition().getY()) / state.getVelocityY();
            } else {
                closestTimeY = (particle.getRadius() - state.getPosition().getY()) / state.getVelocityY();
            }
        }
        //FIXME:
        if (closestTimeY != null && closestTimeY < 0) {
            throw new RuntimeException();
        }

        // Check wall corner
        if (closestTimeX != null && closestTimeX.equals(closestTimeY)) {
            if (Double.compare(closestTimeX, 0) == 0 && Double.compare(closestTimeY, 0) == 0) {
                return Collision.NONE;
            }
            return new Collision(closestTimeX, particle, null, WALL_CORNER);
        }

        // If no vertical intersection then horizontal
        if (closestTimeX == null) {
            // Discard previous collision
            if (Double.compare(closestTimeY, 0) == 0) {
                return Collision.NONE;
            }
            return new Collision(closestTimeY, particle, null, WALL_HORIZONTAL);
        }

        // If no horizontal intersection then vertical
        else if (closestTimeY == null) {
            // Discard previous collision
            if (Double.compare(closestTimeX, 0) == 0) {
                return Collision.NONE;
            }
            return new Collision(closestTimeX, particle, null, WALL_VERTICAL);
        }

        // Discard previous collision
        if (Double.compare(closestTimeX, 0) == 0) {
            closestTimeX = Double.POSITIVE_INFINITY;
        }

        // Discard previous collision
        if (Double.compare(closestTimeY, 0) == 0) {
            closestTimeY = Double.POSITIVE_INFINITY;
        }

        return closestTimeX < closestTimeY ?
            new Collision(closestTimeX, particle, null, WALL_VERTICAL)
            :
            new Collision(closestTimeY, particle, null, WALL_HORIZONTAL);
    }

    private static void wallCollisionState(Collision collision, State state) {
        switch (collision.getType()) {
            case WALL_CORNER:
                state.setVelocityX(-state.getVelocityX());
                state.setVelocityY(-state.getVelocityY());
                break;

            case WALL_HORIZONTAL:
                state.setVelocityX(state.getVelocityX());
                state.setVelocityY(-state.getVelocityY());
                break;

            case WALL_VERTICAL:
                state.setVelocityX(-state.getVelocityX());
                state.setVelocityY(state.getVelocityY());
                break;
            default:
                throw new RuntimeException("Something went wrong on wall collision state calculation");
        }
    }

    private static void particlesCollisionStates(final Collision collision,
                                                 final State particleStateI,
                                                 final State particleStateJ) {

        final Particle particleI = collision.getParticleI();
        final Particle particleJ = collision.getParticleJ();

        // Calculate operation values
        final double deltaX = particleStateJ.getPosition().getX() - particleStateI.getPosition().getX();
        final double deltaY = particleStateJ.getPosition().getY() - particleStateI.getPosition().getY();
        final double deltaVx = particleStateJ.getVelocityX() - particleStateI.getVelocityX();
        final double deltaVy = particleStateJ.getVelocityY() - particleStateI.getVelocityY();

        final double vr = deltaVx * deltaX + deltaVy * deltaY;

        final double sigma = particleI.getRadius() + particleJ.getRadius();

        final double massSum = particleI.getMass() + particleJ.getMass();

        final double j = (2 * particleI.getMass() * particleJ.getMass() * vr) / (sigma * massSum);
        final double jx = j * deltaX / sigma;
        final double jy = j * deltaY / sigma;

        // Particle A new state
        double newVelocityXi = particleStateI.getVelocityX() + jx / particleI.getMass();
        double newVelocityYi = particleStateI.getVelocityY() + jy / particleI.getMass();
        particleStateI.setVelocityX(newVelocityXi);
        particleStateI.setVelocityY(newVelocityYi);

        // Particle B new state
        double newVelocityXj = particleStateJ.getVelocityX() - jx / particleJ.getMass();
        double newVelocityYj = particleStateJ.getVelocityY() - jy / particleJ.getMass();
        particleStateJ.setVelocityX(newVelocityXj);
        particleStateJ.setVelocityY(newVelocityYj);
    }

}
