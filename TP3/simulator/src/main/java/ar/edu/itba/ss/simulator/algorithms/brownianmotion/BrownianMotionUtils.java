package ar.edu.itba.ss.simulator.algorithms.brownianmotion;

import ar.edu.itba.ss.simulator.algorithms.brownianmotion.Collision.CollisionType;
import ar.edu.itba.ss.simulator.utils.Particle;
import ar.edu.itba.ss.simulator.utils.Particle.Position;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static ar.edu.itba.ss.simulator.algorithms.brownianmotion.Collision.CollisionType.*;
import static ar.edu.itba.ss.simulator.utils.Particle.State;
import static java.lang.Math.*;

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
                                                      final Set<Collision> collisions,
                                                      final Set<Particle> collisionParticles,
                                                      final Map<Particle, State> currentStates) {

        collisionParticles.remove(currentCollision.getParticleI());
        collisionParticles.remove(currentCollision.getParticleJ());

        final Set<Collision> updatedTimeCollisions = new HashSet<>();

        // Remove collisions that depend on current collision and remove from collisionParticles for further calculations
        // FIXME: podria sacarse la condicion de !wall porque si fuera wall alguna de sus particulas seria null y no hay forma de
        // FIXME: que la segunda particula sea la misma q la de la colision porque seria la misma colision
        collisions.forEach(collision -> {
                final boolean collisionContainsCurrentCollisionParticles = (collision.containsParticle(currentCollision.getParticleI())
                    || collision.containsParticle(currentCollision.getParticleJ()));

                if (collisionContainsCurrentCollisionParticles) {
                    collisionParticles.remove(collision.getParticleI());
                    collisionParticles.remove(collision.getParticleJ());
                } else {
                    updatedTimeCollisions.add(Collision.collisionWithNewTime(collision, currentCollision.getCollisionTime()));
                }
            }
        );

        //Update collision times
        collisions.clear();
        collisions.addAll(updatedTimeCollisions);

        // For each particle update its state
        Map<Particle, State> newState = new HashMap<>();
        currentStates.forEach(((particle, state) -> {
            // Update state of particles not part of collision
            if (!currentCollision.containsParticle(particle)) {
                final State s = State.nextInstant(state, currentCollision.getCollisionTime());
                newState.put(particle, s);
                //FIXME: ERRORRR!!! Pareceria q se estan moviendo mas tiempo del q deberian y se salen del recinto
                if (s.getPosition().getX() < 0 + 0.2 || s.getPosition().getX() > 6 - 0.2 || s.getPosition().getY() < 0 + 0.2 || s.getPosition().getY() > 6 - 0.2) {
                    throw new RuntimeException();
                }
            } else {
                // Update state of particle if collision is of type wall
                if (currentCollision.isWall()) {
                    newState.put(particle, wallCollisionState(currentCollision, state));
                } else {
                    // Update state of particle if collision is of type particle, only for one of the two particles
                    if (!newState.containsKey(particle)) {
                        newState.putAll(particlesCollisionStates(
                                currentCollision,
                                State.nextInstant(currentStates.get(currentCollision.getParticleI()), currentCollision.getCollisionTime()),
                                State.nextInstant(currentStates.get(currentCollision.getParticleJ()), currentCollision.getCollisionTime())
                            )
                        );
                    }
                }
            }
        }));

        return newState;
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

        //Check horizontal walls
        Double closestTimeY = null;
        if (state.getVelocityY() != 0) {
            if (state.getVelocityY() > 0) {
                closestTimeY = (L - particle.getRadius() - state.getPosition().getY()) / state.getVelocityY();
            } else {
                closestTimeY = (particle.getRadius() - state.getPosition().getY()) / state.getVelocityY();
            }
        }

        if (closestTimeX != null && closestTimeX.equals(closestTimeY)) {
            return new Collision(closestTimeX, particle, null, WALL_CORNER);
        }

        if (closestTimeX == null) {
            return new Collision(closestTimeY, particle, null, WALL_HORIZONTAL);
        } else if (closestTimeY == null) {
            return new Collision(closestTimeX, particle, null, WALL_VERTICAL);
        }
        return closestTimeX < closestTimeY ?
            new Collision(closestTimeX, particle, null, WALL_VERTICAL)
            :
            new Collision(closestTimeY, particle, null, WALL_HORIZONTAL);
    }

    private static State wallCollisionState(Collision collision, State state) {
        State nextState = null;

        switch (collision.getType()) {
            case WALL_CORNER:
                nextState = State.nextInstant(state, collision.getCollisionTime());
                nextState.setVelocityX(-state.getVelocityX());
                nextState.setVelocityY(-state.getVelocityY());
                break;

            case WALL_HORIZONTAL:
                nextState = State.nextInstant(state, collision.getCollisionTime());
                nextState.setVelocityX(state.getVelocityX());
                nextState.setVelocityY(-state.getVelocityY());
                break;

            case WALL_VERTICAL:
                nextState = State.nextInstant(state, collision.getCollisionTime());
                nextState.setVelocityX(-state.getVelocityX());
                nextState.setVelocityY(state.getVelocityY());
                break;
            default:
                throw new RuntimeException("Something went wrong on wall collision");
        }

        return nextState;
    }

    private static Map<Particle, State> particlesCollisionStates(final Collision collision,
                                                                 final State particleStateI,
                                                                 final State particleStateJ) {

        final Map<Particle, State> newStates = new HashMap<>();
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
        newStates.put(particleI, particleStateI);

        // Particle B new state
        double newVelocityXj = particleStateJ.getVelocityX() - jx / particleJ.getMass();
        double newVelocityYj = particleStateJ.getVelocityY() - jy / particleJ.getMass();
        particleStateJ.setVelocityX(newVelocityXj);
        particleStateJ.setVelocityY(newVelocityYj);
        newStates.put(particleJ, particleStateJ);

        return newStates;
    }


}
