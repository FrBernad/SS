package ar.edu.itba.ss.simulator.algorithms.brownianmotion;

import ar.edu.itba.ss.simulator.utils.Particle;

import java.util.Objects;

public class Collision {

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

    //FIXME: SI NO USAMOS SACARLO
    public boolean containsParticle(final Particle particle) {
        return (particleI != null && particleI.equals(particle)) || (particleJ != null && particleJ.equals(particle));
    }

    public boolean isWall() {
        return type == CollisionType.WALL_CORNER || type == CollisionType.WALL_HORIZONTAL || type == CollisionType.WALL_VERTICAL;
    }

    public int compareTime(Collision o) {
        return Double.compare(collisionTime, o.collisionTime);
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
        return containsSameTypeAndParticles((Collision) o);
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

    public enum CollisionType {
        WALL_HORIZONTAL,
        WALL_VERTICAL,
        WALL_CORNER,
        PARTICLES,
        NONE
    }

}
