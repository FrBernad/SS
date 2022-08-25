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

public class OrderParameterGeneratorL {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderParameterGeneratorETA.class);
    private static final String OUT_FILE_PATH_P = "outFilePath";
    private static final String MAX_ITERATIONS_P = "maxIterations";
    private static final String SIMULATIONS_QUANTITY_P = "simulationsQuantity";
    private static final String N_P = "N";
    private static final String ETA_P = "eta";

    private static final int MIN_L = 2;
    private static final int MAX_L = 50;
    private static final int L_STEP = 1;
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
        final int N = parseInt(getPropertyOrDefault(properties, N_P, "300"));

        final Map<Integer, List<Double>> orderParameters = new TreeMap<>(Integer::compare);

        for (int l = MIN_L; l <= MAX_L; l += L_STEP) {

            System.out.printf("Calculating for L = %d: ", l);

            orderParameters.put(l, new ArrayList<>());
            Map<Particle, State> particles = ParticlesGenerator.generateParticles(N, l, RADIUS, PROPERTY, SPEED);

            final double maxRadius = particles
                    .keySet()
                    .stream()
                    .map(Particle::getRadius)
                    .max(Double::compare).orElseThrow();

            final double gridCondition = l / RADIUS_RC + 2 * maxRadius;
            int optimalM = (int) Math.floor(gridCondition);
            if (gridCondition == (int) gridCondition) {
                optimalM = (int) gridCondition - 1;
            }

            FlocksAlgorithmResults methodResults = Flocks.execute(
                    particles,
                    N,
                    l,
                    optimalM,
                    RADIUS_RC,
                    1,
                    eta,
                    true,
                    maxIterations);

            orderParameters.get(l).addAll(methodResults.getOrderParameter());

        }

        try (PrintWriter pw = new PrintWriter(outFilePath)) {
            pw.printf("%d ", N);
            pw.printf("%f ", eta);
            pw.printf("%f ", RADIUS);
            pw.printf("%d ", maxIterations);

            orderParameters.forEach((l, ops) -> {
                pw.printf("%d", l);
                ops.forEach(op -> pw.printf(" %f", op));
                pw.printf("\n");
            });
        }

        LOGGER.info("Finished!");

    }


}
