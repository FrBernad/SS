package ar.edu.itba.ss.simulator.utils;

import java.util.Objects;

import static java.lang.Math.*;

public class Particle {

    private final int id;
    private final double radius;
    private final double mass;

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

    public static class State {
        private final Position position;
        private final double speed;
        private final double angle; //velocity angle in radians

        public State(Position position, double speed, double angle) {
            this.position = position;
            this.speed = speed;
            this.angle = angle;
        }

        public Position getPosition() {
            return position;
        }

        public double getAngle() {
            return angle;
        }

        public double getSpeed() {
            return speed;
        }

        public double getXVelocity() {
            return cos(angle) * speed;
        }

        public double getYVelocity() {
            return sin(angle) * speed;
        }

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

        public static double calculateDistance(Position p1, Position p2) {
            return sqrt(pow(p1.getX() - p2.getX(), 2) + pow(p1.getY() - p2.getY(), 2));
        }
    }
}
