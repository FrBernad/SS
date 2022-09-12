package ar.edu.itba.ss.simulator.utils;

import java.util.Map;

import static ar.edu.itba.ss.simulator.utils.Particle.State;

public class BrownianMotionAlgorithmResults {

    private final ExecutionTimestamps executionTimestamps;
    private final Map<Double, Map<Particle, State>> particlesStates;
    private final int iterations;
    private final double simulationTime;

    public BrownianMotionAlgorithmResults(ExecutionTimestamps executionTimestamps, Map<Double, Map<Particle, State>> particlesStates, int iterations, double simulationTime) {
        this.executionTimestamps = executionTimestamps;
        this.particlesStates = particlesStates;
        this.iterations = iterations;
        this.simulationTime = simulationTime;
    }

    public ExecutionTimestamps getExecutionTimestamps() {
        return executionTimestamps;
    }

    public Map<Double, Map<Particle, State>> getParticlesStates() {
        return particlesStates;
    }

    public int getIterations() {
        return iterations;
    }

    public double getSimulationTime() {
        return simulationTime;
    }
}
