package ar.edu.itba.ss.simulator.utils;

import java.time.Duration;
import java.time.LocalDateTime;

public class ExecutionTimestamps {

    private LocalDateTime algorithmStart;
    private LocalDateTime algorithmEnd;

    public Duration getAlgorithmTotalTime() {
        return Duration.between(algorithmStart, algorithmEnd);
    }

    public void setAlgorithmStart(LocalDateTime algorithmStart) {
        this.algorithmStart = algorithmStart;
    }

    public void setAlgorithmEnd(LocalDateTime algorithmEnd) {
        this.algorithmEnd = algorithmEnd;
    }
}
