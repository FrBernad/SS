package ar.edu.itba.ss.simulator;

import ar.edu.itba.ss.simulator.algorithms.flocks.Flocks;
import ar.edu.itba.ss.simulator.algorithms.flocks.FlocksAlgorithmResults;
import ar.edu.itba.ss.simulator.utils.BaseArguments;
import ar.edu.itba.ss.simulator.utils.Particle;
import ar.edu.itba.ss.simulator.utils.Particle.State;
import com.sun.source.tree.Tree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.*;

import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrDefault;
import static ar.edu.itba.ss.simulator.utils.ArgumentsUtils.getPropertyOrFail;
import static ar.edu.itba.ss.simulator.utils.ParseUtils.ParticlesParserResult;
import static ar.edu.itba.ss.simulator.utils.ParseUtils.parseParticlesList;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class OrderParameterGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderParameterGenerator.class);
    private static final String STATIC_FILE_PATH_P = "staticFile";
    private static final String DYNAMIC_FILE_PATH_P = "dynamicFile";
    private static final String OUT_FILE_PATH_P = "outFilePath";
    private static final Double MIN_ETA = 0.1;
    private static final Double MAX_ETA = 1.0;
    private static final Double ETA_STEP = 0.1;
    private static final int ITERATIONS = 5;
    private static final double THRESHOLD = 0.01;
    private static final int MAX_ITERATIONS_OVER_THRESHOLD = 20;

    public static void main(String[] args) throws IOException {
        LOGGER.info("Order Parameter Generator Starting ...");

        final Properties properties = System.getProperties();
        final String staticFilePath = getPropertyOrFail(properties, STATIC_FILE_PATH_P);
        final String dynamicFilePath = getPropertyOrFail(properties, DYNAMIC_FILE_PATH_P);

        final String outFilePath = getPropertyOrFail(properties, OUT_FILE_PATH_P);

        final File staticFile = Paths.get(staticFilePath).toFile();
        final File dynamicFile = Paths.get(dynamicFilePath).toFile();

        final ParticlesParserResult particlesParserResult = parseParticlesList(
            staticFile,
            dynamicFile,
            " "
        );

        final Map<Particle, State> particles = particlesParserResult.getParticlesPerTime().get(0);

        final double maxRadius = particles
            .keySet()
            .stream()
            .map(Particle::getRadius)
            .max(Double::compare).orElseThrow();

        final double R = 1;

        final double gridCondition = particlesParserResult.getL() / R + 2 * maxRadius;
        int optimalM = (int) Math.floor(gridCondition);
        if (gridCondition == (int) gridCondition) {
            optimalM = (int) gridCondition - 1;
        }

        System.out.printf("Max M casted: %s\nMax M not casted: %f\n", optimalM, (particlesParserResult.getL() / (R + 2 * maxRadius)));

        final Map<Double, List<Double>> orderParameters = new TreeMap<>(Double::compare);

        for (double eta = MIN_ETA; eta <= MAX_ETA; eta += ETA_STEP) {

            System.out.printf("Calculating for eta = %f: ", eta);

            orderParameters.put(eta, new ArrayList<>());

            for (int n = 0; n < ITERATIONS; n++) {
                System.out.printf("%d ", n);

                FlocksAlgorithmResults methodResults = Flocks.execute(
                    particles,
                    particlesParserResult.getN(),
                    particlesParserResult.getL(),
                    optimalM,
                    R,
                    1,
                    eta,
                    THRESHOLD,
                    true,
                    MAX_ITERATIONS_OVER_THRESHOLD
                );

                orderParameters.get(eta).add(methodResults.getOrderParameter().get(methodResults.getOrderParameter().size() - 1));
            }
            System.out.println();
        }

        try (PrintWriter pw = new PrintWriter(outFilePath)) {
            pw.printf("%d ", particlesParserResult.getN());
            pw.printf("%d ", particlesParserResult.getL());
            pw.printf("%f ", R);
            pw.printf("%d\n", ITERATIONS);

            orderParameters.forEach((eta, ops) -> {
                pw.printf("%f", eta);
                ops.forEach(op -> pw.printf(" %f", op));
                pw.printf("\n");
            });
        }

        LOGGER.info("Finished!");

    }


}
