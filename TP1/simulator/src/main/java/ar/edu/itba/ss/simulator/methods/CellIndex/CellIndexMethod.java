package ar.edu.itba.ss.simulator.methods.CellIndex;

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

        final Map<Integer, Set<Particle>> neighbors = new HashMap<>();
        particles.forEach((key, value) -> neighbors.put(key.getId(), new HashSet<>()));

        for (int y = 0; y < M; y++) {
            for (int x = 0; x < M; x++) {
                final Cell currentCell = cells.get(y).get(x);
                if (!currentCell.isEmpty()) {
                    for (Particle p : currentCell.getParticles()) {
                        checkNeighbors(R, p, particles, currentCell, neighbors, L);
                    }
                }
            }
        }

        executionTimestamps.setAlgorithmEnd(LocalDateTime.now());

        return new CellIndexMethodResults(neighbors, executionTimestamps);
    }

    private static void checkNeighbors(double R, final Particle particle, final Map<Particle, Position> particles,
                                       final Cell cell, final Map<Integer, Set<Particle>> neighbors, final int L) {
        final Cell topCell = cell.getTopCell();
        final Cell rightCell = cell.getRightCell();
        final Cell topRightCell = cell.getTopRightCell();
        final Cell bottomRightCell = cell.getBottomRightCell();

        final Position particlePosition = particles.get(particle);

        for (Particle otherParticle : cell.getParticles()) {
            Position otherParticlePosition = particles.get(otherParticle);
            if (!particle.equals(otherParticle)) {
                final double distanceBetween = calculateDistance(particlePosition, otherParticlePosition) - particle.getRadius() - otherParticle.getRadius();
                if (distanceBetween <= R) {
                    neighbors.get(particle.getId()).add(otherParticle);
                    neighbors.get(otherParticle.getId()).add(particle);
                }
            }
        }

        if (topCell != null && !topCell.isEmpty()) {
            for (Particle otherParticle : topCell.getParticles()) {
                Position otherParticlePosition = particles.get(otherParticle);
                if (otherParticlePosition.getY() < particlePosition.getY()) {
                    otherParticlePosition = new Position(otherParticlePosition.getX(), otherParticlePosition.getY() + L);
                }
                final double distanceBetween = calculateDistance(particlePosition, otherParticlePosition) - particle.getRadius() - otherParticle.getRadius();
                if (distanceBetween <= R) {
                    neighbors.get(particle.getId()).add(otherParticle);
                    neighbors.get(otherParticle.getId()).add(particle);
                }
            }
        }

        if (topRightCell != null && !topRightCell.isEmpty()) {
            for (Particle otherParticle : topRightCell.getParticles()) {
                Position otherParticlePosition = particles.get(otherParticle);
                if (otherParticlePosition.getY() < particlePosition.getY()) {
                    otherParticlePosition = new Position(otherParticlePosition.getX() + (otherParticlePosition.getX() < particlePosition.getX() ? L : 0),
                        otherParticlePosition.getY() + L);
                }
                final double distanceBetween = calculateDistance(particlePosition, otherParticlePosition) - particle.getRadius() - otherParticle.getRadius();
                if (distanceBetween <= R) {
                    neighbors.get(particle.getId()).add(otherParticle);
                    neighbors.get(otherParticle.getId()).add(particle);
                }
            }
        }

        if (rightCell != null && !rightCell.isEmpty()) {
            for (Particle otherParticle : rightCell.getParticles()) {
                Position otherParticlePosition = particles.get(otherParticle);
                if (otherParticlePosition.getX() < particlePosition.getX()) {
                    otherParticlePosition = new Position(otherParticlePosition.getX() + L, otherParticlePosition.getY());
                }
                final double distanceBetween = calculateDistance(particlePosition, otherParticlePosition) - particle.getRadius() - otherParticle.getRadius();
                if (distanceBetween <= R) {
                    neighbors.get(particle.getId()).add(otherParticle);
                    neighbors.get(otherParticle.getId()).add(particle);
                }
            }
        }

        if (bottomRightCell != null && !bottomRightCell.isEmpty()) {
            for (Particle otherParticle : bottomRightCell.getParticles()) {
                Position otherParticlePosition = particles.get(otherParticle);
                if (otherParticlePosition.getY() > particlePosition.getY()) {
                    otherParticlePosition = new Position(otherParticlePosition.getX() + (otherParticlePosition.getX() < particlePosition.getX() ? L : 0),
                        otherParticlePosition.getY() - L);
                }
                final double distanceBetween = calculateDistance(particlePosition, otherParticlePosition) - particle.getRadius() - otherParticle.getRadius();
                if (distanceBetween <= R) {
                    neighbors.get(particle.getId()).add(otherParticle);
                    neighbors.get(otherParticle.getId()).add(particle);
                }
            }
        }

//        for (Cell c : neighborCells) {
//            if (c.isPresent() && !c.get().isEmpty()) {
//                for (Particle otherParticle : c.get().getParticles()) {
//                    if (!otherParticle.equals(particle)) {
//                        final Position otherParticlePosition = particles.get(otherParticle);
//                        final double distanceBetween = calculateDistance(particlePosition, otherParticlePosition) - particle.getRadius() - otherParticle.getRadius();
//                        if (distanceBetween <= R) {
//                            neighbors.get(particle.getId()).add(otherParticle);
//                            neighbors.get(otherParticle.getId()).add(particle);
//                        }
//                    }
//                }
//            }
//        }

    }

}



