package ar.edu.itba.ss.simulator.methods.CellIndex;

import ar.edu.itba.ss.simulator.utils.Particle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cell {
    private final List<Particle> particles = new ArrayList<>();
    private final int x;
    private final int y;
    private final double leftBoundary;
    private final double rightBoundary;
    private final double topBoundary;
    private final double bottomBoundary;

    private Cell topCell;
    private Cell topRightCell;
    private Cell rightCell;
    private Cell bottomRightCell;

    public Cell(int x, int y, double leftBoundary, double rightBoundary, double topBoundary, double bottomBoundary) {
        this.x = x;
        this.y = y;
        this.leftBoundary = leftBoundary;
        this.rightBoundary = rightBoundary;
        this.topBoundary = topBoundary;
        this.bottomBoundary = bottomBoundary;
    }

    public boolean isEmpty() {
        return this.particles.isEmpty();
    }

    public List<Particle> getParticles() {
        return particles;
    }

    public void addParticle(final Particle particle) {
        this.particles.add(particle);
    }

    public double getLeftBoundary() {
        return leftBoundary;
    }

    public double getRightBoundary() {
        return rightBoundary;
    }

    public double getTopBoundary() {
        return topBoundary;
    }

    public double getBottomBoundary() {
        return bottomBoundary;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Cell getTopCell() {
        return topCell;
    }

    public Cell getTopRightCell() {
        return topRightCell;
    }

    public Cell getRightCell() {
        return rightCell;
    }

    public Cell getBottomRightCell() {
        return bottomRightCell;
    }

    public void setNeighbors(Cell topCell, Cell topRightCell, Cell rightCell, Cell bottomRightCell) {
        this.topCell = topCell;
        this.topRightCell = topRightCell;
        this.rightCell = rightCell;
        this.bottomRightCell = bottomRightCell;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return x == cell.x && y == cell.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}