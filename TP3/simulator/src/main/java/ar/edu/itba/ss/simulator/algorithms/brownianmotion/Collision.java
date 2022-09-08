package ar.edu.itba.ss.simulator.algorithms.brownianmotion;

import ar.edu.itba.ss.simulator.utils.Particle;

import java.util.Objects;

public class Collision implements Comparable<Collision> {

    private final Double collisionTime;
    private final Particle particleXi;
    private final Particle particleXj;
    private final CollisionType type;

    public final static Collision NONE = new Collision(Double.POSITIVE_INFINITY, null, null, CollisionType.NONE);

    public static Collision collisionWithNewTime(Collision collision, double time) {
        return new Collision(collision.collisionTime - time, collision.particleXi, collision.particleXj, collision.type);
    }

    public Collision(Double collisionTime, Particle particleXi, Particle particleXj, CollisionType type) {
        this.collisionTime = collisionTime;
        this.particleXi = particleXi;
        this.particleXj = particleXj;
        this.type = type;
    }

    public boolean containsParticle(final Particle particle) {
        return (particleXi != null && particleXi.equals(particle)) || (particleXj != null && particleXj.equals(particle));
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
        return ((Objects.equals(particleXi, collision.particleXi) && Objects.equals(particleXj, collision.particleXj))
            ||
            (Objects.equals(particleXi, collision.particleXj) && Objects.equals(particleXj, collision.particleXi))
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
        return Objects.hash(collisionTime, particleXi, particleXj, type);
    }


    public double getCollisionTime() {
        return collisionTime;
    }

    public Particle getParticleXi() {
        return particleXi;
    }

    public Particle getParticleXj() {
        return particleXj;
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
