package ar.edu.itba.ss.simulator.utils;

import java.util.List;
import java.util.Map;

import static ar.edu.itba.ss.simulator.utils.Particle.State;

public class BrownianMotionAlgorithmResults {

    private final ExecutionTimestamps executionTimestamps;
    private final List<Map<Particle, State>> particlesStates;

    public BrownianMotionAlgorithmResults(ExecutionTimestamps executionTimestamps, List<Map<Particle, State>> particlesStates) {
        this.executionTimestamps = executionTimestamps;
        this.particlesStates = particlesStates;
    }

    public ExecutionTimestamps getExecutionTimestamps() {
        return executionTimestamps;
    }

    public List<Map<Particle, State>> getParticlesStates() {
        return particlesStates;
    }

}
