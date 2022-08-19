package ar.edu.itba.ss.simulator.algorithms.flocks;

import ar.edu.itba.ss.simulator.utils.Particle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

import static ar.edu.itba.ss.simulator.utils.Particle.*;
import static java.lang.Math.*;

public class Flocks {

    public static FlocksAlgorithmResults execute(
        final Map<Particle, State> particles,
        final int N,
        final int L,
        final int M,
        final double R,
        final double dt,
        final double eta,
        final double threshold,
        final boolean periodic
    ) {

        return new FlocksAlgorithmResults();
    }

    private static State getNextState(final State currentParticle,
                                      final List<State> neighbors,
                                      final double dt,
                                      final double eta) {

        final double nextX = currentParticle.getPosition().getX() + currentParticle.getXVelocity() * dt;
        final double nextY = currentParticle.getPosition().getY() + currentParticle.getYVelocity() * dt;

        final List<State> surroundingParticles = new ArrayList<>(neighbors);
        surroundingParticles.add(currentParticle);

        final Position nextPosition = new Position(nextX, nextY);

        final double avgCos = surroundingParticles.stream().mapToDouble(p -> cos(p.getAngle())).average().orElseThrow(RuntimeException::new);
        final double avgSin = surroundingParticles.stream().mapToDouble(p -> sin(p.getAngle())).average().orElseThrow(RuntimeException::new);

        //FIXME: QUE ONDA EL ATAN2 QUE RECIBE 2 COSAS
        final double nextAngle = atan2(avgSin, avgCos) + generateNoise(eta);

        return new State(nextPosition, currentParticle.getSpeed(), nextAngle);
    }

    private static double generateNoise(final double eta) {
        final Random random = new Random();
        return -eta / 2 + eta * random.nextDouble();
    }


}
