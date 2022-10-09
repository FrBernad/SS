package ar.edu.itba.ss.simulator;

import ar.edu.itba.ss.simulator.simulation.SpaceMission;
import ar.edu.itba.ss.simulator.utils.AlgorithmResults;
import ar.edu.itba.ss.simulator.utils.ParseUtils;
import ar.edu.itba.ss.simulator.utils.Particle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrDefault;
import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrFail;
import static ar.edu.itba.ss.simulator.utils.ParseUtils.parseParticlesList;
import static ar.edu.itba.ss.simulator.utils.Particle.Position;
import static ar.edu.itba.ss.simulator.utils.Particle.State;
import static java.lang.Double.parseDouble;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class SimulatorSpeedToEarth {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimulatorSpeedToEarth.class);
    private static final String STATIC_FILE_PATH_P = "staticFile";
    private static final String DYNAMIC_FILE_PATH_P = "dynamicFile";
    private static final String RESULTS_OUT_DIR_PATH_P = "resultsDir";
    private static final String DT_P = "dt";
    private static final String TF_P = "tf";
    private static final String DELIMITER_P = "delimiter";
    private static final String DEFAULT_DELIMITER = " ";
    private static final int SUN = 1;
    private static final int EARTH = 2;
    private static final int VENUS = 3;
    private static final int SPACESHIP = 4;
    private static final double DISTANCE_TO_SPACESHIP = 1500;
    private static final double ORBITAL_SPEED = 5.8;
//    private static final double SPEED_STEP = 0.1;
//    private static final double MIN_SPEED = 2;
//    private static final double MAX_SPEED = 8;
    private static final double SPEED_STEP = 0.001;
    private static final double MIN_SPEED = 4.390;
    private static final double MAX_SPEED = 4.411;


    public static void main(String[] args) throws IOException {
        LOGGER.info("SimulatorSpeedToEarth Starting ...");
        final String staticFilePath;
        final String dynamicFilePath;
        final String outResultsDirPath;
        final String delimiter;
        final double dt;
        final double tf;

        final Properties properties = System.getProperties();
        try {
            staticFilePath = getPropertyOrFail(properties, STATIC_FILE_PATH_P);
            dynamicFilePath = getPropertyOrFail(properties, DYNAMIC_FILE_PATH_P);
            outResultsDirPath = getPropertyOrFail(properties, RESULTS_OUT_DIR_PATH_P);
            delimiter = getPropertyOrDefault(properties, DELIMITER_P, DEFAULT_DELIMITER);
            dt = parseDouble(getPropertyOrFail(properties, DT_P));
            tf = parseDouble(getPropertyOrDefault(properties, TF_P, "5"));
        } catch (Exception E) {
            printClientUsage();
            return;
        }

        final File staticFile = Paths.get(staticFilePath).toFile();
        final File dynamicFile = Paths.get(dynamicFilePath).toFile();

        final ParseUtils.ParticlesParserResult particlesParserResult = parseParticlesList(staticFile,
            dynamicFile,
            delimiter);

        Map<Particle, State> particles = particlesParserResult.getParticlesPerTime().get(0);
        State sunState = null;
        Particle venus = null;
        State venusState = null;
        Particle spaceship = null;
        State spaceshipState = null;

        for (Map.Entry<Particle, State> e : particles.entrySet()) {
            Particle p = e.getKey();
            State s = e.getValue();
            switch (p.getId()) {
                case SUN:
                    sunState = s;
                    break;
                case EARTH:
                    break;
                case VENUS:
                    venus = p;
                    venusState = s;
                    break;
                case SPACESHIP:
                    spaceship = p;
                    spaceshipState = s;
                    break;
            }
        }

        if (sunState == null || venusState == null || spaceshipState == null) {
            printClientUsage();
            return;
        }

        double sunx = (sunState.getPosition().getX());
        double suny = (sunState.getPosition().getY());
        double venusx = (venusState.getPosition().getX());
        double venusy = (venusState.getPosition().getY());
        double venusvx = (venusState.getVelocityX());
        double venusvy = (venusState.getVelocityY());
        double venusR = (venus.getRadius());

        //https://math.stackexchange.com/questions/2045174/how-to-find-a-point-between-two-points-with-given-distance
        double d = sqrt((pow((venusx - sunx), 2) + pow((venusy - suny), 2)));
        // Componentes del versor que une el sol con venus (normal a la orbita)
        double rx = (venusx - sunx) / d;
        double ry = (venusy - suny) / d;

        // Componentes del versor tangencial a la orbita
        double ox = -ry;
        double oy = rx;

        //Position
        double spaceshipx = (DISTANCE_TO_SPACESHIP + venusR) * rx + venusx;
        double spaceshipy = (DISTANCE_TO_SPACESHIP + venusR) * ry + venusy;

        for (double speed = MIN_SPEED; speed <= MAX_SPEED; speed += SPEED_STEP) {
            //Velocity
            double vt = speed + ORBITAL_SPEED + venusvx * ox + venusvy * oy;
            double spaceshipvx = ox * vt;
            double spaceshipvy = oy * vt;
            spaceshipState = new State(new Position(spaceshipx, spaceshipy), spaceshipvx, spaceshipvy);
            particles.put(spaceship, spaceshipState);

            LOGGER.info(String.format("Speed %f", speed));
            LOGGER.info(String.format("   Spaceship %f", ORBITAL_SPEED + speed));
            LOGGER.info("   Executing Venus Mission ...");

            final AlgorithmResults methodResults = SpaceMission.execute(
                particles,
                dt,
                tf
            );

            LOGGER.info(String.format("   Finished Earth Mission In %d Iterations",
                methodResults.getIterations()));
            LOGGER.info("   Writing Results ...");
            final String[] fileName = dynamicFile.getName().split("_");
            final String outResultsPath = String.format("%s/%s %s %f", outResultsDirPath, fileName[1], fileName[2], speed);
            final File outResultsFile = new File(outResultsPath);

            int index = 0;
            double step = 6;

            try (PrintWriter pw = new PrintWriter(outResultsFile)) {
                for (Map.Entry<Double, Map<Particle, State>> entry : methodResults.getParticlesStates().entrySet()) {
                    Double time = entry.getKey();
                    Map<Particle, State> states = entry.getValue();
                    if (index % step == 0) {
                        pw.append(String.format("%f\n", time));
                        states.forEach((particle, state) ->
                            pw.printf("%d %.16f %.16f %.16f %.16f\n",
                                particle.getId(),
                                state.getPosition().getX(), state.getPosition().getY(),
                                state.getVelocityX(), state.getVelocityY()));

                    }
                    index += 1;
                }
            }

            LOGGER.info("   Done!\n");
        }

    }

    private static void printClientUsage() {
        System.out.println("Invalid simulator invocation.\n" +
            "Usage: ./simulator -DstaticFile='path/to/static/file' -DdynamicFilesDir='path/to/dynamic/files/dir' " +
            " -DresultsDir=resultsDir [-Ddelimiter=delimiter] -Ddt=dt -Dtf=tf"
        );
    }

}

