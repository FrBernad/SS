package ar.edu.itba.ss.simulator.utils;

public class ParticleWithPhase extends Particle {
    private final double phase;

    public ParticleWithPhase(int id, double radius, double mass, double phase) {
        super(id, radius, mass);
        this.phase = phase;
    }

    public double getPhase() {
        return phase;
    }

}
