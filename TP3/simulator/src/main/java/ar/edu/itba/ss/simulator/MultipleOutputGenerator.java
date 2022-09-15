package ar.edu.itba.ss.simulator;

import ar.edu.itba.ss.simulator.algorithms.brownianmotion.BrownianMotion;
import ar.edu.itba.ss.simulator.utils.ActionLogger;
import ar.edu.itba.ss.simulator.utils.BrownianMotionAlgorithmResults;
import ar.edu.itba.ss.simulator.utils.Particle;
import ar.edu.itba.ss.simulator.utils.Particle.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrDefault;
import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrFail;
import static java.lang.Integer.parseInt;

public class MultipleOutputGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultipleOutputGenerator.class);
    private static final String N_P = "N";
    private static final int L = 6;
    private static final double BIG_PARTICLE_R = 0.7;
    private static final double BIG_PARTICLE_MASS = 2;
    private static final double SMALL_PARTICLE_R = 0.2;
    private static final double SMALL_PARTICLE_MASS = 0.9;
    private static final double PARTICLE_MAX_SPEED = 0;
    private static final double PARTICLE_MIN_SPEED = 2;
    private static final String MAX_ITERATIONS_P = "maxIterations";
    private static final String RESULTS_OUT_PATH_P = "resultsDirectory";
    private final static String RUNS_P = "runs";


    public static void main(String[] args) throws FileNotFoundException {
        final Properties properties = System.getProperties();
        final String resultsDirectoryPath;
        final int maxIterations;
        final int N;
        final int runs;

        try {
            resultsDirectoryPath = getPropertyOrFail(properties, RESULTS_OUT_PATH_P);
            maxIterations = parseInt(getPropertyOrDefault(properties, MAX_ITERATIONS_P, "20000"));
            N = parseInt(getPropertyOrDefault(properties, N_P, "120"));
            runs = parseInt(getPropertyOrDefault(properties, RUNS_P, "50"));
        } catch (IllegalArgumentException e) {
            printClientUsage();
            return;
        }

        boolean staticFileGenerated = false;

        for (int i = 0; i < runs; i++) {

            LOGGER.info("Generating particles for run {}", i + 1);


            Map<Particle, State> particles = ParticlesGenerator.generateParticles(N,
                    L, BIG_PARTICLE_R, BIG_PARTICLE_MASS, SMALL_PARTICLE_R, SMALL_PARTICLE_MASS,
                    PARTICLE_MAX_SPEED, PARTICLE_MIN_SPEED);

            if (!staticFileGenerated) {
                staticFileGenerated = true;
                //Static File
                final String directory = String.format("%sStatic.txt", resultsDirectoryPath);
                final File staticFile = new File(directory);
                try (PrintWriter pw = new PrintWriter(staticFile)) {

                    pw.println(N);
                    pw.println(L);

                    for (Map.Entry<Particle, State> entry : particles.entrySet()) {
                        pw.printf("%f %f\n", entry.getKey().getRadius(), entry.getKey().getMass());
                    }
                }

            }

            LOGGER.info("Simulating run {}", i + 1);
            BrownianMotionAlgorithmResults methodResults = BrownianMotion.execute(
                    particles,
                    L,
                    maxIterations);

            LOGGER.info(String.format("Finished run %d Brownian Motion In %d Iterations / %.3f Seconds!",
                    i + 1, methodResults.getIterations(), methodResults.getSimulationTime()));


            LOGGER.info("Writing Results ...");
            final String resultsDirectory = String.format("%srun%d", resultsDirectoryPath, i + 1);
            final File outResultsFile = new File(resultsDirectory);
            try (PrintWriter pw = new PrintWriter(outResultsFile)) {
                methodResults.getParticlesStates()
                        .forEach((time, states) -> {
                            pw.append(String.format("%f\n", time));
                            states.forEach((particle, state) ->
                                    pw.printf("%d %f %f %f %f\n",
                                            particle.getId(),
                                            state.getPosition().getX(), state.getPosition().getY(),
                                            state.getVelocityX(), state.getVelocityY()));
                        });
            }
            LOGGER.info("Done ");


        }

    }

    private static void printClientUsage() {
        System.out.println("Invalid generator invocation.\n" +
                "Usage: ./files_generator -DresultsDirectory='path/to/results/directory' " +
                "-DN=N -Druns=50 -DmaxIterations=maxIterations");
    }
}
