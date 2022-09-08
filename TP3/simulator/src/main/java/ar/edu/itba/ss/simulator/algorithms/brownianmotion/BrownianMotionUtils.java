package ar.edu.itba.ss.simulator.algorithms.brownianmotion;

import ar.edu.itba.ss.simulator.algorithms.brownianmotion.Collision.CollisionType;
import ar.edu.itba.ss.simulator.utils.Particle;

import java.util.HashMap;
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

        collisionParticles.remove(currentCollision.getParticleA());
        collisionParticles.remove(currentCollision.getParticleB());

        // Remove collisions that depend on current collision and remove from collisionParticles for further calculations
        // FIXME: podria sacarse la condicion de !wall porque si fuera wall alguna de sus particulas seria null y no hay forma de
        // FIXME: que la segunda particula sea la misma q la de la colision porque seria la misma colision
        collisions.removeIf(collision -> {
                final boolean collisionContainsCurrentCollisionParticles = (collision.containsParticle(currentCollision.getParticleA())
                    || collision.containsParticle(currentCollision.getParticleB()));

                if (collisionContainsCurrentCollisionParticles) {
                    collisionParticles.remove(collision.getParticleA());
                    collisionParticles.remove(collision.getParticleB());
                }

                return collisionContainsCurrentCollisionParticles;
            }
        );

        // For each particle update its state
        Map<Particle, State> newState = new HashMap<>();
        currentStates.forEach(((particle, state) -> {
            if (!currentCollision.containsParticle(particle)) {
                newState.put(particle, State.nextInstant(state, state.getSpeed(), state.getAngle(), currentCollision.getCollisionTime()));
            } else {
                if (currentCollision.isWall()) {
                    newState.put(particle, wallCollisionState(currentCollision, state));
                } else {
                    if (!newState.containsKey(particle)) {
                        newState.putAll(particlesCollisionStates(
                                currentCollision,
                                currentStates.get(currentCollision.getParticleA()),
                                currentStates.get(currentCollision.getParticleB())
                            )
                        );
                    }
                }
            }
        }));


        return newState;
    }

    private static Collision calculateParticleCollision(final Particle particleA,
                                                        final State stateA,
                                                        final Particle particleB,
                                                        final State stateB) {

        final double deltaX = stateA.getPosition().getX() - stateB.getPosition().getX();
        final double deltaY = stateA.getPosition().getY() - stateB.getPosition().getY();
        final double deltaVx = stateA.getXVelocity() - stateB.getXVelocity();
        final double deltaVy = stateA.getYVelocity() - stateB.getYVelocity();

        final double vr = deltaVx * deltaX + deltaVy * deltaY;

        if (vr >= 0) {
            return Collision.NONE;
        }

        final double rr = pow(deltaX, 2) + pow(deltaY, 2);
        final double vv = pow(deltaVx, 2) + pow(deltaVy, 2);
        final double sigma = particleA.getRadius() + particleB.getRadius();

        final double d = pow(vr, 2) - vv * (rr - pow(sigma, 2));
        if (d < 0) {
            return Collision.NONE;
        }

        final double collisionTime = -(vr + sqrt(d)) / vv;

        return new Collision(collisionTime, particleA, particleB, CollisionType.PARTICLES);
    }

    private static Collision calculateWallCollision(final Particle particle,
                                                    final State state,
                                                    final int L) {

        if (state.getXVelocity() == 0 && state.getYVelocity() == 0) {
            return Collision.NONE;
        }

        //Check vertical walls
        Double closestTimeX = null;
        if (state.getXVelocity() != 0) {
            if (state.getXVelocity() > 0) {
                closestTimeX = (L - particle.getRadius() - state.getPosition().getX()) / state.getXVelocity();
            } else {
                closestTimeX = (particle.getRadius() - state.getPosition().getX()) / state.getXVelocity();
            }
        }

        //Check horizontal walls
        Double closestTimeY = null;
        if (state.getYVelocity() != 0) {
            if (state.getYVelocity() > 0) {
                closestTimeY = (L - particle.getRadius() - state.getPosition().getY()) / state.getYVelocity();
            } else {
                closestTimeY = (particle.getRadius() - state.getPosition().getY()) / state.getYVelocity();
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
        double angle;
        State nextState = null;

        switch (collision.getType()) {
            case WALL_CORNER:
                angle = atan2(-state.getYVelocity(), -state.getXVelocity());
                nextState = State.nextInstant(state, state.getSpeed(), angle, collision.getCollisionTime());
                break;

            case WALL_HORIZONTAL:
                angle = atan2(-state.getYVelocity(), state.getXVelocity());
                nextState = State.nextInstant(state, state.getSpeed(), angle, collision.getCollisionTime());
                break;

            case WALL_VERTICAL:
                angle = atan2(state.getYVelocity(), -state.getXVelocity());
                nextState = State.nextInstant(state, state.getSpeed(), angle, collision.getCollisionTime());
                break;
        }

        return nextState;
    }

    private static Map<Particle, State> particlesCollisionStates(final Collision collision,
                                                                 final State particleStateA,
                                                                 final State particleStateB) {

        //FIXME: Está bien calcular la velocidad nueva así y el nuevo angulo así?
        final Map<Particle, State> newStates = new HashMap<>();
        final Particle particleA = collision.getParticleA();
        final Particle particleB = collision.getParticleB();
        final double collisionTime = collision.getCollisionTime();

        // Calculate operation values
        final double deltaX = particleStateA.getPosition().getX() - particleStateB.getPosition().getX();
        final double deltaY = particleStateA.getPosition().getY() - particleStateB.getPosition().getY();
        final double deltaVx = particleStateA.getXVelocity() - particleStateB.getXVelocity();
        final double deltaVy = particleStateA.getYVelocity() - particleStateB.getYVelocity();
        final double vr = deltaVx * deltaX + deltaVy * deltaY;

        final double sigma = particleA.getRadius() + particleB.getRadius();

        final double massSum = particleA.getMass() + particleB.getMass();

        final double j = (2 * massSum * vr) / (sigma * massSum);
        final double jx = j * deltaX / sigma;
        final double jy = j * deltaY / sigma;

        // Particle A new state
        double newVelocityXA = particleStateA.getXVelocity() + jx / particleA.getMass();
        double newVelocityYA = particleStateA.getYVelocity() + jy / particleA.getMass();
        double speedA = Math.sqrt(Math.pow(newVelocityXA, 2) + Math.pow(newVelocityYA, 2));
        double angleA = atan2(newVelocityYA, newVelocityXA);

        newStates.put(particleA, State.nextInstant(particleStateA, speedA, angleA, collisionTime));


        // Particle B new state
        double newVelocityXB = particleStateB.getXVelocity() - jx / particleB.getMass();
        double newVelocityYB = particleStateB.getYVelocity() - jy / particleB.getMass();
        double speedB = Math.sqrt(Math.pow(newVelocityXB, 2) + Math.pow(newVelocityYB, 2));
        double angleB = atan2(newVelocityYB, newVelocityXB);

        newStates.put(particleB, State.nextInstant(particleStateB, speedB, angleB, collisionTime));

        return newStates;
    }


}
