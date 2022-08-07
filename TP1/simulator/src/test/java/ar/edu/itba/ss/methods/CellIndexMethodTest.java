package ar.edu.itba.ss.methods;

import ar.edu.itba.ss.simulator.methods.CellIndex.Cell;
import ar.edu.itba.ss.simulator.methods.CellIndex.Grid;
import ar.edu.itba.ss.simulator.utils.Particle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static ar.edu.itba.ss.simulator.utils.ParseUtils.ParticlesParserResult;
import static ar.edu.itba.ss.simulator.utils.ParseUtils.parseParticlesList;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class CellIndexMethodTest {

    private static final String dynamicFilePath = "/Users/frbernad/PROGRAMMING/ITBA/SS/TPs/TP1/assets/Dynamic100.txt";
    private static final String staticFilePath = "/Users/frbernad/PROGRAMMING/ITBA/SS/TPs/TP1/assets/Static100.txt";

    private ParticlesParserResult particlesParserResult;

    private Map<Particle, Particle.Position> particles;

    @Before
    public void initParticles() throws IOException {
        particlesParserResult = parseParticlesList(
            Paths.get(staticFilePath).toFile(),
            Paths.get(dynamicFilePath).toFile(),
            " "
        );

        particles = particlesParserResult.getParticlesPerTime().get(0);
    }

    @Test
    public void testParticleInCell() {
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

    @Test
    public void testCellNeighborsPeriodic() {
        final int M = 5;
        final int L = particlesParserResult.getL();

        final Grid grid = new Grid(L, M, particles, true);

        List<List<Cell>> matrix = grid.getGrid();
        for (int y = 0; y < M; y++) {
            for (int x = 0; x < M; x++) {
                final Cell currentCell = matrix.get(y).get(x);
                //Arriba derecha caso borde
                if (y == M - 1 && x == M - 1) {
                    assertTrue(currentCell.getTopRightCell().getX() == 0 && currentCell.getTopRightCell().getY() == 0);
                }
                //Abajo derecha caso borde
                else if (y == 0 && x == M - 1) {
                    assertTrue(currentCell.getBottomRightCell().getX() == 0 && currentCell.getBottomRightCell().getY() == M - 1);
                }
                //Derecha X
                if (x == M - 1) {
                    assertEquals(0, currentCell.getRightCell().getX());
                }
                //Medio X
                else {
                    assertEquals(currentCell.getRightCell().getX(), currentCell.getX() + 1);
                }
                //Arriba al medio
                if (y == M - 1 && x != M - 1) {
                    assertEquals(0, currentCell.getTopCell().getY());
                    assertEquals(currentCell.getX() + 1, currentCell.getTopRightCell().getX());
                }
                //Arriba derecha normal
                if (x != M - 1 && y != M - 1) {
                    assertEquals(currentCell.getX() + 1, currentCell.getTopRightCell().getX());
                    assertEquals(currentCell.getY() + 1, currentCell.getTopRightCell().getY());
                }
                //Abajo derecha normal
                if (y != 0 && x != M - 1) {
                    assertEquals(currentCell.getX() + 1, currentCell.getBottomRightCell().getX());
                    assertEquals(currentCell.getY() - 1, currentCell.getBottomRightCell().getY());
                }
            }
        }
    }

    @Test
    public void testCellNeighborsNonPeriodic() {
        final int M = 5;
        final int L = particlesParserResult.getL();

        final Grid grid = new Grid(L, M, particles, false);

        List<List<Cell>> matrix = grid.getGrid();
        for (int y = 0; y < M; y++) {
            for (int x = 0; x < M; x++) {
                final Cell currentCell = matrix.get(y).get(x);
                //Arriba derecha caso borde
                if (y == M - 1 && x == M - 1) {
                    assertNull(currentCell.getTopRightCell());
                    assertNull(currentCell.getTopCell());
                    assertNull(currentCell.getRightCell());
                    assertNull(currentCell.getBottomRightCell());
                }
                //Abajo derecha caso borde
                else if (y == 0 && x == M - 1) {
                    assertNull(currentCell.getBottomRightCell());
                }
                //Derecha X
                if (x == M - 1) {
                    assertNull(currentCell.getBottomRightCell());
                    assertNull(currentCell.getRightCell());
                }
                //Medio X
                else {
                    assertEquals(currentCell.getRightCell().getX(), currentCell.getX() + 1);
                }
                //Arriba al medio
                if (y == M - 1 && x != M - 1) {
                    assertNull(currentCell.getTopCell());
                    assertNull(currentCell.getTopRightCell());
                }
                //Arriba derecha normal
                if (x != M - 1 && y != M - 1) {
                    assertEquals(currentCell.getX() + 1, currentCell.getTopRightCell().getX());
                    assertEquals(currentCell.getY() + 1, currentCell.getTopRightCell().getY());
                }
                //Abajo derecha normal
                if (y != 0 && x != M - 1) {
                    assertEquals(currentCell.getX() + 1, currentCell.getBottomRightCell().getX());
                    assertEquals(currentCell.getY() - 1, currentCell.getBottomRightCell().getY());
                }
            }
        }
    }

    @Test
    public void testCellNeighborsM1Periodic() {
        final int M = 1;
        final int L = particlesParserResult.getL();

        final Grid grid = new Grid(L, M, particles, true);

        List<List<Cell>> matrix = grid.getGrid();
        for (int y = 0; y < M; y++) {
            for (int x = 0; x < M; x++) {
                final Cell currentCell = matrix.get(y).get(x);
                assertEquals(currentCell.getRightCell(), currentCell);
                assertEquals(currentCell.getTopCell(), currentCell);
                assertEquals(currentCell.getTopRightCell(), currentCell);
                assertEquals(currentCell.getBottomRightCell(), currentCell);
            }
        }
    }

    @Test
    public void testCellNeighborsM1NonPeriodic() {
        final int M = 1;
        final int L = particlesParserResult.getL();

        final Grid grid = new Grid(L, M, particles, false);

        List<List<Cell>> matrix = grid.getGrid();
        for (int y = 0; y < M; y++) {
            for (int x = 0; x < M; x++) {
                final Cell currentCell = matrix.get(y).get(x);
                assertNull(currentCell.getRightCell());
                assertNull(currentCell.getTopCell());
                assertNull(currentCell.getTopRightCell());
                assertNull(currentCell.getBottomRightCell());
            }
        }
    }
}
