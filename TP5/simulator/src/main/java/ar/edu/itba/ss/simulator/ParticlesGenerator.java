package ar.edu.itba.ss.simulator;

import ar.edu.itba.ss.simulator.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrDefault;
import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrFail;
import static ar.edu.itba.ss.simulator.utils.R.values.R0;
import static ar.edu.itba.ss.simulator.utils.R.values.R1;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.Math.pow;

public class ParticlesGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParticlesGenerator.class);
    private static final String STATIC_FILE_PATH_P = "staticFile";
    private static final String DYNAMIC_FILE_PATH_P = "dynamicFile";

    /*Java Properties*/
    private static final String N_P = "N";
    private static final String L_P = "L";
    private static final String W_P = "W";
    private static final String DELIMITER_P = "delimiter";
    private static final String PARTICLE_MASS_P = "mass";
    private static final String R0_P = "r0";
    private static final String DR_P = "dr";
    private static final String INITIAL_VX_P = "vx";
    private static final String INITIAL_VY_P = "vy";
    private static final String SEED_P = "seed";

    /*Default Properties*/

    private static final String DEFAULT_DELIMITER = " ";


    public static void main(String[] args) throws IOException {
        LOGGER.info("Files Generator Starting ...");

        FileGeneratorArguments fileArguments;

        final Properties properties = System.getProperties();

        try {
            LOGGER.info("Parsing arguments ...");
            fileArguments = getAndParseBaseArguments(properties);
        } catch (IllegalArgumentException e) {
            printClientUsage();
            return;
        }

        LOGGER.info("Generating files ...");

        RandomGenerator.setInstance(fileArguments.getSeed());

        Map<Particle, R> particles = generateParticles(fileArguments.getN(), fileArguments.getL(),
            fileArguments.getW(), fileArguments.getMass(),
            fileArguments.getR0(), fileArguments.getDr(),
            fileArguments.getVx(), fileArguments.getVy());

        //Static File
        try (PrintWriter pw = new PrintWriter(fileArguments.getStaticFile())) {

            pw.println(fileArguments.getN());
            pw.println(fileArguments.getL());

            for (Map.Entry<Particle, R> entry : particles.entrySet()) {
                pw.printf("%f %f\n", entry.getKey().getRadius(), entry.getKey().getMass());
            }
        }

        //Dynamic File
        try (PrintWriter pw = new PrintWriter(fileArguments.getDynamicFile())) {
            pw.println(0);
            for (Map.Entry<Particle, R> entry : particles.entrySet()) {
                pw.printf("%f %f %f %f\n", entry.getValue().get(R0.ordinal()).getX(), entry.getValue().get(R0.ordinal()).getY(),
                    entry.getValue().get(R1.ordinal()).getX(), entry.getValue().get(R1.ordinal()).getY());
            }
        }

        LOGGER.info("Finished!");

    }

    private static FileGeneratorArguments getAndParseBaseArguments(final Properties properties) throws IllegalArgumentException {
        final String staticFilePath = getPropertyOrFail(properties, STATIC_FILE_PATH_P);
        final String dynamicFilePath = getPropertyOrFail(properties, DYNAMIC_FILE_PATH_P);

        final String delimiter = getPropertyOrDefault(properties, DELIMITER_P, DEFAULT_DELIMITER);


        final int N = parseInt(getPropertyOrFail(properties, N_P));
        final int L = parseInt(getPropertyOrFail(properties, L_P));

        final int W = parseInt(getPropertyOrFail(properties, W_P));

        final double mass = parseDouble(getPropertyOrFail(properties, PARTICLE_MASS_P));

        final double r0 = parseDouble(getPropertyOrFail(properties, R0_P));

        final double dr = parseDouble(getPropertyOrFail(properties, DR_P));

        final double vx = parseDouble(getPropertyOrFail(properties, INITIAL_VX_P));

        final double vy = parseDouble(getPropertyOrFail(properties, INITIAL_VY_P));

        Long seed;
        try {
            seed = Long.parseLong(getPropertyOrFail(properties, SEED_P));
        } catch (Exception e) {
            seed = null;
        }


        final File staticFile = new File(staticFilePath);
        final File dynamicFile = new File(dynamicFilePath);

        return new FileGeneratorArguments(staticFile, dynamicFile, delimiter, N, L, W, mass, r0, dr, vx, vy, seed);
    }

    public static Map<Particle, R> generateParticles(final int N, final double L,
                                                     final double W, final double mass,
                                                     final double r0, final double dr,
                                                     final double initialVx, final double initialVy) {

        final Map<Particle, R> particles = new HashMap<>();

        final Random random = RandomGenerator.getInstance().getRandom();

        // Particle state
        int particleId = 0;
        final double maxRadius = r0 + dr;
        final double minRadius = r0 - dr;

        double scale1 = BigDecimal.valueOf(maxRadius).scale();
        double scale2 = BigDecimal.valueOf(minRadius).scale();

        double scale = Math.max(scale1, scale2);
        scale = 1 / pow(10, scale);


        // Generate particles positions
        for (int j = 0; j < N; j++) {

            final double radius = random.nextDouble() * ((maxRadius - minRadius) + scale) + minRadius;
            final double offset = random.nextDouble();

            Particle particle = new Particle(particleId, radius, mass);
            particleId++;

            final R particleState = generateParticleState(radius + offset, L - radius,
                radius + offset, W - radius - offset, initialVx, initialVy, particle, particles);
            particles.put(particle, particleState);
        }
        return particles;
    }

    public static R generateParticleState(final double minHeight, final double maxHeight,
                                          final double minWidth, final double maxWidth,
                                          final double initialVx, final double initialVy,
                                          final Particle particle, final Map<Particle, R> particles) {
        final Random random = RandomGenerator.getInstance().getRandom();

        Pair r0 = null;
        Pair r1 = null;

        boolean success = false;
        while (!success) {
            success = true;

            final double x = random.nextDouble() * (maxWidth - minWidth) + minWidth;

            final double y = random.nextDouble() * (maxHeight - minHeight) + minHeight;
            r0 = new Pair(x, y);
            r1 = new Pair(initialVx, initialVy);

            for (Map.Entry<Particle, R> entry : particles.entrySet()) {
                final Particle otherParticle = entry.getKey();
                final Pair otherPosition = entry.getValue().get(R0.ordinal());
                final double radiusDistance = pow(otherParticle.getRadius() + particle.getRadius(), 2);
                final double particlesDistance = pow(otherPosition.getX() - x, 2) + pow(otherPosition.getY() - y, 2);

                if (particlesDistance <= radiusDistance) {
                    success = false;
                    break;
                }
            }
        }
        final R newR = new R();
        newR.set(R0.ordinal(), r0.getX(), r0.getY());
        newR.set(R1.ordinal(), r1.getX(), r1.getY());
        return newR;
    }


    private static void printClientUsage() {
        System.out.println("Invalid generator invocation.\n" +
            "Usage: ./generator " +
            "-DstaticFile='path/to/static/file' " +
            "-DdynamicFile='path/to/dynamic/file' " +
            "-DN=N -DL=L -DW=W -Dmass=mass -Dr0=r0 -Ddr=dr -Dvx=vx -Dvy=vy -Dseed=seed");
    }
}
