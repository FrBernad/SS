package ar.edu.itba.ss.simulator.algorithms.flocks;

import ar.edu.itba.ss.simulator.utils.ExecutionTimestamps;
import ar.edu.itba.ss.simulator.utils.Particle;

import java.util.List;
import java.util.Map;

public class FlocksAlgorithmResults {
    private final ExecutionTimestamps executionTimestamps;
    private final List<Map<Particle, Particle.State>> particlesStates;
    private final List<Double> orderParameter;

    public FlocksAlgorithmResults(ExecutionTimestamps executionTimestamps, List<Map<Particle, Particle.State>> particlesStates, List<Double> orderParameter) {
        this.executionTimestamps = executionTimestamps;
        this.particlesStates = particlesStates;
        this.orderParameter = orderParameter;
    }

    public ExecutionTimestamps getExecutionTimestamps() {
        return executionTimestamps;
    }

    public List<Map<Particle, Particle.State>> getParticlesStates() {
        return particlesStates;
    }

    public List<Double> getOrderParameter() {
        return orderParameter;
    }

}
