package ar.edu.itba.ss.simulator.utils;

import ar.edu.itba.ss.simulator.utils.Particle.Position;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static ar.edu.itba.ss.simulator.utils.Particle.State;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.Math.*;

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

    public static void parsePlanets(final String staticDir, final String dynamicDir, final File earthFile, final File venusFile, final File sunFile, final double distanceToSpaceship) throws FileNotFoundException {

        final Scanner earthScanner = new Scanner(earthFile);
        final Scanner venusScanner = new Scanner(venusFile);
        final Scanner sunScanner = new Scanner(sunFile);

        final List<String> planetRadius = new ArrayList<>();
        final List<String> planetMass = new ArrayList<>();

        final Map<String, Map<String, List<String>>> planetPositions = new HashMap<>();
        final Map<String, Map<String, List<String>>> planetVelocities = new HashMap<>();

        final String earthName = earthFile.getName().split("_")[0];
        final String venusName = venusFile.getName().split("_")[0];
        final String sunName = sunFile.getName().split("_")[0];

        boolean foundMass = false;


        while (sunScanner.hasNextLine()) {

            String line = sunScanner.nextLine();
            if (line.toLowerCase().contains("vol. mean radius, km")) {
                planetRadius.add(line.split("=")[1].trim().split(" ")[0]);

            }

            if (line.toLowerCase().contains("mass") && !foundMass) {
                String[] massParts = line.split("=");
                planetMass.add(massParts[2].trim().split("~")[1] + massParts[1].split(",")[1].trim().split(" ")[0].replace("10^", "E+"));
                foundMass = true;
            }

            if (line.toLowerCase().contains("$$soe")) {

                String date = "";

                int i = 0;
                while (true) {
                    line = sunScanner.nextLine();
                    if (line.toLowerCase().contains("$$eoe")) {
                        break;
                    }

                    if (i == 0) {
                        String[] parts = line.split(" ");
                        date = parts[3].trim() + "_" + parts[4].trim();
                        planetPositions.putIfAbsent(date, new HashMap<>());
                        planetVelocities.putIfAbsent(date, new HashMap<>());
                    } else if (i == 1) {
                        String[] parts = line.split("=");
                        final Map<String, List<String>> sunPositions = planetPositions.get(date);
                        sunPositions.putIfAbsent(sunName, new ArrayList<>());
                        final List<String> positions = sunPositions.get(sunName);
                        positions.add(parts[1].trim().split(" ")[0]);
                        positions.add(parts[2].trim().split(" ")[0]);
                    }
                    if (i == 2) {
                        String[] parts = line.split("=");
                        final Map<String, List<String>> sunVelocities = planetVelocities.get(date);
                        sunVelocities.putIfAbsent(sunName, new ArrayList<>());
                        final List<String> velocities = sunVelocities.get(sunName);
                        velocities.add(parts[1].trim().split(" ")[0]);
                        velocities.add(parts[2].trim().split(" ")[0]);
                    }
                    i++;
                    i = i % 3;
                }
            }
        }

        sunScanner.close();


        while (earthScanner.hasNextLine()) {
            String line = earthScanner.nextLine();
            if (line.toLowerCase().contains("vol. mean radius (km)")) {
                String[] parts = line.split("=");
                planetRadius.add(parts[1].trim().split(" ")[0].split("\\+")[0]);
                planetMass.add(parts[2].trim().split("\\+")[0] + parts[1].trim().split(" ")[4].replace("x10^", "E+"));
            }

            if (line.toLowerCase().contains("$$soe")) {

                String date = "";

                int i = 0;
                while (true) {
                    line = earthScanner.nextLine();
                    if (line.toLowerCase().contains("$$eoe")) {
                        break;
                    }

                    if (i == 0) {
                        String[] parts = line.split(" ");
                        date = parts[3].trim() + "_" + parts[4].trim();
                        planetPositions.putIfAbsent(date, new HashMap<>());
                        planetVelocities.putIfAbsent(date, new HashMap<>());
                    } else if (i == 1) {
                        String[] parts = line.split("=");
                        final Map<String, List<String>> earthPositions = planetPositions.get(date);
                        earthPositions.putIfAbsent(earthName, new ArrayList<>());
                        final List<String> positions = earthPositions.get(earthName);
                        positions.add(parts[1].trim().split(" ")[0]);
                        positions.add(parts[2].trim().split(" ")[0]);
                    }
                    if (i == 2) {
                        String[] parts = line.split("=");
                        final Map<String, List<String>> sunVelocities = planetVelocities.get(date);
                        sunVelocities.putIfAbsent(earthName, new ArrayList<>());
                        final List<String> velocities = sunVelocities.get(earthName);
                        velocities.add(parts[1].trim().split(" ")[0]);
                        velocities.add(parts[2].trim().split(" ")[0]);
                    }
                    i++;
                    i = i % 3;
                }
            }
        }
        earthScanner.close();

        foundMass = false;

        while (venusScanner.hasNextLine()) {
            String line = venusScanner.nextLine();

            if (line.toLowerCase().contains("vol. mean radius (km)")) {
                planetRadius.add(line.split("=")[1].trim().split(" ")[0].split("\\+")[0]);
            }

            if (line.toLowerCase().contains("mass") && !foundMass) {
                String[] massParts = line.split("=");
                planetMass.add(massParts[1].trim().split(" ")[0] + massParts[0].trim().split(" ")[1].replace("x10^", "E+"));
                foundMass = true;
            }

            if (line.toLowerCase().contains("$$soe")) {

                String date = "";

                int i = 0;
                while (true) {
                    line = venusScanner.nextLine();
                    if (line.toLowerCase().contains("$$eoe")) {
                        break;
                    }

                    if (i == 0) {
                        String[] parts = line.split(" ");
                        date = parts[3].trim() + "_" + parts[4].trim();
                        planetPositions.putIfAbsent(date, new HashMap<>());
                        planetVelocities.putIfAbsent(date, new HashMap<>());
                    } else if (i == 1) {
                        String[] parts = line.split("=");
                        final Map<String, List<String>> venusPositions = planetPositions.get(date);
                        venusPositions.putIfAbsent(venusName, new ArrayList<>());
                        final List<String> positions = venusPositions.get("venus");
                        positions.add(parts[1].trim().split(" ")[0]);
                        positions.add(parts[2].trim().split(" ")[0]);
                    }
                    if (i == 2) {
                        String[] parts = line.split("=");
                        final Map<String, List<String>> sunVelocities = planetVelocities.get(date);
                        sunVelocities.putIfAbsent(venusName, new ArrayList<>());
                        final List<String> velocities = sunVelocities.get(venusName);
                        velocities.add(parts[1].trim().split(" ")[0]);
                        velocities.add(parts[2].trim().split(" ")[0]);
                    }
                    i++;
                    i = i % 3;
                }
            }
        }

        venusScanner.close();

        final String staticFilePath = String.format("%s/StaticPlanets", staticDir);
        final File staticFile = new File(staticFilePath);
        try (PrintWriter pw = new PrintWriter(staticFile)) {
            pw.append("4\n");
            pw.append("0\n");
            pw.append(String.format("%s %s\n", planetRadius.get(0), planetMass.get(0)));
            pw.append(String.format("%s %s\n", planetRadius.get(1), planetMass.get(1)));
            pw.append(String.format("%s %s\n", planetRadius.get(2), planetMass.get(2)));
            pw.append("1 2E+5");
        }

        for (String date : planetPositions.keySet()) {
            final String dynamicFilePath = String.format("%s/DynamicPlanets_%s", dynamicDir, date);
            final File outFile = new File(dynamicFilePath);
            try (PrintWriter pw = new PrintWriter(outFile)) {
                pw.append("0\n");
                List<String> sunPosition = planetPositions.get(date).get(sunName);
                List<String> sunVelocities = planetVelocities.get(date).get(sunName);
                pw.append(String.format("%s %s ", sunPosition.get(0), sunPosition.get(1)));
                pw.append(String.format("%s %s\n", sunVelocities.get(0), sunVelocities.get(1)));
                List<String> earthPosition = planetPositions.get(date).get(earthName);
                List<String> earthVelocities = planetVelocities.get(date).get(earthName);
                pw.append(String.format("%s %s ", earthPosition.get(0), earthPosition.get(1)));
                pw.append(String.format("%s %s\n", earthVelocities.get(0), earthVelocities.get(1)));
                List<String> venusPosition = planetPositions.get(date).get(venusName);
                List<String> venusVelocities = planetVelocities.get(date).get(venusName);
                pw.append(String.format("%s %s ", venusPosition.get(0), venusPosition.get(1)));
                pw.append(String.format("%s %s\n", venusVelocities.get(0), venusVelocities.get(1)));

                double sunx = parseDouble(sunPosition.get(0));
                double suny = parseDouble(sunPosition.get(1));
                double earthx = parseDouble(earthPosition.get(0));
                double earthy = parseDouble(earthPosition.get(1));
                double earthvx = parseDouble(earthVelocities.get(0));
                double earthvy = parseDouble(earthVelocities.get(1));
                double earthR = parseDouble(planetRadius.get(1));

                //https://math.stackexchange.com/questions/2045174/how-to-find-a-point-between-two-points-with-given-distance
                double d = sqrt((pow((earthx - sunx), 2) + pow((earthy - suny), 2)));
                // Componentes del versor que une el sol con la tierra (normal a la orbita)
                double rx = (earthx - sunx) / d;
                double ry = (earthy - suny) / d;

                // Componentes del versor tangencial a la orbita
                double ox = -ry;
                double oy = rx;

                //Position
                double spaceshipx = distanceToSpaceship * -rx + earthx + earthR;
                double spaceshipy = distanceToSpaceship * -ry + earthy + earthR;

                //Velocity
                double vt = -7.12 - 8 + earthvx * ox + earthvy * oy;
                double spaceshipvx = ox * vt;
                double spaceshipvy = oy * vt;

                pw.append(String.format("%1.20E %1.20E ", spaceshipx, spaceshipy));
                pw.append(String.format("%1.20E %1.20E\n", spaceshipvx, spaceshipvy));
            }
        }
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

