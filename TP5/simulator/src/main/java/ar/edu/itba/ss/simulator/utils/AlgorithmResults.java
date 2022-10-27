package ar.edu.itba.ss.simulator.utils;

import java.util.List;
import java.util.Map;

public class AlgorithmResults {

    private final ExecutionTimestamps executionTimestamps;
    private final int iterations;

    public AlgorithmResults(ExecutionTimestamps executionTimestamps, int iterations) {
        this.executionTimestamps = executionTimestamps;
        this.iterations = iterations;
    }

    public ExecutionTimestamps getExecutionTimestamps() {
        return executionTimestamps;
    }

    public int getIterations() {
        return iterations;
    }

}