package ar.edu.itba.ss.simulator.methods.cellIndex;

import ar.edu.itba.ss.simulator.utils.ExecutionTimestamps;
import ar.edu.itba.ss.simulator.utils.Particle;

import java.time.LocalDateTime;
import java.util.*;

import static ar.edu.itba.ss.simulator.utils.Particle.Position;
import static ar.edu.itba.ss.simulator.utils.Particle.Position.calculateDistance;

public class CellIndexMethod {

    public static CellIndexMethodResults calculateNeighbors(final Map<Particle, Position> particles,
                                                            final int N,
                                                            final int L,
                                                            final int M,
                                                            final double R,
                                                            final Boolean periodic) {

        final Grid grid = new Grid(L, M, particles, periodic);

        final ExecutionTimestamps executionTimestamps = new ExecutionTimestamps();
        executionTimestamps.setAlgorithmStart(LocalDateTime.now());

        final List<List<Cell>> cells = grid.getGrid();

        final Map<Particle, Set<Particle>> neighbors = new HashMap<>();
        particles.forEach((key, value) -> neighbors.put(key, new HashSet<>()));

        for (int y = 0; y < M; y++) {
            for (int x = 0; x < M; x++) {
                final Cell currentCell = cells.get(y).get(x);
                if (!currentCell.isEmpty()) {
                    for (Particle p : currentCell.getParticles()) {
                        checkNeighbors(p, particles, currentCell, neighbors, R, L, M, periodic);
                    }
                }
            }
        }

        executionTimestamps.setAlgorithmEnd(LocalDateTime.now());

        return new CellIndexMethodResults(neighbors, executionTimestamps);
    }

    private static void checkNeighbors(final Particle particle, final Map<Particle, Position> particles,
                                       final Cell cell, final Map<Particle, Set<Particle>> neighbors,
                                       final double R, final int L, final int M, final boolean periodic) {
        final Cell topCell = cell.getTopCell();
        final Cell rightCell = cell.getRightCell();
        final Cell topRightCell = cell.getTopRightCell();
        final Cell bottomRightCell = cell.getBottomRightCell();

        final Position particlePosition = particles.get(particle);

        for (Particle otherParticle : cell.getParticles()) {
            Position otherParticlePosition = particles.get(otherParticle);
            if (!particle.equals(otherParticle)) {

                addIfInRadius(particle, particlePosition, otherParticle, otherParticlePosition, neighbors, R);
            }
        }

        if (topCell != null && !topCell.isEmpty()) {
            for (Particle otherParticle : topCell.getParticles()) {
                Position otherParticlePosition = particles.get(otherParticle);

                if (periodic && M != 1) {
                    if (otherParticlePosition.getY() < particlePosition.getY()) {
                        otherParticlePosition = new Position(otherParticlePosition.getX(), otherParticlePosition.getY() + L);
                    }
                }

                addIfInRadius(particle, particlePosition, otherParticle, otherParticlePosition, neighbors, R);
            }
        }

        if (topRightCell != null && !topRightCell.isEmpty()) {
            for (Particle otherParticle : topRightCell.getParticles()) {
                Position otherParticlePosition = particles.get(otherParticle);

                if (periodic && M != 1) {
                    if (otherParticlePosition.getY() < particlePosition.getY()) {
                        otherParticlePosition = new Position(otherParticlePosition.getX() + (otherParticlePosition.getX() < particlePosition.getX() ? L : 0)
                                , otherParticlePosition.getY() + L);
                    } else if (otherParticlePosition.getX() < particlePosition.getX()) {
                        otherParticlePosition = new Position(otherParticlePosition.getX() + L, otherParticlePosition.getY());
                    }
                }

                addIfInRadius(particle, particlePosition, otherParticle, otherParticlePosition, neighbors, R);
            }
        }

        if (rightCell != null && !rightCell.isEmpty()) {
            for (Particle otherParticle : rightCell.getParticles()) {
                Position otherParticlePosition = particles.get(otherParticle);

                if (periodic && M != 1) {
                    if (otherParticlePosition.getX() < particlePosition.getX()) {
                        otherParticlePosition = new Position(otherParticlePosition.getX() + L, otherParticlePosition.getY());
                    }
                }

                addIfInRadius(particle, particlePosition, otherParticle, otherParticlePosition, neighbors, R);
            }
        }

        if (bottomRightCell != null && !bottomRightCell.isEmpty()) {
            for (Particle otherParticle : bottomRightCell.getParticles()) {
                Position otherParticlePosition = particles.get(otherParticle);

                if (periodic && M != 1) {
                    if (otherParticlePosition.getY() > particlePosition.getY()) {
                        otherParticlePosition = new Position(otherParticlePosition.getX() + (otherParticlePosition.getX() < particlePosition.getX() ? L : 0),
                                otherParticlePosition.getY() - L);
                    } else if (otherParticlePosition.getX() < particlePosition.getX()) {
                        otherParticlePosition = new Position(otherParticlePosition.getX() + L, otherParticlePosition.getY());
                    }
                }

                addIfInRadius(particle, particlePosition, otherParticle, otherParticlePosition, neighbors, R);
            }
        }

    }

    private static void addIfInRadius(final Particle particle, final Position particlePosition,
                                      final Particle otherParticle, final Position otherParticlePosition,
                                      final Map<Particle, Set<Particle>> neighbors, final double R) {
        final double distanceBetween = calculateDistance(particlePosition, otherParticlePosition) - particle.getRadius() - otherParticle.getRadius();
        if (distanceBetween <= R) {
            neighbors.get(particle).add(otherParticle);
            neighbors.get(otherParticle).add(particle);
        }
    }

}



