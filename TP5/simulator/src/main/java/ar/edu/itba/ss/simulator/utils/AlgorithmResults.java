package ar.edu.itba.ss.simulator.utils;

import java.util.Map;

public class AlgorithmResults {

    private final ExecutionTimestamps executionTimestamps;
    private final int iterations;

    private final Map<Double, Map<Particle, R>> particlesStates;

    public AlgorithmResults(ExecutionTimestamps executionTimestamps, int iterations, Map<Double, Map<Particle, R>> particlesStates) {
        this.executionTimestamps = executionTimestamps;
        this.iterations = iterations;
        this.particlesStates = particlesStates;
    }

    public ExecutionTimestamps getExecutionTimestamps() {
        return executionTimestamps;
    }

    public int getIterations() {
        return iterations;
    }

    public Map<Double, Map<Particle, R>> getParticlesStates() {
        return particlesStates;
    }
}
