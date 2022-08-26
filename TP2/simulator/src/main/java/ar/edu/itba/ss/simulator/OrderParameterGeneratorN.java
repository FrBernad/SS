package ar.edu.itba.ss.simulator;

import ar.edu.itba.ss.simulator.algorithms.flocks.Flocks;
import ar.edu.itba.ss.simulator.algorithms.flocks.FlocksAlgorithmResults;
import ar.edu.itba.ss.simulator.utils.Particle;
import ar.edu.itba.ss.simulator.utils.Particle.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrDefault;
import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrFail;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class OrderParameterGeneratorN {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderParameterGeneratorN.class);
    private static final String OUT_FILE_PATH_P = "outFilePath";
    private static final String MAX_ITERATIONS_P = "maxIterations";
    private static final String L_P = "L";
    private static final String ETA_P = "eta";

    private static final List<Integer> N_LIST = new ArrayList<>(Arrays.asList(2, 12, 20, 50, 75, 80, 100, 150, 200, 300, 500, 800, 1200));
    private static final double RADIUS_RC = 1; // interaction radius
    private static final double RADIUS = 0; // particle's radius
    private static final double PROPERTY = 0; // particle's property

    private static final double SPEED = 0.03;


    public static void main(String[] args) throws IOException {
        LOGGER.info("Order Parameter Generator Starting ...");

        final Properties properties = System.getProperties();

        final String outFilePath = getPropertyOrFail(properties, OUT_FILE_PATH_P);

        final int maxIterations = parseInt(getPropertyOrDefault(properties, MAX_ITERATIONS_P, "3000"));
        final double eta = parseDouble(getPropertyOrDefault(properties, ETA_P, "0.2"));
        final int L = parseInt(getPropertyOrDefault(properties, L_P, "300"));

        final Map<Integer, List<Double>> orderParameters = new TreeMap<>(Integer::compare);
        N_LIST.forEach(n -> {
            System.out.printf("Calculating for N = %d: \n", n);

            orderParameters.put(n, new ArrayList<>());
            Map<Particle, State> particles = ParticlesGenerator.generateParticles(n, L, RADIUS, PROPERTY, SPEED);

            final double maxRadius = particles
                    .keySet()
                    .stream()
                    .map(Particle::getRadius)
                    .max(Double::compare).orElseThrow();

            final double gridCondition = L / RADIUS_RC + 2 * maxRadius;
            int optimalM = (int) Math.floor(gridCondition);
            if (gridCondition == (int) gridCondition) {
                optimalM = (int) gridCondition - 1;
            }

            FlocksAlgorithmResults methodResults = Flocks.execute(
                    particles,
                    n,
                    L,
                    optimalM,
                    RADIUS_RC,
                    1,
                    eta,
                    true,
                    maxIterations);

            orderParameters.get(n).addAll(methodResults.getOrderParameter());


        });

        try (PrintWriter pw = new PrintWriter(outFilePath)) {
            pw.printf("%d ", L);
            pw.printf("%f ", eta);
            pw.printf("%f ", RADIUS);
            pw.printf("%d\n", maxIterations);

            orderParameters.forEach((n, ops) -> {
                pw.printf("%d", n);
                ops.forEach(op -> pw.printf(" %f", op));
                pw.printf("\n");
            });
        }

        LOGGER.info("Finished!");

    }


}
