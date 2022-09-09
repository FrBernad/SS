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

        final List<Map<Particle, State>> particlesStates = new ArrayList<>(List.of(initialParticlesStates));

        final ExecutionTimestamps executionTimestamps = new ExecutionTimestamps();
        executionTimestamps.setAlgorithmStart(LocalDateTime.now());

        final TreeSet<Collision> collisions = new TreeSet<>();
        //Set que almacena todas las partículas que tienen una colisión asociada
        final Set<Particle> collisionParticles = new HashSet<>();

        for (int i = 0; i < maxIterations; i++) {
            final Map<Particle, State> currentStates = particlesStates.get(i);

            currentStates.forEach((particle, state) -> {
                if (!collisionParticles.contains(particle)) {

                    Collision collision = getClosestCollision(particle, state, currentStates, L);

                    if (collision.getType() != CollisionType.NONE) {

                        if (collision.getParticleI() != null) {
                            collisionParticles.add(collision.getParticleI());
                        }
                        if (collision.getParticleJ() != null) {
                            collisionParticles.add(collision.getParticleJ());
                        }

                        collisions.add(collision);
                    }
                }
            });

            final Collision closestCollision = collisions.pollFirst();

            particlesStates.add(updateParticlesStates(closestCollision, collisions, collisionParticles, currentStates));
        }

        executionTimestamps.setAlgorithmEnd(LocalDateTime.now());

        return new BrownianMotionAlgorithmResults(executionTimestamps, particlesStates);
    }

}
