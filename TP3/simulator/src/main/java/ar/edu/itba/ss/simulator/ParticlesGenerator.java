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
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.Math.*;

public class ParticlesGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParticlesGenerator.class);
    private static final String STATIC_FILE_PATH_P = "staticFile";
    private static final String DYNAMIC_FILE_PATH_P = "dynamicFile";
    private static final String N_P = "N";
    private static final String DELIMITER_P = "delimiter";
    private static final String DEFAULT_DELIMITER = " ";
    private static final String PARTICLE_MIN_SPEED_P = "particleMinSpeed";
    private static final String PARTICLE_MAX_SPEED_P = "particleMaxSpeed";
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
        final int particleMinSpeed = fileArguments.getParticleMinSpeed();
        final int particleMaxSpeed = fileArguments.getParticleMaxSpeed();
        final double smallParticleR = 0.2;
        final double bigParticleR = 0.7;
        final double smallParticleMass = 0.9;
        final double bigParticleMass = 2;

        Map<Particle, State> particles = generateParticles(fileArguments.getN(), L, bigParticleR,
                bigParticleMass, smallParticleR, smallParticleMass, particleMaxSpeed, particleMinSpeed);

        //Static File
        try (PrintWriter pw = new PrintWriter(fileArguments.getStaticFile())) {

            pw.println(fileArguments.getN());
            pw.println(L);

            for (Map.Entry<Particle, State> entry : particles.entrySet()) {
                pw.printf("%f %f\n", entry.getKey().getRadius(), entry.getKey().getMass());
            }
        }

        //Dynamic File

        try (PrintWriter pw = new PrintWriter(fileArguments.getDynamicFile())) {
            pw.println(0);
            for (Map.Entry<Particle, State> entry : particles.entrySet()) {
                pw.printf("%f %f %f %f\n", entry.getValue().getPosition().getX(), entry.getValue().getPosition().getY(),
                        entry.getValue().getVelocityX(), entry.getValue().getVelocityX());
            }
        }

        LOGGER.info("Finished!");

    }

    private static FileGeneratorArguments getAndParseBaseArguments(final Properties properties) throws IllegalArgumentException {
        final String staticFilePath = getPropertyOrFail(properties, STATIC_FILE_PATH_P);
        final String dynamicFilePath = getPropertyOrFail(properties, DYNAMIC_FILE_PATH_P);

        final String delimiter = getPropertyOrDefault(properties, DELIMITER_P, DEFAULT_DELIMITER);

        final int N = parseInt(getPropertyOrDefault(properties, N_P, "100"));
        final int particleMinSpeed = parseInt(getPropertyOrDefault(properties, PARTICLE_MIN_SPEED_P, "0"));
        final int particleMaxSpeed = parseInt(getPropertyOrDefault(properties, PARTICLE_MAX_SPEED_P, "2"));

        final File staticFile = new File(staticFilePath);
        final File dynamicFile = new File(dynamicFilePath);

        return new FileGeneratorArguments(staticFile, dynamicFile, N, delimiter, particleMinSpeed, particleMaxSpeed);
    }

    public static Map<Particle, State> generateParticles(final int N,
                                                         final int L,
                                                         final double bigParticleR,
                                                         final double bigParticleMass,
                                                         final double smallParticleR,
                                                         final double smallParticleMass,
                                                         final double particleMaxSpeed,
                                                         final double particleMinSpeed) {

        // Particle info
        List<Particle> particlesArray = new ArrayList<>();
        int id = 0;

        particlesArray.add(new Particle(id++, bigParticleR, bigParticleMass));

        for (int i = 0; i < N; i++) {
            particlesArray.add(new Particle(id++, smallParticleR, smallParticleMass));
        }

        final Map<Particle, State> particles = new TreeMap<>((Comparator.comparingInt(Particle::getId)));
        final Random random = new Random();


        // Particle state
        int particleId = 0;

        // Generate Big Particle
        final Position bigParticlePosition = new Position((double) L / 2, (double) L / 2);
        final State bigParticleState = new State(bigParticlePosition, 0, 0);

        particles.put(particlesArray.get(particleId), bigParticleState);

        // Generate small particles positions
        for (int j = 0; j < N; j++) {
            boolean success = false;
            particleId++;

            final double speed = particleMinSpeed + Math.random() * (particleMaxSpeed - particleMinSpeed);
            final double angle = random.nextDouble() * (MAX_ANGLE);
            final double velocityX = speed * cos(angle);
            final double velocityY = speed * sin(angle);

            State newState = null;
            while (!success) {
                success = true;

                final double offset = random.nextDouble();

                final double x = smallParticleR + offset + random.nextDouble() * (L - 2 * smallParticleR - offset);
                final double y = smallParticleR + offset + random.nextDouble() * (L - 2 * smallParticleR - offset);

                newState = new State(new Position(x, y), velocityX, velocityY);

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
        }
        return particles;
    }

    private static void printClientUsage() {
        System.out.println("Invalid generator invocation.\n" +
                "Usage: ./files_generator -DstaticFile='path/to/static/file' -DdynamicFile='path/to/dynamic/file' " +
                "-DN=N -DparticleMinSpeed=minSpeed -DparticleMaxSpeed=maxSpeed");
    }
}
