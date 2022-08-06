package ar.edu.itba.ss.simulator.methods;

import ar.edu.itba.ss.simulator.utils.ExecutionTimestamps;
import ar.edu.itba.ss.simulator.utils.Particle;

import java.util.ArrayList;
import java.util.List;

public class CellIndexMethod {


    public static CellIndexMethodResults calculateNeighbors(final List<Particle> particles,
                                                            final int N,
                                                            final int L,
                                                            final int M,
                                                            final double R,
                                                            final Boolean periodic) {



        return new CellIndexMethodResults(new ExecutionTimestamps());
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
