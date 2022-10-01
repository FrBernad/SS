package ar.edu.itba.ss.simulator.utils;

import ar.edu.itba.ss.simulator.utils.Particle.Position;

import java.io.File;
import java.io.FileNotFoundException;
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
                parseDouble(dynamicArray.get(DynamicFields.VELOCITY_X.getValue())),
                parseDouble(dynamicArray.get(DynamicFields.VELOCITY_Y.getValue()))
            );

            currentParticlesPerTime.put(currentParticle, currentParticleState);

            particleIndex++;
        }

        dynamicScanner.close();

        return new ParticlesParserResult(N, L, particlesPerTime);
    }

    public static void parsePlanets(final File earthFile, final File venusFile, final File sunFile) throws FileNotFoundException {

        final Scanner earthScanner = new Scanner(earthFile);
        final Scanner venusScanner = new Scanner(venusFile);
        final Scanner sunScanner = new Scanner(sunFile);

        final Map<String, List<String>> planetInfo = new HashMap<>();
        final String earthName = earthFile.getName().split("_")[0];
        final String venusName = venusFile.getName().split("_")[0];
        final String sunName = sunFile.getName().split("_")[0];
        planetInfo.putIfAbsent(earthName, new ArrayList<>());
        planetInfo.putIfAbsent(venusName, new ArrayList<>());
        planetInfo.putIfAbsent(sunName, new ArrayList<>());


        while (earthScanner.hasNextLine()) {
            final List<String> earthInfo = planetInfo.get(earthName);
            final String line = earthScanner.nextLine();
            if (line.toLowerCase().contains("vol. mean radius (km)")) {
                String[] parts = line.split("=");
                earthInfo.add(parts[1].trim().split(" ")[0].split("\\+")[0]);
                earthInfo.add(parts[2].trim().split("\\+")[0] + parts[1].trim().split(" ")[4].replace("x10^", "E+"));
            }

            if (line.toLowerCase().contains("x =")) {
                String[] positions = line.split("=");
                earthInfo.add(positions[1].trim().split(" ")[0]);
                earthInfo.add(positions[2].trim().split(" ")[0]);
            }

            if (line.toLowerCase().contains("vx=")) {
                String[] velocities = line.split("=");
                earthInfo.add(velocities[1].trim().split(" ")[0]);
                earthInfo.add(velocities[2].trim().split(" ")[0]);
                break;
            }


        }
        earthScanner.close();

        boolean foundMass = false;

        while (venusScanner.hasNextLine()) {

            final List<String> venusInfo = planetInfo.get(venusName);
            final String line = venusScanner.nextLine();
            if (line.toLowerCase().contains("vol. mean radius (km)")) {
                venusInfo.add(line.split("=")[1].trim().split(" ")[0].split("\\+")[0]);

            }

            if (line.toLowerCase().contains("mass") && !foundMass) {
                String[] massParts = line.split("=");
                venusInfo.add(massParts[1].trim().split(" ")[0] + massParts[0].trim().split(" ")[1].replace("x10^", "E+"));
                foundMass = true;
            }

            if (line.toLowerCase().contains("x =")) {
                String[] positions = line.split("=");
                venusInfo.add(positions[1].trim().split(" ")[0]);
                venusInfo.add(positions[2].trim().split(" ")[0]);
            }

            if (line.toLowerCase().contains("vx=")) {
                String[] velocities = line.split("=");
                venusInfo.add(velocities[1].trim().split(" ")[0]);
                venusInfo.add(velocities[2].trim().split(" ")[0]);
                break;
            }
        }

        venusScanner.close();

        foundMass = false;

        while (sunScanner.hasNextLine()) {

            final List<String> sunInfo = planetInfo.get(sunName);
            final String line = sunScanner.nextLine();
            if (line.toLowerCase().contains("vol. mean radius, km")) {
                sunInfo.add(line.split("=")[1].trim().split(" ")[0].replace("10^", "E+"));

            }

            if (line.toLowerCase().contains("mass") && !foundMass) {
                String[] massParts = line.split("=");
                sunInfo.add(massParts[2].trim().split("~")[1] + massParts[1].split(",")[1].trim().split(" ")[0]);
                foundMass = true;
            }

            if (line.toLowerCase().contains("x =")) {
                String[] positions = line.split("=");
                sunInfo.add(positions[1].trim().split(" ")[0]);
                sunInfo.add(positions[2].trim().split(" ")[0]);
            }

            if (line.toLowerCase().contains("vx=")) {
                String[] velocities = line.split("=");
                sunInfo.add(velocities[1].trim().split(" ")[0]);
                sunInfo.add(velocities[2].trim().split(" ")[0]);
                break;
            }
        }

        sunScanner.close();

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
        X(0), Y(1), VELOCITY_X(2), VELOCITY_Y(3);

        private final int value;

        DynamicFields(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}

