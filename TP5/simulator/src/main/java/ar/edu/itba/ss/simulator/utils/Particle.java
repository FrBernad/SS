package ar.edu.itba.ss.simulator.utils;

import java.util.Objects;

public class Particle implements Comparable<Particle> {
    private final int id;
    private double radius;
    private final double mass;

    public static final Particle LEFT_WALL = new Particle(-1, 0, 0);
    public static final Particle RIGHT_WALL = new Particle(-2, 0, 0);
    public static final Particle BOTTOM_WALL = new Particle(-3, 0, 0);
    public static final Particle BOTTOM_WALL_RIGHT_OPENING = new Particle(-4, 0, 0);
    public static final Particle BOTTOM_WALL_LEFT_OPENING = new Particle(-5, 0, 0);


    public Particle(int id, double radius, double mass) {
        this.id = id;
        this.radius = radius;
        this.mass = mass;
    }

    public int getId() {
        return id;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getMass() {
        return mass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Particle particle = (Particle) o;
        return id == particle.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Particle o) {
        return Integer.compare(id, o.id);
    }

}
