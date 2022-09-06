package ar.edu.itba.ss.simulator.utils;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class ExecutionTimestamps {

    private LocalDateTime algorithmStart;
    private LocalDateTime algorithmEnd;

    public LocalDateTime getAlgorithmStart() {
        return algorithmStart;
    }

    public LocalDateTime getAlgorithmEnd() {
        return algorithmEnd;
    }

    public LocalTime getAlgorithmTotalTime() {
        return LocalTime.ofNanoOfDay(
                getAlgorithmEnd().toLocalTime().toNanoOfDay() - getAlgorithmStart().toLocalTime().toNanoOfDay()
        );
    }

    public void setAlgorithmStart(LocalDateTime algorithmStart) {
        this.algorithmStart = algorithmStart;
    }

    public void setAlgorithmEnd(LocalDateTime algorithmEnd) {
        this.algorithmEnd = algorithmEnd;
    }
}
