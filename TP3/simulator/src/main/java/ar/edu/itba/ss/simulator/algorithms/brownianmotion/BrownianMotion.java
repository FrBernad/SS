package ar.edu.itba.ss.simulator.algorithms.brownianmotion;

import ar.edu.itba.ss.simulator.algorithms.brownianmotion.Collision.CollisionType;
import ar.edu.itba.ss.simulator.utils.BrownianMotionAlgorithmResults;
import ar.edu.itba.ss.simulator.utils.ExecutionTimestamps;
import ar.edu.itba.ss.simulator.utils.Particle;

import java.time.LocalDateTime;
import java.util.*;

import static ar.edu.itba.ss.simulator.algorithms.brownianmotion.BrownianMotionUtils.getClosestCollision;
import static ar.edu.itba.ss.simulator.algorithms.brownianmotion.BrownianMotionUtils.updateParticlesStates;
import static ar.edu.itba.ss.simulator.utils.Particle.State;

public class BrownianMotion {


    public static BrownianMotionAlgorithmResults execute(final Map<Particle, State> initialParticlesStates, int L, int maxIterations) {

        final List<Map<Particle, State>> particlesStates = new LinkedList<>(List.of(initialParticlesStates));

        final ExecutionTimestamps executionTimestamps = new ExecutionTimestamps();
        executionTimestamps.setAlgorithmStart(LocalDateTime.now());

        // Particles with an associated collision
        final Set<Particle> particlesWithCollision = new HashSet<>();

        // Particles collisions ordered by collision time
        final TreeSet<Collision> closestCollisions = new TreeSet<>();

        for (int i = 0; i < maxIterations; i++) {
            final Map<Particle, State> currentStates = particlesStates.get(i);

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

            // Evolve and update particles states
            particlesStates.add(updateParticlesStates(closestCollision, particlesWithCollision, closestCollisions, currentStates));
        }

        executionTimestamps.setAlgorithmEnd(LocalDateTime.now());

        return new BrownianMotionAlgorithmResults(executionTimestamps, particlesStates);
    }

}
