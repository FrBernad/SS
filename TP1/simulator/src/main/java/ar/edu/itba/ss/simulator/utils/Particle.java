package ar.edu.itba.ss.simulator.utils;

import java.util.ArrayList;
import java.util.List;

public class Particle {
    private final int id;
    private final List<Position> positions;
    private final double radius;
    private final double property;

    public Particle(int id, double radius, double property) {
        this.id = id;
        this.radius = radius;
        this.property = property;
        this.positions = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public double getRadius() {
        return radius;
    }

    public double getProperty() {
        return property;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public static class Position {
        private final double x;
        private final double y;

        public Position(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

    }
}
