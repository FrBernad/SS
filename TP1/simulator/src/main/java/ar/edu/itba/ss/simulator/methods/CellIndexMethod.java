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

        final ExecutionTimestamps executionTimestamps = new ExecutionTimestamps();
        executionTimestamps.setAlgorithmStart(LocalDateTime.now());

        final Grid grid = new Grid(L, M, particles);
//
//        List<List<Grid.Cell>> matrix = grid.getGrid();
//        boolean success = true;
//
//        int i = 0;
//        int j = 0;
//        for (List<Grid.Cell> rows : matrix) {
//            j = 0;
//            for (Grid.Cell cell : rows) {
//
//                System.out.printf(
//                        "-----------------------------\n" +
//                                "Cell [ %d : %d ]\n" +
//                                "%f<X<%f\n" +
//                                "%f<Y< %f\n", i, j, cell.leftBoundary, cell.rightBoundary, cell.topBoundary, cell.bottomBoundary);
//                j++;
//                for (Particle p : cell.getParticles()) {
//                    Position position = particles.get(p);
//                    boolean condition = (
//                            position.getX() >= cell.leftBoundary && position.getX() < cell.rightBoundary) &&
//                            (position.getY() >= cell.bottomBoundary && position.getY() < cell.topBoundary);
//                    if (!condition) {
//                        success = false;
//                    }
//                    System.out.printf("Particle: %d X: %f ---- Y: %f (%s)\n", p.getId(), position.getX(), position.getY(), condition);
//                }
//            }
//            i++;
//        }
//        System.out.println("Success in all cells: " + success);
//

        executionTimestamps.setAlgorithmEnd(LocalDateTime.now());
        return new CellIndexMethodResults(executionTimestamps);
    }

    private static class Grid {
        private final List<List<Cell>> grid;

        private final int M;
        private final int L;


        public Grid(int L, int M, Map<Particle, Position> particles) {
            this.M = M;
            this.L = L;

            this.grid = new ArrayList<>();
            double increment = L / (double) M;
            double leftBoundary;
            double rightBoundary;
            double bottomBoundary = 0;
            double topBoundary = increment;

            for (int i = 0; i < M; i++) {
                leftBoundary = 0;
                rightBoundary = leftBoundary + increment;
                this.grid.add(i, new ArrayList<>());
                for (int j = 0; j < M; j++) {
                    this.grid.get(i).add(j, new Cell(leftBoundary, rightBoundary, topBoundary, bottomBoundary));
                    leftBoundary += increment;
                    rightBoundary += increment;
                }
                topBoundary += increment;
                bottomBoundary += increment;
            }

            for (Map.Entry<Particle, Position> entry : particles.entrySet()) {
                final int x_index = (int) Math.floor(entry.getValue().getX() / increment);
                final int y_index = (int) Math.floor(entry.getValue().getY() / increment);
                this.grid.get(y_index).get(x_index).addParticle(entry.getKey());
            }

        }

        public List<List<Cell>> getGrid() {
            return grid;
        }

        private static class Cell {
            private final List<Particle> particles = new ArrayList<>();
            private final double leftBoundary;
            private final double rightBoundary;
            private final double topBoundary;
            private final double bottomBoundary;

            public Cell(double leftBoundary, double rightBoundary, double topBoundary, double bottomBoundary) {
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
