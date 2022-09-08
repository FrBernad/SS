package ar.edu.itba.ss.simulator.algorithms.brownianmotion;

import ar.edu.itba.ss.simulator.utils.Particle;

import java.util.Objects;

public class Collision implements Comparable<Collision> {

    private final Double collisionTime;
    private final Particle particleA;
    private final Particle particleB;
    private final CollisionType type;

    public final static Collision NONE = new Collision(Double.POSITIVE_INFINITY, null, null, CollisionType.NONE);

    public static Collision collisionWithNewTime(Collision collision, double time) {
        return new Collision(collision.collisionTime - time, collision.particleA, collision.particleB, collision.type);
    }

    public Collision(Double collisionTime, Particle particleA, Particle particleB, CollisionType type) {
        this.collisionTime = collisionTime;
        this.particleA = particleA;
        this.particleB = particleB;
        this.type = type;
    }

    public boolean containsParticle(final Particle particle) {
        return (particleA != null && particleA.equals(particle)) || (particleB != null && particleB.equals(particle));
    }

    public boolean isWall() {
        return type == CollisionType.WALL_CORNER || type == CollisionType.WALL_HORIZONTAL || type == CollisionType.WALL_VERTICAL;
    }

    @Override
    //TODO: Pueden ocurrir colisiones al mismo tiempo ?
    public int compareTo(Collision o) {
        int ret = Double.compare(collisionTime, o.collisionTime);

        if (ret == 0) {
            ret = containsSameTypeAndParticles(o) ? 0 : 1;
        }

        return ret;
    }

    private boolean containsSameTypeAndParticles(final Collision collision) {
        return ((Objects.equals(particleA, collision.particleA) && Objects.equals(particleB, collision.particleB))
            ||
            (Objects.equals(particleA, collision.particleB) && Objects.equals(particleB, collision.particleA))
        )
            && type == collision.type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Collision collision = (Collision) o;
        return Double.compare(collision.collisionTime, collisionTime) == 0 && containsSameTypeAndParticles((Collision) o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(collisionTime, particleA, particleB, type);
    }


    public double getCollisionTime() {
        return collisionTime;
    }

    public Particle getParticleA() {
        return particleA;
    }

    public Particle getParticleB() {
        return particleB;
    }

    public CollisionType getType() {
        return type;
    }

    public enum CollisionType {
        WALL_HORIZONTAL,
        WALL_VERTICAL,
        WALL_CORNER,
        PARTICLES,
        NONE
    }

}
