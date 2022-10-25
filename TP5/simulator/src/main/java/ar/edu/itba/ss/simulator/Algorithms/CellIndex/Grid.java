package ar.edu.itba.ss.simulator.Algorithms.CellIndex;

import ar.edu.itba.ss.simulator.utils.Pair;
import ar.edu.itba.ss.simulator.utils.Particle;
import ar.edu.itba.ss.simulator.utils.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ar.edu.itba.ss.simulator.utils.R.values.R0;

public class Grid {
    private final List<List<Cell>> grid;
    private final int M;
    private final int N;
    private final boolean periodic;

    public Grid(final int L, final int W, final int M, final int N, final Map<Particle, R> particles, final Boolean periodic) {
        this.M = M;
        this.N = N;
        this.periodic = periodic;

        this.grid = new ArrayList<>();
        double incrementY = L / (double) M;
        double incrementX = W / (double) N;

        buildGrid(incrementX, incrementY);
        fillGrid(particles, incrementX, incrementY);
        setCellNeighbors();
    }

    private void buildGrid(final double incrementX, final double incrementY) {
        double leftBoundary;
        double rightBoundary;
        double bottomBoundary = 0;
        double topBoundary = incrementY;

        for (int y = 0; y < M; y++) {
            leftBoundary = 0;
            rightBoundary = leftBoundary + incrementX;
            this.grid.add(y, new ArrayList<>());
            for (int x = 0; x < N; x++) {
                this.grid.get(y).add(x, new Cell(x, y, leftBoundary, rightBoundary, topBoundary, bottomBoundary));
                leftBoundary += incrementX;
                rightBoundary += incrementX;
            }
            topBoundary += incrementY;
            bottomBoundary += incrementY;
        }

    }

    private void fillGrid(Map<Particle, R> particles, final double incrementX, final double incrementY) {
        for (Map.Entry<Particle, R> entry : particles.entrySet()) {
            Pair position = entry.getValue().get(R0.ordinal());
            final int x_index = (int) Math.floor(position.getX() / incrementX);
            final int y_index = (int) Math.floor(position.getY() / incrementY);
            this.grid.get(y_index).get(x_index).addParticle(entry.getKey());
        }
    }

    private void setCellNeighbors() {
        for (int y = 0; y < M; y++) {
            for (int x = 0; x < N; x++) {

                final Cell currentCell = this.grid.get(y).get(x);

                Cell topCell;
                Cell rightCell;
                Cell topRightCell;
                Cell bottomRightCell;

                topCell = this.grid.get((y + 1) % M).get(x);
                topRightCell = this.grid.get((y + 1) % M).get((x + 1) % N);
                bottomRightCell = this.grid.get((y - 1) < 0 ? M - 1 : y - 1).get((x + 1) % N);
                rightCell = this.grid.get(y).get((x + 1) % N);

                if (!this.periodic) {
                    if (y + 1 >= M) {
                        topCell = null;
                        topRightCell = null;
                    }
                    if (y - 1 < 0) {
                        bottomRightCell = null;
                    }
                    if (x + 1 >= N) {
                        topRightCell = null;
                        rightCell = null;
                        bottomRightCell = null;
                    }
                } else {
                    if (y == this.M - 1 && x == this.N - 1) {
                        //Esquina arriba a la derecha
                        topRightCell = this.grid.get(0).get(0);
                    } else if (y == 0 && x == this.N - 1) {
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
