package ar.edu.itba.ss.simulator.algorithms.brownianmotion;

import ar.edu.itba.ss.simulator.algorithms.brownianmotion.Collision.CollisionType;
import ar.edu.itba.ss.simulator.utils.Particle;

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

        collisionParticles.remove(currentCollision.getParticleXi());
        collisionParticles.remove(currentCollision.getParticleXj());

        final Set<Collision> updatedTimeCollisions = new HashSet<>();

        // Remove collisions that depend on current collision and remove from collisionParticles for further calculations
        // FIXME: podria sacarse la condicion de !wall porque si fuera wall alguna de sus particulas seria null y no hay forma de
        // FIXME: que la segunda particula sea la misma q la de la colision porque seria la misma colision
        collisions.removeIf(collision -> {
                    final boolean collisionContainsCurrentCollisionParticles = (collision.containsParticle(currentCollision.getParticleXi())
                            || collision.containsParticle(currentCollision.getParticleXj()));

                    if (collisionContainsCurrentCollisionParticles) {
                        collisionParticles.remove(collision.getParticleXi());
                        collisionParticles.remove(collision.getParticleXj());
                    } else {
                        updatedTimeCollisions.add(Collision.collisionWithNewTime(collision, currentCollision.getCollisionTime()));
                    }

                    return collisionContainsCurrentCollisionParticles;
                }
        );

        //Update collision times
        collisions.clear();
        collisions.addAll(updatedTimeCollisions);


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
                                        currentStates.get(currentCollision.getParticleXi()),
                                        currentStates.get(currentCollision.getParticleXj())
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
        final double deltaVx = stateXj.getXVelocity() - stateXi.getXVelocity();
        final double deltaVy = stateXj.getYVelocity() - stateXi.getYVelocity();

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
                                                                 final State particleStateXi,
                                                                 final State particleStateXj) {

        //FIXME: Está bien calcular la velocidad nueva así y el nuevo angulo así?
        final Map<Particle, State> newStates = new HashMap<>();
        final Particle particleXi = collision.getParticleXi();
        final Particle particleXj = collision.getParticleXj();
        final double collisionTime = collision.getCollisionTime();

        //        A=Xj
        // Calculate operation values
        final double deltaX = particleStateXj.getPosition().getX() - particleStateXi.getPosition().getX();
        final double deltaY = particleStateXj.getPosition().getY() - particleStateXi.getPosition().getY();
        final double deltaVx = particleStateXj.getXVelocity() - particleStateXi.getXVelocity();
        final double deltaVy = particleStateXj.getYVelocity() - particleStateXi.getYVelocity();

        final double vr = deltaVx * deltaX + deltaVy * deltaY;


        final double sigma = particleXi.getRadius() + particleXj.getRadius();

        final double massSum = particleXi.getMass() + particleXj.getMass();

        final double j = (2 * particleXi.getMass() * particleXj.getMass() * vr) / (sigma * massSum);
        final double jx = j * deltaX / sigma;
        final double jy = j * deltaY / sigma;

        // Particle A new state
        double newVelocityXi = particleStateXi.getXVelocity() + jx / particleXi.getMass();
        double newVelocityYi = particleStateXi.getYVelocity() + jy / particleXi.getMass();
        double speedXi = sqrt(pow(newVelocityXi, 2) + pow(newVelocityYi, 2));
        double angleXi = atan2(newVelocityYi, newVelocityXi);

        newStates.put(particleXi, State.nextInstant(particleStateXi, speedXi, angleXi, collisionTime));

        // Particle B new state
        double newVelocityXj = particleStateXj.getXVelocity() - jx / particleXj.getMass();
        double newVelocityYj = particleStateXj.getYVelocity() - jy / particleXj.getMass();
        double speedYj = sqrt(pow(newVelocityXj, 2) + pow(newVelocityYj, 2));
        double angleYj = atan2(newVelocityYj, newVelocityXj);

        newStates.put(particleXj, State.nextInstant(particleStateXj, speedYj, angleYj, collisionTime));

        return newStates;
    }


}
