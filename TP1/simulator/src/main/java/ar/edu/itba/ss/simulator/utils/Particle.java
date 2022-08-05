package ar.edu.itba.ss.simulator.utils;

public class Particle {
    private final int id;
    private final double x;
    private final double y;
    private final double radius;
    private final double property;

    public Particle(int id, double x, double y, double radius, double property) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.property = property;
    }

    public int getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRadius() {
        return radius;
    }

    public double getProperty() {
        return property;
    }
}
