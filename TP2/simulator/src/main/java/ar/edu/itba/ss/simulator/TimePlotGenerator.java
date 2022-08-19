//package ar.edu.itba.ss.simulator;
//
//import ar.edu.itba.ss.simulator.utils.Particle;
//import ar.edu.itba.ss.simulator.utils.Particle.State;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static ar.edu.itba.ss.simulator.methods.cellIndex.CellIndexMethod.calculateNeighbors;
//import static ar.edu.itba.ss.simulator.utils.ParseUtils.*;
//
//public class TimePlotGenerator {
//    private static final Logger LOGGER = LoggerFactory.getLogger(TimePlotGenerator.class);
//    private static final String dynamicFilePath = "/Users/frbernad/PROGRAMMING/ITBA/SS/TPs/TP2/results/Dynamic2.txt";
//
//    private static final String staticFilePath = "/Users/frbernad/PROGRAMMING/ITBA/SS/TPs/TP2/results/Static2.txt";
//
//    private static final String outFilePath = "/Users/frbernad/PROGRAMMING/ITBA/SS/TPs/TP2/results/timePlot.txt";
//
//
//    public static void main(String[] args) throws IOException {
//        LOGGER.info("Time Plot Generator Starting ...");
//
//        final ParticlesParserResult particlesParserResult = parseParticlesList(
//            Paths.get(staticFilePath).toFile(),
//            Paths.get(dynamicFilePath).toFile(),
//            " "
//        );
//
//        final Map<Particle, State> particles = particlesParserResult.getParticlesPerTime().get(0);
//
//        final double maxRadius = particlesParserResult.getParticlesPerTime()
//            .get(0)
//            .keySet()
//            .stream()
//            .map(Particle::getRadius)
//            .max(Double::compare).orElse(0.0);
//
//        final int L = 20;
//        final int N = particles.keySet().size();
//        final double R = 1;
//
//
//        final int maxM = (int) (L / (R + 2 * maxRadius));
//        System.out.printf("Max M casted: %s\nMax M not casted: %f\n", maxM, (L / (R + 2 * maxRadius)));
//
//        final Map<Integer, List<Long>> times = new HashMap<>();
//
//        for (int M = 1; M <= maxM; M++) {
//            System.out.printf("Calculating for M = %d: ", M);
//            times.put(M, new ArrayList<>());
//            for (int n = 0; n < 150; n++) {
//                System.out.printf("%d ", n + 1);
//                final long time = calculateNeighbors(particles, N, L, M, R, false)
//                    .getExecutionTimestamps()
//                    .getAlgorithmTotalTime()
//                    .toNanoOfDay();
//
//                times.get(M).add(time);
//            }
//            System.out.println();
//        }
//
//        try (PrintWriter pw = new PrintWriter(outFilePath)) {
//            times.forEach((m, ts) -> {
//                pw.printf("%d", m);
//                ts.forEach(t -> pw.printf(" %d", t));
//                pw.printf("\n");
//            });
//        }
//
//        LOGGER.info("Finished!");
//
//    }
//
//}
