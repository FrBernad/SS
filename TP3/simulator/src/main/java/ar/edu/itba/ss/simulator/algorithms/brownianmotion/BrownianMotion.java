package ar.edu.itba.ss.simulator.algorithms.brownianmotion;

import ar.edu.itba.ss.simulator.algorithms.brownianmotion.Collision.CollisionType;
import ar.edu.itba.ss.simulator.utils.BrownianMotionAlgorithmResults;
import ar.edu.itba.ss.simulator.utils.ExecutionTimestamps;
import ar.edu.itba.ss.simulator.utils.Particle;
import ar.edu.itba.ss.simulator.utils.Particle.Position;

import java.time.LocalDateTime;
import java.util.*;

import static ar.edu.itba.ss.simulator.algorithms.brownianmotion.BrownianMotionUtils.getClosestCollision;
import static ar.edu.itba.ss.simulator.algorithms.brownianmotion.BrownianMotionUtils.updateParticlesStates;
import static ar.edu.itba.ss.simulator.utils.Particle.State;

public class BrownianMotion {


    public static BrownianMotionAlgorithmResults execute(final Map<Particle, State> initialParticlesStates, int L, int maxIterations) {

        final Map<Double, Map<Particle, State>> particlesStates = new TreeMap<>(Map.of(0.0, initialParticlesStates));

        final ExecutionTimestamps executionTimestamps = new ExecutionTimestamps();
        executionTimestamps.setAlgorithmStart(LocalDateTime.now());

        // Particles with an associated collision
        final Set<Particle> particlesWithCollision = new HashSet<>();

        // Particles collisions ordered by collision time
        final TreeSet<Collision> closestCollisions = new TreeSet<>();

        boolean bigParticleTouchedBorder = false;
        final Particle bigParticle = initialParticlesStates.keySet().stream().max(Comparator.comparingDouble(Particle::getRadius)).orElseThrow();

        int iteration;
        double simulationTime = 0;
        for (iteration = 0; iteration < maxIterations && !bigParticleTouchedBorder; iteration++) {
            final Map<Particle, State> currentStates = particlesStates.get(simulationTime);

            // Calculate collisions
            for (Map.Entry<Particle, State> entry : currentStates.entrySet()) {

                Particle particle = entry.getKey();
                State state = entry.getValue();

                // Calculate collision only for particle without associated collision
                if (!particlesWithCollision.contains(particle)) {
                    Collision collision = getClosestCollision(particle, state, currentStates, L);

                    if (collision.getType() != CollisionType.NONE) {
                        closestCollisions.add(collision);
                        particlesWithCollision.add(collision.getParticleI());
                    }
                }
            }

            final Collision closestCollision = closestCollisions.pollFirst();
            if (closestCollision == null) {
                throw new RuntimeException("Missing closest collision");
            }

            // Evolve and update particles states
            final Map<Particle, State> newState = updateParticlesStates(closestCollision, particlesWithCollision, closestCollisions, currentStates);

            simulationTime += closestCollision.getCollisionTime();

            particlesStates.put(simulationTime, newState);

            bigParticleTouchedBorder = checkBigParticlePosition(newState.get(bigParticle).getPosition(), bigParticle.getRadius(), L);
        }

        executionTimestamps.setAlgorithmEnd(LocalDateTime.now());

        return new BrownianMotionAlgorithmResults(executionTimestamps, particlesStates, iteration, simulationTime);
    }

    private static boolean checkBigParticlePosition(Position position, double radius, int L) {
        return position.getX() <= radius || position.getX() >= L - radius || position.getY() <= radius || position.getY() >= L - radius;
    }

}
