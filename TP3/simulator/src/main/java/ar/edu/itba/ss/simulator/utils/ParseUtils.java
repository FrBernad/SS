package ar.edu.itba.ss.simulator.utils;

import ar.edu.itba.ss.simulator.utils.Particle.Position;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static ar.edu.itba.ss.simulator.utils.Particle.State;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class ParseUtils {

    public static ParticlesParserResult parseParticlesList(final File staticFile, final File dynamicFile, final String delimiter) throws IOException {
        final Scanner staticScanner = new Scanner(staticFile);
        final Scanner dynamicScanner = new Scanner(dynamicFile);

        final int N = parseInt(staticScanner.nextLine().split(delimiter)[0]);
        final int L = parseInt(staticScanner.nextLine().split(delimiter)[0]);

        final List<Map<Particle, State>> particlesPerTime = new ArrayList<>();

        final List<Particle> particles = new ArrayList<>();

        while (staticScanner.hasNextLine()) {
            final List<String> staticArray = Arrays.asList(staticScanner.nextLine().split(delimiter));

            particles.add(new Particle(particles.size() + 1, parseDouble(staticArray.get(StaticFields.RADIUS.getValue())), parseDouble(staticArray.get(StaticFields.MASS.getValue()))));
        }

        staticScanner.close();

        int particleIndex = 0;
        int timeIndex = -1;
        while (dynamicScanner.hasNextLine()) {

            List<String> dynamicArray = Arrays.asList(dynamicScanner.nextLine().split(delimiter));
            if (dynamicArray.size() == 1) {
                particleIndex = 0;
                timeIndex++;

                dynamicArray = Arrays.asList(dynamicScanner.nextLine().split(delimiter));

                particlesPerTime.add(new HashMap<>());
            }

            final Map<Particle, State> currentParticlesPerTime = particlesPerTime.get(timeIndex);

            final Particle currentParticle = particles.get(particleIndex);

            final State currentParticleState = new State(
                new Position(
                    parseDouble(dynamicArray.get(DynamicFields.X.getValue())),
                    parseDouble(dynamicArray.get(DynamicFields.Y.getValue()))
                ),
                parseDouble(dynamicArray.get(DynamicFields.SPEED.getValue())),
                parseDouble(dynamicArray.get(DynamicFields.ANGLE.getValue()))
            );

            currentParticlesPerTime.put(currentParticle, currentParticleState);

            particleIndex++;
        }

        dynamicScanner.close();

        return new ParticlesParserResult(N, L, particlesPerTime);
    }

    public static class ParticlesParserResult {
        private final List<Map<Particle, State>> particlesPerTime;
        private final int N;
        private final int L;

        public ParticlesParserResult(int N, int L, List<Map<Particle, State>> particlesPerTime) {
            this.N = N;
            this.L = L;
            this.particlesPerTime = particlesPerTime;
        }

        public int getN() {
            return N;
        }

        public int getL() {
            return L;
        }

        public List<Map<Particle, State>> getParticlesPerTime() {
            return particlesPerTime;
        }
    }

    public enum StaticFields {
        RADIUS(0), MASS(1);

        private final int value;

        StaticFields(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum DynamicFields {
        X(0), Y(1), SPEED(2), ANGLE(3);

        private final int value;

        DynamicFields(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}

