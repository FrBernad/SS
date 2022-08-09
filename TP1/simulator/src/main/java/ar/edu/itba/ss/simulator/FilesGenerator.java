package ar.edu.itba.ss.simulator;

import ar.edu.itba.ss.simulator.utils.FileGeneratorArguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.Random;

import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrDefault;
import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrFail;
import static java.lang.Double.max;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class FilesGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(Simulator.class);

    private static final String STATIC_FILE_PATH_P = "staticFile";
    private static final String DYNAMIC_FILE_PATH_P = "dynamicFile";
    private static final String N_P = "N";
    private static final String L_P = "L";
    private static final String MAX_RADIUS_P = "maxRadius";
    private static final String MIN_RADIUS_P = "minRadius";
    private static final String PROPERTY_P = "property";
    private static final String DELIMITER_P = "delimiter";
    private static final String TIMES_P = "times";
    private static final String DEFAULT_DELIMITER = " ";


    public static void main(String[] args) throws IOException {
        LOGGER.info("Files Generator Starting ...");

        FileGeneratorArguments fileArguments;

        final Properties properties = System.getProperties();

        try {
            fileArguments = getAndParseBaseArguments(properties);
        } catch (IllegalArgumentException e) {
            printClientUsage();
            return;
        }

        final double minR = fileArguments.getMinR();
        final double maxR = fileArguments.getMaxR();
        try (PrintWriter pw = new PrintWriter(fileArguments.getStaticFile())) {
            pw.println(fileArguments.getN());
            pw.println(fileArguments.getL());
            for (int i = 0; i < fileArguments.getN(); i++) {
                pw.printf("%f %f\n", minR + Math.random() * (maxR - minR), fileArguments.getProperty());
            }
        }

        try (PrintWriter pw = new PrintWriter(fileArguments.getDynamicFile())) {
            final Random random = new Random();
            for (int i = 0; i < fileArguments.getTimes(); i++) {
                pw.println(i);
                for (int j = 0; j < fileArguments.getN(); j++) {
                    double x = random.nextDouble() * (fileArguments.getL());
                    double y = random.nextDouble() * (fileArguments.getL());
                    pw.printf("%f %f\n", x, y);
                }

            }
        }


    }

    private static FileGeneratorArguments getAndParseBaseArguments(final Properties properties) throws IllegalArgumentException {
        final String staticFilePath = getPropertyOrFail(properties, STATIC_FILE_PATH_P);
        final String dynamicFilePath = getPropertyOrFail(properties, DYNAMIC_FILE_PATH_P);

        final String delimiter = getPropertyOrDefault(properties, DELIMITER_P, DEFAULT_DELIMITER);
        final int L = parseInt(getPropertyOrDefault(properties, L_P, "100"));
        final double minR = parseDouble(getPropertyOrFail(properties, MIN_RADIUS_P));
        final double maxR = parseDouble(getPropertyOrFail(properties, MAX_RADIUS_P));
        final double P = parseDouble(getPropertyOrFail(properties, PROPERTY_P));

        final int N = parseInt(getPropertyOrDefault(properties, N_P, "100"));
        final int times = parseInt(getPropertyOrDefault(properties, TIMES_P, "1"));

        final File staticFile = new File(staticFilePath);
        final File dynamicFile = new File(dynamicFilePath);

        return new FileGeneratorArguments(staticFile, dynamicFile, L, minR, maxR, P, N, delimiter, times);
    }

    private static void printClientUsage() {
        System.out.println("Invalid generator invocation.\n" +
            "Usage: ./files_generator -DstaticFile='path/to/static/file' -DdynamicFile='path/to/dynamic/file' " +
            "-DL=L -Dradius=radius -DN=N -Dproperty=property");
    }
}
