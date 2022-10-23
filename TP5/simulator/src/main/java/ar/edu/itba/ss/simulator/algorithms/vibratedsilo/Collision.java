package ar.edu.itba.ss.simulator.algorithms.vibratedsilo;

import ar.edu.itba.ss.simulator.utils.Particle;

import java.util.Objects;

public class Collision implements Comparable<Collision> {

    private final Double collisionTime;
    private final Particle particleI;
    private final Particle particleJ;
    private final CollisionType type;

    public final static Collision NONE = new Collision(Double.POSITIVE_INFINITY, null, null, CollisionType.NONE);

    public Collision(Double collisionTime, Particle particleI, Particle particleJ, CollisionType type) {
        this.collisionTime = collisionTime;
        this.particleI = particleI;
        this.particleJ = particleJ;
        this.type = type;
    }

    public static Collision withUpdatedTime(Collision collision, double newTime) {
        final double time = collision.getCollisionTime() - newTime;
        if (time <= 0) {
            throw new RuntimeException();
        }
        return new Collision(time, collision.getParticleI(), collision.getParticleJ(), collision.getType());
    }

    public boolean containsParticle(final Particle particle) {
        return (particleI != null && particleI.equals(particle)) || (particleJ != null && particleJ.equals(particle));
    }

    public boolean isWall() {
        return type == CollisionType.WALL_CORNER || type == CollisionType.WALL_HORIZONTAL || type == CollisionType.WALL_VERTICAL;
    }

    private boolean containsSameTypeAndParticles(final Collision collision) {
        return ((Objects.equals(particleI, collision.particleI) && Objects.equals(particleJ, collision.particleJ))
            ||
            (Objects.equals(particleI, collision.particleJ) && Objects.equals(particleJ, collision.particleI))
        )
            && type == collision.type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Collision collision = (Collision) o;
        return containsSameTypeAndParticles(collision);
    }

    @Override
    public int hashCode() {
        return Objects.hash(particleI, particleJ, type);
    }


    public Double getCollisionTime() {
        return collisionTime;
    }

    public Particle getParticleI() {
        return particleI;
    }

    public Particle getParticleJ() {
        return particleJ;
    }

    public CollisionType getType() {
        return type;
    }

    @Override
    public int compareTo(Collision o) {
        int ret = Double.compare(collisionTime, o.collisionTime);
        if (ret == 0) {
            return containsSameTypeAndParticles(o) ? 0 : 1;
        }
        return ret;
    }

    public enum CollisionType {
        WALL_HORIZONTAL,
        WALL_VERTICAL,
        WALL_CORNER,
        PARTICLES,
        NONE
    }

}
