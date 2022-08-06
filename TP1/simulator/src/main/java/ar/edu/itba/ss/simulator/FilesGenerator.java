package ar.edu.itba.ss.simulator;

import ar.edu.itba.ss.simulator.utils.FileGeneratorArguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Properties;

import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrFail;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class FilesGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(Simulator.class);

    private static final String STATIC_FILE_PATH_P = "staticFile";
    private static final String DYNAMIC_FILE_PATH_P = "dynamicFile";
    private static final String N_P = "N";
    private static final String L_P = "L";
    private static final String RADIUS_P = "radius";
    private static final String PROPERTY_P = "property";
    private static final String DELIMITER_P = "delimiter";
    private static final String TIMES_P = "times";


    public static void main(String[] args) {
        LOGGER.info("Files Generator Starting ...");

        FileGeneratorArguments fileArguments;

        final Properties properties = System.getProperties();

        try {
            fileArguments = getAndParseBaseArguments(properties);
        } catch (IllegalArgumentException e) {
            printClientUsage();
            return;
        }

    }

    private static FileGeneratorArguments getAndParseBaseArguments(final Properties properties) throws IllegalArgumentException {
        final String staticFilePath = getPropertyOrFail(properties, STATIC_FILE_PATH_P);
        final String dynamicFilePath = getPropertyOrFail(properties, DYNAMIC_FILE_PATH_P);

        final String delimiter = getPropertyOrFail(properties, DELIMITER_P);
        final double L = parseDouble(getPropertyOrFail(properties, L_P));
        final double R = parseDouble(getPropertyOrFail(properties, RADIUS_P));
        final double P = parseDouble(getPropertyOrFail(properties, PROPERTY_P));

        final int N = parseInt(getPropertyOrFail(properties, N_P));
        final int times = parseInt(getPropertyOrFail(properties, TIMES_P));

        final File staticFile = new File(staticFilePath);
        final File dynamicFile = new File(dynamicFilePath);

        return new FileGeneratorArguments(staticFile, dynamicFile, L, R, P, N, delimiter, times);
    }

    private static void printClientUsage() {
        System.out.println("Invalid simulator invocation.\n" +
                "Usage: ./files_generator -DstaticFile='path/to/static/file' -DdynamicFile='path/to/dynamic/file' " +
                "-DL=L -Dradius=radius -DN=N -Dproperty=property");
    }
}
