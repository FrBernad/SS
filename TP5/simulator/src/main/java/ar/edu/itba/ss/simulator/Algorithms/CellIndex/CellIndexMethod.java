package ar.edu.itba.ss.simulator.Algorithms.CellIndex;

import ar.edu.itba.ss.simulator.utils.Pair;
import ar.edu.itba.ss.simulator.utils.Particle;
import ar.edu.itba.ss.simulator.utils.R;

import java.util.*;

import static ar.edu.itba.ss.simulator.utils.R.values.R0;
import static java.lang.Math.hypot;

public class CellIndexMethod {

    public static Map<Particle, Set<Particle>> calculateNeighbors(final Map<Particle, R> particles,
                                                                  final Grid grid, final double R) {

        final List<List<Cell>> cells = grid.getGrid();

        grid.clear();
        grid.fillGrid(particles);

        final Map<Particle, Set<Particle>> neighbors = new HashMap<>();
        particles.forEach((key, value) -> neighbors.put(key, new HashSet<>()));

        for (int y = 0; y < grid.getM(); y++) {
            for (int x = 0; x < grid.getN(); x++) {
                final Cell currentCell = cells.get(y).get(x);
                if (!currentCell.isEmpty()) {
                    for (Particle p : currentCell.getParticles()) {
                        checkNeighbors(p, particles, currentCell, neighbors, R);
                    }
                }
            }
        }

        return neighbors;
    }

    private static void checkNeighbors(final Particle particle, final Map<Particle, R> particles,
                                       final Cell cell, final Map<Particle, Set<Particle>> neighbors,
                                       final double R) {

        final Optional<Cell> topCell = Optional.ofNullable(cell.getTopCell());
        final Optional<Cell> rightCell = Optional.ofNullable(cell.getRightCell());
        final Optional<Cell> topRightCell = Optional.ofNullable(cell.getTopRightCell());
        final Optional<Cell> bottomRightCell = Optional.ofNullable(cell.getBottomRightCell());

        final List<Optional<Cell>> surroundingCells = List.of(topCell, rightCell, topRightCell, bottomRightCell);

        final Pair particlePosition = particles.get(particle).get(R0.ordinal());

        for (Particle otherParticle : cell.getParticles()) {
            Pair otherParticlePosition = particles.get(otherParticle).get(R0.ordinal());

            if (!particle.equals(otherParticle)) {
                addIfInRadius(particle, particlePosition, otherParticle, otherParticlePosition, neighbors, R);
            }
        }

        surroundingCells.stream().filter(c -> c.isPresent() && !c.get().isEmpty()).forEach(c -> {
            for (Particle otherParticle : c.get().getParticles()) {
                Pair otherParticlePosition = particles.get(otherParticle).get(R0.ordinal());
                addIfInRadius(particle, particlePosition, otherParticle, otherParticlePosition, neighbors, R);
            }
        });
    }


    private static void addIfInRadius(final Particle particle, final Pair particlePosition,
                                      final Particle otherParticle, final Pair otherParticlePosition,
                                      final Map<Particle, Set<Particle>> neighbors, final double R) {
        final double deltaX = particlePosition.getX() - otherParticlePosition.getX();
        final double deltaY = particlePosition.getY() - otherParticlePosition.getY();
        final double distanceBetween = hypot(deltaX, deltaY) - particle.getRadius() - otherParticle.getRadius();
        if (distanceBetween <= R) {
            neighbors.get(particle).add(otherParticle);
            neighbors.get(otherParticle).add(particle);
        }
    }

}



