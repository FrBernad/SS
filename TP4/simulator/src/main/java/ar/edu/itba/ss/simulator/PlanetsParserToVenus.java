package ar.edu.itba.ss.simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrFail;
import static ar.edu.itba.ss.simulator.utils.ParseUtils.parsePlanetsToVenus;

public class PlanetsParserToVenus {

    private static final String EARTH_PATH_P = "earthFile";
    private static final String VENUS_PATH_P = "venusFile";
    private static final String SUN_PATH_P = "sunFile";

    private static final String ASSETS_STATIC_DIR_PATH_P = "assetsStaticDir";
    private static final String ASSETS_DYNAMIC_DIR_PATH_P = "assetsDynamicDir";

    private static final double DISTANCE_TO_SPACESHIP = 1500;


    private static final Logger LOGGER = LoggerFactory.getLogger(PlanetsParserToVenus.class);

    public static void main(String[] args) throws IOException {
        LOGGER.info("SimulatorPlanets Starting ...");

        final String assetsStaticDir;
        final String assetsDynamicDir;
        final String earthFilePath;
        final String sunFilePath;
        final String venusFilePath;

        try {
            final Properties properties = System.getProperties();
            assetsStaticDir = getPropertyOrFail(properties, ASSETS_STATIC_DIR_PATH_P);
            assetsDynamicDir = getPropertyOrFail(properties, ASSETS_DYNAMIC_DIR_PATH_P);

            earthFilePath = getPropertyOrFail(properties, EARTH_PATH_P);
            sunFilePath = getPropertyOrFail(properties, SUN_PATH_P);
            venusFilePath = getPropertyOrFail(properties, VENUS_PATH_P);
        } catch (Exception e) {
            printClientUsage();
            return;
        }
        final File earthFile = new File(earthFilePath);
        final File venusFile = new File(venusFilePath);
        final File sunFile = new File(sunFilePath);

        parsePlanetsToVenus(assetsStaticDir, assetsDynamicDir, earthFile, venusFile, sunFile, DISTANCE_TO_SPACESHIP);

        LOGGER.info("SimulatorPlanets finished!");
    }

    private static void printClientUsage() {
        System.out.println("Invalid simulator invocation.\n" +
                "Usage: ./simulator -DassetsStaticDir='path/to/assetsStatic -DassetsDynamicDir='path/to/assetsDynamic'" +
                " -DearthFile=earthFile -DvenusFile=venusFile -DsunFile=sunFile ");
    }


}
