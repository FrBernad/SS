package ar.edu.itba.ss.simulator.utils;

import java.util.List;
import java.util.Map;

import static ar.edu.itba.ss.simulator.utils.Particle.State;

public class BrownianMotionAlgorithmResults {

    private final ExecutionTimestamps executionTimestamps;
    private final List<Map<Particle, State>> particlesStates;
    private final int iterations;

    public BrownianMotionAlgorithmResults(ExecutionTimestamps executionTimestamps, List<Map<Particle, State>> particlesStates, int iterations) {
        this.executionTimestamps = executionTimestamps;
        this.particlesStates = particlesStates;
        this.iterations = iterations;
    }

    public ExecutionTimestamps getExecutionTimestamps() {
        return executionTimestamps;
    }

    public List<Map<Particle, State>> getParticlesStates() {
        return particlesStates;
    }

    public int getIterations() {
        return iterations;
    }
}
