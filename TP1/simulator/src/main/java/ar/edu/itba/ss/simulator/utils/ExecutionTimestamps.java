package ar.edu.itba.ss.simulator.utils;

import java.time.LocalDateTime;

public class ExecutionTimestamps {

    private LocalDateTime fileParsingStart;
    private LocalDateTime fileParsingEnd;
    private LocalDateTime algorithmStart;
    private LocalDateTime algorithmEnd;

    public LocalDateTime getFileParsingStart() {
        return fileParsingStart;
    }

    public void setFileParsingStart(LocalDateTime fileParsingStart) {
        this.fileParsingStart = fileParsingStart;
    }

    public LocalDateTime getFileParsingEnd() {
        return fileParsingEnd;
    }

    public void setFileParsingEnd(LocalDateTime fileParsingEnd) {
        this.fileParsingEnd = fileParsingEnd;
    }

    public LocalDateTime getAlgorithmStart() {
        return algorithmStart;
    }

    public void setAlgorithmStart(LocalDateTime algorithmStart) {
        this.algorithmStart = algorithmStart;
    }

    public LocalDateTime getAlgorithmEnd() {
        return algorithmEnd;
    }

    public void setAlgorithmEnd(LocalDateTime algorithmEnd) {
        this.algorithmEnd = algorithmEnd;
    }
}
