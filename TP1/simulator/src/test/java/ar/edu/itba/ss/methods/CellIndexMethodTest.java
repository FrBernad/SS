package ar.edu.itba.ss.methods;

import ar.edu.itba.ss.simulator.utils.Particle;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static ar.edu.itba.ss.simulator.methods.CellIndexMethod.*;
import static ar.edu.itba.ss.simulator.methods.CellIndexMethod.Grid.*;
import static ar.edu.itba.ss.simulator.utils.ParseUtils.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class CellIndexMethodTest {

    private static final String dynamicFilePath = "/Users/frbernad/PROGRAMMING/ITBA/SS/TPs/TP1/assets/Dynamic100.txt";
    private static final String staticFilePath = "/Users/frbernad/PROGRAMMING/ITBA/SS/TPs/TP1/assets/Static100.txt";

    @Test
    public void testParticleInCell() throws IOException {

        final ParticlesParserResult particlesParserResult = parseParticlesList(Paths.get(staticFilePath).toFile(),
            Paths.get(dynamicFilePath).toFile(),
            " ");

        final Map<Particle, Particle.Position> particles = particlesParserResult.getParticlesPerTime().get(0);
        final int M = 10;
        final int L = particlesParserResult.getL();

        final Grid grid = new Grid(L, M, particles, false);

        List<List<Cell>> matrix = grid.getGrid();
        int i = 0;
        int j = 0;
        for (List<Cell> rows : matrix) {
            j = 0;
            for (Cell cell : rows) {
                System.out.printf(
                    "-----------------------------\n" +
                        "Cell [ %d : %d ]\n" +
                        "%f<X<%f\n" +
                        "%f<Y< %f\n", i, j, cell.getLeftBoundary(), cell.getRightBoundary(), cell.getTopBoundary(), cell.getBottomBoundary());
                j++;
                for (Particle p : cell.getParticles()) {
                    Particle.Position position = particles.get(p);
                    boolean condition = (
                        position.getX() >= cell.getLeftBoundary() && position.getX() < cell.getRightBoundary()) &&
                        (position.getY() >= cell.getBottomBoundary() && position.getY() < cell.getTopBoundary());
                    System.out.printf("Particle: %d X: %f ---- Y: %f (%s)\n", p.getId(), position.getX(), position.getY(), condition);
                    assertTrue(condition);
                }
            }
            i++;
        }
    }
}
