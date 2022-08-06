package ar.edu.itba.ss.simulator.utils;

import java.time.LocalDateTime;

public class ExecutionTimestamps {

    private LocalDateTime algorithmStart;
    private LocalDateTime algorithmEnd;

    public LocalDateTime getAlgorithmStart() {
        return algorithmStart;
    }

    public LocalDateTime getAlgorithmEnd() {
        return algorithmEnd;
    }

    public void setAlgorithmStart(LocalDateTime algorithmStart) {
        this.algorithmStart = algorithmStart;
    }

    public void setAlgorithmEnd(LocalDateTime algorithmEnd) {
        this.algorithmEnd = algorithmEnd;
    }
}
