package ar.edu.itba.ss.simulator.Algorithms.CellIndex;

import ar.edu.itba.ss.simulator.utils.ExecutionTimestamps;
import ar.edu.itba.ss.simulator.utils.Particle;

import java.util.Map;
import java.util.Set;

public class CellIndexMethodResults {
    private ExecutionTimestamps executionTimestamps;
    private Map<Particle, Set<Particle>> neighbors;

    public CellIndexMethodResults(Map<Particle, Set<Particle>> neighbors, ExecutionTimestamps executionTimestamps) {
        this.executionTimestamps = executionTimestamps;
        this.neighbors = neighbors;
    }

    public void setNeighbors(Map<Particle, Set<Particle>> neighbors) {
        this.neighbors = neighbors;
    }

    public void setExecutionTimestamps(ExecutionTimestamps executionTimestamps) {
        this.executionTimestamps = executionTimestamps;
    }

    public ExecutionTimestamps getExecutionTimestamps() {
        return executionTimestamps;
    }

    public Map<Particle, Set<Particle>> getNeighbors() {
        return neighbors;
    }

}
