package ar.edu.itba.ss.simulator.methods;

import ar.edu.itba.ss.simulator.utils.ExecutionTimestamps;
import ar.edu.itba.ss.simulator.utils.Particle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ar.edu.itba.ss.simulator.utils.Particle.*;

public class CellIndexMethod {


    public static CellIndexMethodResults calculateNeighbors(final Map<Particle, Position> particles,
                                                            final int N,
                                                            final int L,
                                                            final int M,
                                                            final double R,
                                                            final Boolean periodic) {


        return new CellIndexMethodResults(new ExecutionTimestamps());
    }

    private static class Grid {
        private final int L;
        private final int M;

        private final ArrayList<ArrayList<Cell>> grid;

        public Grid(int L, int M, List<Particle> particles) {
            this.L = L;
            this.M = M;
            this.grid = new ArrayList<>();

            for (int i = 0; i < M; i++) {
                for (int j = 0; j < M; j++) {

                }
            }
        }

        private static class Cell {
            private final List<Particle> particles;
            private final double leftBoundary;
            private final double rightBoundary;
            private final double topBoundary;
            private final double bottomBoundary;

            public Cell(List<Particle> particles, double leftBoundary, double rightBoundary, double topBoundary, double bottomBoundary) {
                this.particles = particles;
                this.leftBoundary = leftBoundary;
                this.rightBoundary = rightBoundary;
                this.topBoundary = topBoundary;
                this.bottomBoundary = bottomBoundary;
            }

            public List<Particle> getParticles() {
                return particles;
            }

//            public boolean containsParticle(final Particle particle){
//                return true;
//            }

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
