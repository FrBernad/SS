package ar.edu.itba.ss.simulator;

import ar.edu.itba.ss.simulator.utils.FileGeneratorArguments;
import ar.edu.itba.ss.simulator.utils.Particle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrDefault;
import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrFail;
import static ar.edu.itba.ss.simulator.utils.Particle.Position;
import static ar.edu.itba.ss.simulator.utils.Particle.State;
import static java.lang.Integer.parseInt;
import static java.lang.Math.pow;

public class ParticlesGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParticlesGenerator.class);
    private static final String STATIC_FILE_PATH_P = "staticFile";
    private static final String DYNAMIC_FILE_PATH_P = "dynamicFile";
    private static final String N_P = "N";
    private static final String DELIMITER_P = "delimiter";
    private static final String DEFAULT_DELIMITER = " ";
    private static final Double MAX_ANGLE = 2 * Math.PI;

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

        final int L = 6;
        final int particleMinSpeed = 0;
        final int particleMaxSpeed = 2;
        final double smallParticleR = 0.2;
        final double bigParticleR = 0.7;
        final double smallParticleMass = 0.9;
        final double bigParticleMass = 2;

        List<Particle> particlesArray = new ArrayList<>();
        try (PrintWriter pw = new PrintWriter(fileArguments.getStaticFile())) {
            int id = 0;

            pw.println(fileArguments.getN());
            pw.println(L);

            particlesArray.add(new Particle(id++, bigParticleR, bigParticleMass));

            pw.printf("%f %f\n", bigParticleR, bigParticleMass);

            for (int i = 0; i < fileArguments.getN(); i++) {
                particlesArray.add(new Particle(id++, smallParticleR, smallParticleMass));
                pw.printf("%f %f\n", smallParticleR, smallParticleMass);
            }
        }


        try (PrintWriter pw = new PrintWriter(fileArguments.getDynamicFile())) {
            final Random random = new Random();

            int particleId = 0;

            final Map<Particle, State> particles = new HashMap<>();

            //Generate Big Particle
            final Position bigParticlePosition = new Position((double) L / 2, (double) L / 2);
            final State bigParticleState = new State(bigParticlePosition, 0, random.nextDouble() * (MAX_ANGLE));
            //FIXME: Tiene angulo inicial ???
            particles.put(particlesArray.get(particleId), bigParticleState);

            // Write Big Particle
            pw.println(0);
            pw.printf("%f %f %f %f\n", bigParticleState.getPosition().getX(), bigParticleState.getPosition().getY(),
                bigParticleState.getSpeed(), bigParticleState.getAngle());

            // Generate small particles positions
            for (int j = 0; j < fileArguments.getN(); j++) {
                boolean success = false;
                particleId++;

                final double speed = particleMinSpeed + Math.random() * (particleMaxSpeed - particleMinSpeed);
                final double angle = random.nextDouble() * (MAX_ANGLE);
                State newState = null;
                while (!success) {
                    success = true;

                    // Avoid borders interception
                    double x = smallParticleR + random.nextDouble() * (L - 2*smallParticleR);
                    double y = smallParticleR + random.nextDouble() * (L - 2*smallParticleR);
                    newState = new State(new Position(x, y), speed, angle);

                    for (Map.Entry<Particle, State> entry : particles.entrySet()) {
                        final Particle otherParticle = entry.getKey();
                        final Position otherPosition = entry.getValue().getPosition();
                        final double radiusDistance = pow(otherParticle.getRadius() + particlesArray.get(particleId).getRadius(), 2);
                        final double particlesDistance = pow(otherPosition.getX() - x, 2) + pow(otherPosition.getY() - y, 2);

                        if (particlesDistance <= radiusDistance) {
                            success = false;
                            break;
                        }
                    }
                }
                particles.put(particlesArray.get(particleId), newState);
                pw.printf("%f %f %f %f\n", newState.getPosition().getX(), newState.getPosition().getY(), newState.getSpeed(), newState.getAngle());
            }
        }

        LOGGER.info("Finished!");

    }

    private static FileGeneratorArguments getAndParseBaseArguments(final Properties properties) throws IllegalArgumentException {
        final String staticFilePath = getPropertyOrFail(properties, STATIC_FILE_PATH_P);
        final String dynamicFilePath = getPropertyOrFail(properties, DYNAMIC_FILE_PATH_P);

        final String delimiter = getPropertyOrDefault(properties, DELIMITER_P, DEFAULT_DELIMITER);

        final int N = parseInt(getPropertyOrDefault(properties, N_P, "100"));

        final File staticFile = new File(staticFilePath);
        final File dynamicFile = new File(dynamicFilePath);

        return new FileGeneratorArguments(staticFile, dynamicFile, N, delimiter);
    }

    public static Map<Particle, State> generateParticles(final int N,
                                                         final int L,
                                                         final double particleRadius,
                                                         final double particleMass,
                                                         final double particleMaxSpeed,
                                                         final double particleMinSpeed,
                                                         final int startId) {
        Map<Particle, State> particles = new HashMap<>();
        final Random random = new Random();

        for (int i = 0; i < N; i++) {
            Position position = new Position(random.nextDouble() * L, random.nextDouble() * L);
            double angle = random.nextDouble() * (MAX_ANGLE);
            particles.put(
                new Particle(startId + i, particleRadius, particleMass),
                new State(position, particleMinSpeed + Math.random() * (particleMaxSpeed - particleMinSpeed), angle)
            );
        }
        return particles;
    }

    private static void printClientUsage() {
        System.out.println("Invalid generator invocation.\n" +
            "Usage: ./files_generator -DstaticFile='path/to/static/file' -DdynamicFile='path/to/dynamic/file' " +
            "-DN=N");
    }
}
