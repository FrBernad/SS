package ar.edu.itba.ss.simulator.methods.cellIndex;

import ar.edu.itba.ss.simulator.utils.Particle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ar.edu.itba.ss.simulator.utils.Particle.Position;

public class Grid {
    private final List<List<Cell>> grid;
    private final int M;
    private final int L;
    private final boolean periodic;

    public Grid(int L, int M, Map<Particle, Position> particles, Boolean periodic) {
        this.M = M;
        this.L = L;
        this.periodic = periodic;

        this.grid = new ArrayList<>();
        double increment = L / (double) M;
        buildGrid(increment);
        fillGrid(particles, increment);
        setCellNeighbors();
    }

    private void buildGrid(final double increment) {
        double leftBoundary;
        double rightBoundary;
        double bottomBoundary = 0;
        double topBoundary = increment;

        for (int y = 0; y < M; y++) {
            leftBoundary = 0;
            rightBoundary = leftBoundary + increment;
            this.grid.add(y, new ArrayList<>());
            for (int x = 0; x < M; x++) {
                this.grid.get(y).add(x, new Cell(x, y, leftBoundary, rightBoundary, topBoundary, bottomBoundary));
                leftBoundary += increment;
                rightBoundary += increment;
            }
            topBoundary += increment;
            bottomBoundary += increment;
        }

    }

    private void fillGrid(Map<Particle, Position> particles, final double increment) {
        for (Map.Entry<Particle, Position> entry : particles.entrySet()) {
            final int x_index = (int) Math.floor(entry.getValue().getX() / increment);
            final int y_index = (int) Math.floor(entry.getValue().getY() / increment);
            this.grid.get(y_index).get(x_index).addParticle(entry.getKey());
        }
    }

    private void setCellNeighbors() {
        for (int y = 0; y < M; y++) {
            for (int x = 0; x < M; x++) {

                final Cell currentCell = this.grid.get(y).get(x);

                Cell topCell;
                Cell rightCell;
                Cell topRightCell;
                Cell bottomRightCell;

                topCell = this.grid.get((y + 1) % M).get(x);
                topRightCell = this.grid.get((y + 1) % M).get((x + 1) % M);
                bottomRightCell = this.grid.get((y - 1) < 0 ? M - 1 : y - 1).get((x + 1) % M);
                rightCell = this.grid.get(y).get((x + 1) % M);

                if (!this.periodic) {
                    if (y + 1 >= M) {
                        topCell = null;
                        topRightCell = null;
                    }
                    if (y - 1 < 0) {
                        bottomRightCell = null;
                    }
                    if (x + 1 >= M) {
                        topRightCell = null;
                        rightCell = null;
                        bottomRightCell = null;
                    }
                } else {
                    if (y == this.M - 1 && x == this.M - 1) {
                        //Esquina arriba a la derecha
                        topRightCell = this.grid.get(0).get(0);
                    } else if (y == 0 && x == this.M - 1) {
                        //Esquina abajo a la derecha
                        bottomRightCell = this.grid.get(M - 1).get(0);
                    }
                }
                currentCell.setNeighbors(topCell, topRightCell, rightCell, bottomRightCell);
            }
        }
    }

    public List<List<Cell>> getGrid() {
        return grid;
    }

}
