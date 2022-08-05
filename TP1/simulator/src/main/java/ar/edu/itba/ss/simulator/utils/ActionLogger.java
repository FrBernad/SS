package ar.edu.itba.ss.simulator.utils;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ActionLogger {

    private final static String START_PARSING = "Started reading input files";
    private final static String FINISH_PARSING = "Finished reading input files";

    private final static String START_ALGORITHM = "Finished reading input files";

    private final static String FINISH_ALGORITHM = "Finished reading input files";

    private final static String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";

    public static void logTimestamps(final PrintWriter writer, final ExecutionTimestamps executionTimestamps) {
        logFileParsingStart(writer, executionTimestamps.getFileParsingStart());
        logFileParsingEnd(writer, executionTimestamps.getFileParsingEnd());
        logAlgorithmStart(writer, executionTimestamps.getAlgorithmStart());
        logAlgorithmEnd(writer, executionTimestamps.getAlgorithmEnd());


    }

    private static void logFileParsingStart(final PrintWriter writer, final LocalDateTime timestamp) {
        writer.append(formatTimeLog(START_PARSING, timestamp));
    }

    private static void logFileParsingEnd(final PrintWriter writer, final LocalDateTime timestamp) {
        writer.append(formatTimeLog(FINISH_PARSING, timestamp));
    }

    private static void logAlgorithmStart(final PrintWriter writer, final LocalDateTime timestamp) {
        writer.append(formatTimeLog(START_ALGORITHM, timestamp));
    }

    private static void logAlgorithmEnd(final PrintWriter writer, final LocalDateTime timestamp) {
        writer.append(formatTimeLog(FINISH_ALGORITHM, timestamp));
    }

    private static String formatTimeLog(final String action, final LocalDateTime timestamp) {
        return String.format("%s - %s\n",
            timestamp.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)),
            action
        );
    }

}
