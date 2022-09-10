package ar.edu.itba.ss.simulator.utils;

import java.util.Objects;

import static java.lang.Math.*;

public class Particle implements Comparable<Particle> {
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

    @Override
    public int compareTo(Particle o) {
        return Integer.compare(id, o.id);
    }

    public static class State {
        private final Position position;
        private double velocityX; //velocity angle in radians
        private double velocityY; //velocity angle in radians

        public State(Position position, double velocityX, double velocityY) {
            this.position = position;
            this.velocityX = velocityX;
            this.velocityY = velocityY;
        }

        public Position getPosition() {
            return position;
        }

        public double getVelocityX() {
            return velocityX;
        }

        public double getVelocityY() {
            return velocityY;
        }

        public void setVelocityX(double velocityX) {
            this.velocityX = velocityX;
        }

        public void setVelocityY(double velocityY) {
            this.velocityY = velocityY;
        }

        public static State nextInstant(State currentState, double time) {
            Position position = new Position(
                currentState.position.getX() + currentState.getVelocityX() * time,
                currentState.position.getY() + currentState.getVelocityY() * time
            );

            return new State(position, currentState.getVelocityX(), currentState.getVelocityY());
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

    }
}
