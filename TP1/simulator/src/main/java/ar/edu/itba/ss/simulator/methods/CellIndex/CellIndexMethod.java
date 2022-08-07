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
                        checkNeighbors(R, p, particles, currentCell, neighbors);
                    }
                }
            }
        }

        executionTimestamps.setAlgorithmEnd(LocalDateTime.now());

        return new CellIndexMethodResults(neighbors, executionTimestamps);
    }

    private static void checkNeighbors(double R, Particle particle, Map<Particle, Position> particles, Cell cell, Map<Integer, Set<Particle>> neighbors) {
        final Cell topCell = cell.getTopCell();
        final Cell rightCell = cell.getRightCell();
        final Cell topRightCell = cell.getTopRightCell();
        final Cell bottomRightCell = cell.getBottomRightCell();

        List<Cell> neighborCells = List.of(topCell, rightCell, topRightCell, bottomRightCell);

        final Position particlePosition = particles.get(particle);

        for (Cell c : neighborCells) {
            if (!c.isEmpty()) {
                for (Particle otherParticle : c.getParticles()) {
                    final Position currentPosition = particles.get(otherParticle);
                    final double distanceBetween = calculateDistance(particlePosition, currentPosition) - particle.getRadius() - otherParticle.getRadius();
                    //FIXME: Ver cuando esta un circulo adentro del otro o intersecciones
                    if (distanceBetween <= R) {
                        neighbors.get(particle.getId()).add(otherParticle);
                        neighbors.get(otherParticle.getId()).add(particle);
                    }
                }
            }
        }

    }

}



