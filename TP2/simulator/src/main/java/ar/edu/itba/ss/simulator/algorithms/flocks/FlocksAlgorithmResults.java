package ar.edu.itba.ss.simulator.algorithms.flocks;

import ar.edu.itba.ss.simulator.utils.ExecutionTimestamps;
import ar.edu.itba.ss.simulator.utils.Particle;

import java.util.List;
import java.util.Map;

public class FlocksAlgorithmResults {
    private ExecutionTimestamps executionTimestamps;
    private List<Map<Particle, Particle.State>> particlesStates;

    public FlocksAlgorithmResults(ExecutionTimestamps executionTimestamps, List<Map<Particle, Particle.State>> particlesStates) {
        this.executionTimestamps = executionTimestamps;
        this.particlesStates = particlesStates;
    }

    public ExecutionTimestamps getExecutionTimestamps() {
        return executionTimestamps;
    }

    public List<Map<Particle, Particle.State>> getParticlesStates() {
        return particlesStates;
    }
}
