package ar.edu.itba.ss.simulator.algorithms.flocks;

import ar.edu.itba.ss.simulator.methods.cellIndex.CellIndexMethod;
import ar.edu.itba.ss.simulator.utils.ExecutionTimestamps;
import ar.edu.itba.ss.simulator.utils.Particle;

import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static ar.edu.itba.ss.simulator.utils.Particle.Position;
import static ar.edu.itba.ss.simulator.utils.Particle.State;
import static java.lang.Math.*;

public class Flocks {

    public static FlocksAlgorithmResults execute(final Map<Particle, State> initialParticlesStates,
                                                 final int N,
                                                 final int L,
                                                 final int M,
                                                 final double R,
                                                 final double dt,
                                                 final double eta,
                                                 final boolean periodic,
                                                 final int maxIterations) {

        final List<Map<Particle, State>> particlesStates = new ArrayList<>(List.of(initialParticlesStates));
        double currentOrderParameter;

        final ExecutionTimestamps executionTimestamps = new ExecutionTimestamps();
        executionTimestamps.setAlgorithmStart(LocalDateTime.now());

        final List<Double> orderParameters = new ArrayList<>();
        int iterations = 0;
        while (iterations < maxIterations) {
            final Map<Particle, State> currentParticlesStates = particlesStates.get(particlesStates.size() - 1);

            //Update order parameter
            currentOrderParameter = generateOrderParameter(currentParticlesStates.values());
            orderParameters.add(currentOrderParameter);

            //Update order parameter
            final Map<Particle, Position> currentParticlesPositions = currentParticlesStates.entrySet()
                .stream()
                .collect(Collectors.toMap(Entry::getKey, e -> e.getValue().getPosition()));

            final Map<Particle, Set<Particle>> currentNeighbors = CellIndexMethod.calculateNeighbors(currentParticlesPositions, N, L, M, R, periodic).getNeighbors();

            final Map<Particle, State> nextParticlesState = new HashMap<>();

            currentNeighbors.forEach((particle, neighbors) ->
                nextParticlesState.put(particle, getNextState(particle, neighbors, currentParticlesStates, dt, eta, L))
            );

            particlesStates.add(nextParticlesState);
            iterations++;
        }

        executionTimestamps.setAlgorithmEnd(LocalDateTime.now());

        return new FlocksAlgorithmResults(executionTimestamps, particlesStates, orderParameters);
    }

    private static State getNextState(final Particle currentParticle, final Set<Particle> neighbors,
                                      final Map<Particle, State> particlesState,
                                      final double dt, final double eta, final int L) {

        final List<State> neighborsStates = neighbors.stream().map(particlesState::get).collect(Collectors.toList());
        final State currentParticleState = particlesState.get(currentParticle);

        double nextX = (currentParticleState.getPosition().getX() + currentParticleState.getXVelocity() * dt) % L;
        if (nextX < 0) {
            nextX += L;
        }
        double nextY = (currentParticleState.getPosition().getY() + currentParticleState.getYVelocity() * dt) % L;
        if (nextY < 0) {
            nextY += L;
        }

        final List<State> surroundingParticlesStates = new ArrayList<>(neighborsStates);
        surroundingParticlesStates.add(currentParticleState);

        final Position nextPosition = new Position(nextX, nextY);

        final double avgCos = surroundingParticlesStates.stream().mapToDouble(p -> cos(p.getAngle())).average().orElseThrow(RuntimeException::new);
        final double avgSin = surroundingParticlesStates.stream().mapToDouble(p -> sin(p.getAngle())).average().orElseThrow(RuntimeException::new);

        final double nextAngle = atan2(avgSin, avgCos) + generateNoise(eta);

        return new State(nextPosition, currentParticleState.getSpeed(), nextAngle);
    }

    private static double generateNoise(final double eta) {
        final Random random = new Random();
        return -eta / 2 + eta * random.nextDouble();
    }

    private static double generateOrderParameter(final Collection<State> particlesStates) {
        final double speed = particlesStates.stream().findFirst().orElseThrow().getSpeed();

        final double vx = particlesStates.stream().mapToDouble(State::getXVelocity).sum();
        final double vy = particlesStates.stream().mapToDouble(State::getYVelocity).sum();

        final double norm = sqrt(pow(vx, 2) + pow(vy, 2));

        return norm / (particlesStates.size() * speed);
    }


}
