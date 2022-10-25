package ar.edu.itba.ss.simulator;

import ar.edu.itba.ss.simulator.utils.FileGeneratorArguments;
import ar.edu.itba.ss.simulator.utils.Pair;
import ar.edu.itba.ss.simulator.utils.Particle;
import ar.edu.itba.ss.simulator.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrDefault;
import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrFail;
import static ar.edu.itba.ss.simulator.utils.R.values.*;

import static java.lang.Integer.parseInt;
import static java.lang.Math.pow;

public class ParticlesGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParticlesGenerator.class);
    private static final String STATIC_FILE_PATH_P = "staticFile";
    private static final String DYNAMIC_FILE_PATH_P = "dynamicFile";
    private static final String N_P = "N";
    private static final String DELIMITER_P = "delimiter";
    private static final String DEFAULT_DELIMITER = " ";
    private static final String DEFAULT_N = "25";
    private static final int L = 70;
    private static final int W = 20;
    private static final double PARTICLE_MASS = 1;
    private static final double MIN_RADIUS = 0.85;
    private static final double MAX_RADIUS = 1.15;
    private static final double INITIAL_VELOCITY = 0;

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

        Map<Particle, R> particles = generateParticles(fileArguments.getN(), L, W);

        //Static File
        try (PrintWriter pw = new PrintWriter(fileArguments.getStaticFile())) {

            pw.println(fileArguments.getN());
            pw.println(L);

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

        final int N = parseInt(getPropertyOrDefault(properties, N_P, DEFAULT_N));

        final File staticFile = new File(staticFilePath);
        final File dynamicFile = new File(dynamicFilePath);

        return new FileGeneratorArguments(staticFile, dynamicFile, N, delimiter);
    }

    public static Map<Particle, R> generateParticles(final int N, final int L, final int W) {

        final Map<Particle, R> particles = new TreeMap<>((Comparator.comparingInt(Particle::getId)));
        final Random random = new Random();

        // Particle state
        int particleId = 0;

        // Generate particles positions
        for (int j = 0; j < N; j++) {

            final double radius = random.nextDouble() * ((MAX_RADIUS - MIN_RADIUS) + 0.01) + MIN_RADIUS;

            Particle particle = new Particle(particleId, radius, PARTICLE_MASS);
            particleId++;

            final R particleState = generateParticleState(radius, L - radius, radius, W - radius, particle, particles);
            particles.put(particle, particleState);
        }
        return particles;
    }

    public static R generateParticleState(final double minHeight, final double maxHeight,
                                          final double minWidth, final double maxWidth,
                                          final Particle particle, Map<Particle, R> particles) {
        final Random random = new Random();

        Pair r0 = null;
        Pair r1 = null;

        boolean success = false;
        while (!success) {
            success = true;

            final double x = random.nextDouble() * (maxWidth - minWidth) + minWidth;
            final double y = random.nextDouble() * (maxHeight - minHeight) + minHeight;
            r0 = new Pair(x, y);
            r1 = new Pair(INITIAL_VELOCITY, INITIAL_VELOCITY);

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
        newR.add(r0.getX(), r0.getY());
        newR.add(r1.getX(), r1.getY());
        return newR;
    }


    private static void printClientUsage() {
        System.out.println("Invalid generator invocation.\n" +
            "Usage: ./files_generator -DstaticFile='path/to/static/file' -DdynamicFile='path/to/dynamic/file'");
    }
}
