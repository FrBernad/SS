package ar.edu.itba.ss.simulator.utils;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ActionLogger {

    private final static String START_ALGORITHM = "Started cell index algorithm";

    private final static String FINISH_ALGORITHM = "Finished cell index algorithm";

    private final static String TOTAL_ALGORITHM_TIME = "Total algorithm time";

    private final static String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss.SSS";

    public static void logTimestamps(final PrintWriter writer, final ExecutionTimestamps executionTimestamps) {
        logAlgorithmStart(writer, executionTimestamps.getAlgorithmStart());
        logAlgorithmEnd(writer, executionTimestamps.getAlgorithmEnd());
        logTotalAlgorithmTime(writer, LocalTime.ofNanoOfDay(
            executionTimestamps.getAlgorithmEnd().toLocalTime().toNanoOfDay() - executionTimestamps.getAlgorithmStart().toLocalTime().toNanoOfDay())
        );
    }

    private static void logAlgorithmStart(final PrintWriter writer, final LocalDateTime timestamp) {
        writer.append(formatTimeLog(START_ALGORITHM, timestamp));
    }

    private static void logAlgorithmEnd(final PrintWriter writer, final LocalDateTime timestamp) {
        writer.append(formatTimeLog(FINISH_ALGORITHM, timestamp));
    }

    private static void logTotalAlgorithmTime(final PrintWriter writer, final LocalTime timestamp) {
        writer.append(String.format("%s - %s\n",
                TOTAL_ALGORITHM_TIME,
                timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"))
            )
        );
    }

    private static String formatTimeLog(final String action, final LocalDateTime timestamp) {
        return String.format("%s - %s\n",
            timestamp.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)),
            action
        );
    }

}
