package ar.edu.itba.ss.simulator.methods;

import ar.edu.itba.ss.simulator.utils.ExecutionTimestamps;
import ar.edu.itba.ss.simulator.utils.Particle;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ar.edu.itba.ss.simulator.utils.Particle.Position;

public class CellIndexMethod {

    public static CellIndexMethodResults calculateNeighbors(final Map<Particle, Position> particles,
                                                            final int N,
                                                            final int L,
                                                            final int M,
                                                            final double R,
                                                            final Boolean periodic) {

        Grid grid = new Grid(L, M, particles, periodic);

        final ExecutionTimestamps executionTimestamps = new ExecutionTimestamps();
        executionTimestamps.setAlgorithmStart(LocalDateTime.now());


        executionTimestamps.setAlgorithmEnd(LocalDateTime.now());
        return new CellIndexMethodResults(executionTimestamps);
    }

    public static class Grid {
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

        public static class Cell {
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

        }


    }

    public static class CellIndexMethodResults {
        private final ExecutionTimestamps executionTimestamps;
        private List<List<Particle>> neighbors;

        public CellIndexMethodResults(ExecutionTimestamps executionTimestamps) {
            this.executionTimestamps = executionTimestamps;
            this.neighbors = new ArrayList<>();
        }

        public ExecutionTimestamps getExecutionTimestamps() {
            return executionTimestamps;
        }

        public List<List<Particle>> getNeighbors() {
            return neighbors;
        }

        public void setNeighbors(List<List<Particle>> neighbors) {
            this.neighbors = neighbors;
        }
    }


}
