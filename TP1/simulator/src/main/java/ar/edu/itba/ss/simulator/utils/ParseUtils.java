package ar.edu.itba.ss.simulator.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.Double.*;
import static java.lang.Integer.*;

public class ParseUtils {

    public static ParticlesParserResult parseParticlesList(final File staticFile, final File dynamicFile) throws IOException {
        final Scanner staticScanner = new Scanner(staticFile);
        final Scanner dynamicScanner = new Scanner(dynamicFile);

        //Skip first line
        dynamicScanner.nextLine();

        final int N = parseInt(staticScanner.nextLine());
        final int L = parseInt(staticScanner.nextLine());

        final List<Particle> particles = new ArrayList<>();

        while (staticScanner.hasNextLine() && dynamicScanner.hasNextLine()) {
            final String[] staticArray = staticScanner.nextLine().split(" ");
            final String[] dynamicArray = dynamicScanner.nextLine().split(" ");

            particles.add(
                new Particle(particles.size() + 1,
                    parseDouble(dynamicArray[DynamicFields.X.getValue()]),
                    parseDouble(dynamicArray[DynamicFields.Y.getValue()]),
                    parseDouble(staticArray[StaticFields.RADIUS.getValue()]),
                    parseDouble(staticArray[StaticFields.PROPERTY.getValue()]))
            );

        }

        return new ParticlesParserResult(N, L, particles);
    }

    public static class ParticlesParserResult {
        private final List<Particle> particles;
        private final int N;
        private final int L;

        public ParticlesParserResult(int N, int L, List<Particle> particles) {
            this.N = N;
            this.L = L;
            this.particles = particles;
        }

        public int getN() {
            return N;
        }

        public int getL() {
            return L;
        }

        public List<Particle> getParticles() {
            return particles;
        }
    }

    public enum StaticFields {
        RADIUS(0),
        PROPERTY(1);

        private final int value;

        StaticFields(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum DynamicFields {
        X(0),
        Y(1);

        private final int value;

        DynamicFields(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}

