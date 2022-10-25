package ar.edu.itba.ss.simulator.Algorithms.CellIndex;

import ar.edu.itba.ss.simulator.utils.Pair;
import ar.edu.itba.ss.simulator.utils.Particle;
import ar.edu.itba.ss.simulator.utils.R;

import java.util.*;

import static ar.edu.itba.ss.simulator.utils.R.values.R0;
import static java.lang.Math.hypot;

public class CellIndexMethod {

    public static Map<Particle, Set<Particle>> calculateNeighbors(final Map<Particle, R> particles,
                                                                  final int N,
                                                                  final int M,
                                                                  final int L,
                                                                  final int W,
                                                                  final double R,
                                                                  final Boolean periodic) {

        final Grid grid = new Grid(L, W, M, N, particles, periodic);

        final List<List<Cell>> cells = grid.getGrid();

        final Map<Particle, Set<Particle>> neighbors = new HashMap<>();
        particles.forEach((key, value) -> neighbors.put(key, new HashSet<>()));

        for (int y = 0; y < M; y++) {
            for (int x = 0; x < N; x++) {
                final Cell currentCell = cells.get(y).get(x);
                if (!currentCell.isEmpty()) {
                    for (Particle p : currentCell.getParticles()) {
                        checkNeighbors(p, particles, currentCell, neighbors, R, L, M, periodic);
                    }
                }
            }
        }

        return neighbors;
    }

    private static void checkNeighbors(final Particle particle, final Map<Particle, R> particles,
                                       final Cell cell, final Map<Particle, Set<Particle>> neighbors,
                                       final double R, final int L, final int M, final boolean periodic) {
        final Cell topCell = cell.getTopCell();
        final Cell rightCell = cell.getRightCell();
        final Cell topRightCell = cell.getTopRightCell();
        final Cell bottomRightCell = cell.getBottomRightCell();

        final Pair particlePosition = particles.get(particle).get(R0.ordinal());

        for (Particle otherParticle : cell.getParticles()) {
            Pair otherParticlePosition = particles.get(otherParticle).get(R0.ordinal());
            if (!particle.equals(otherParticle)) {

                addIfInRadius(particle, particlePosition, otherParticle, otherParticlePosition, neighbors, R);
            }
        }

        if (topCell != null && !topCell.isEmpty()) {
            for (Particle otherParticle : topCell.getParticles()) {
                Pair otherParticlePosition = particles.get(otherParticle).get(R0.ordinal());

                if (periodic && M != 1) {
                    if (otherParticlePosition.getY() < particlePosition.getY()) {
                        otherParticlePosition = new Pair(otherParticlePosition.getX(), otherParticlePosition.getY() + L);
                    }
                }

                addIfInRadius(particle, particlePosition, otherParticle, otherParticlePosition, neighbors, R);
            }
        }

        if (topRightCell != null && !topRightCell.isEmpty()) {
            for (Particle otherParticle : topRightCell.getParticles()) {
                Pair otherParticlePosition = particles.get(otherParticle).get(R0.ordinal());

                if (periodic && M != 1) {
                    if (otherParticlePosition.getY() < particlePosition.getY()) {
                        otherParticlePosition = new Pair(otherParticlePosition.getX() + (otherParticlePosition.getX() < particlePosition.getX() ? L : 0)
                            , otherParticlePosition.getY() + L);
                    } else if (otherParticlePosition.getX() < particlePosition.getX()) {
                        otherParticlePosition = new Pair(otherParticlePosition.getX() + L, otherParticlePosition.getY());
                    }
                }

                addIfInRadius(particle, particlePosition, otherParticle, otherParticlePosition, neighbors, R);
            }
        }

        if (rightCell != null && !rightCell.isEmpty()) {
            for (Particle otherParticle : rightCell.getParticles()) {
                Pair otherParticlePosition = particles.get(otherParticle).get(R0.ordinal());

                if (periodic && M != 1) {
                    if (otherParticlePosition.getX() < particlePosition.getX()) {
                        otherParticlePosition = new Pair(otherParticlePosition.getX() + L, otherParticlePosition.getY());
                    }
                }

                addIfInRadius(particle, particlePosition, otherParticle, otherParticlePosition, neighbors, R);
            }
        }

        if (bottomRightCell != null && !bottomRightCell.isEmpty()) {
            for (Particle otherParticle : bottomRightCell.getParticles()) {
                Pair otherParticlePosition = particles.get(otherParticle).get(R0.ordinal());

                if (periodic && M != 1) {
                    if (otherParticlePosition.getY() > particlePosition.getY()) {
                        otherParticlePosition = new Pair(otherParticlePosition.getX() + (otherParticlePosition.getX() < particlePosition.getX() ? L : 0),
                            otherParticlePosition.getY() - L);
                    } else if (otherParticlePosition.getX() < particlePosition.getX()) {
                        otherParticlePosition = new Pair(otherParticlePosition.getX() + L, otherParticlePosition.getY());
                    }
                }

                addIfInRadius(particle, particlePosition, otherParticle, otherParticlePosition, neighbors, R);
            }
        }

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



