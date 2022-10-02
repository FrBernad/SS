package ar.edu.itba.ss.simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrFail;
import static ar.edu.itba.ss.simulator.utils.ParseUtils.parsePlanets;

public class PlanetsParser {

    private static final String EARTH_PATH_P = "earthFile";
    private static final String VENUS_PATH_P = "venusFile";
    private static final String SUN_PATH_P = "sunFile";
    private static final String ASSETS_DIR_PATH_P = "assetsDir";
    private static final double DISTANCE_TO_SPACESHIP = 1500;


    private static final Logger LOGGER = LoggerFactory.getLogger(PlanetsParser.class);

    public static void main(String[] args) throws IOException {
        LOGGER.info("SimulatorPlanets Starting ...");

        final Properties properties = System.getProperties();
        final String assetsDir = getPropertyOrFail(properties, ASSETS_DIR_PATH_P);

        final String earthFilePath = getPropertyOrFail(properties, EARTH_PATH_P);
        final String sunFilePath = getPropertyOrFail(properties, SUN_PATH_P);
        final String venusFilePath = getPropertyOrFail(properties, VENUS_PATH_P);

        final File earthFile = new File(earthFilePath);
        final File venusFile = new File(venusFilePath);
        final File sunFile = new File(sunFilePath);

        parsePlanets(assetsDir, earthFile, venusFile, sunFile, DISTANCE_TO_SPACESHIP);

    }

    private static void printClientUsage() {
        System.out.println("Invalid simulator invocation.\n" +
            "Usage: ./simulator -DassetsDir='path/to/assets " +
            "-DearthFile=earthFile -DvenusFile=venusFile -DsunFile=sunFile "
        );
    }


}
