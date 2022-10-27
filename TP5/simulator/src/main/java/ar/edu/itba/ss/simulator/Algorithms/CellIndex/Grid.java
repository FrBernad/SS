package ar.edu.itba.ss.simulator.Algorithms.CellIndex;

import ar.edu.itba.ss.simulator.utils.Pair;
import ar.edu.itba.ss.simulator.utils.Particle;
import ar.edu.itba.ss.simulator.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ar.edu.itba.ss.simulator.simulation.VibratedSilo.INTERACTION_RADIUS;
import static ar.edu.itba.ss.simulator.utils.R.values.R0;

public class Grid {

    private static final Logger LOGGER = LoggerFactory.getLogger(Grid.class);

    private final List<List<Cell>> grid;
    private final int M;
    private final int N;
    private final int L;
    private final double incrementX;
    private final double incrementY;


    public Grid(final int L, final int W, final int M, final int N) {
        this.M = M;
        this.N = N;
        this.L = L;

        this.grid = new ArrayList<>();
        incrementX = W / (double) N;
        incrementY = L / (double) M;

        LOGGER.info(String.format("Initializing grid with: M = %d / N = %d / Interaction Radius = %f / dx = %f / dy = %f",
            M, N, INTERACTION_RADIUS, incrementX, incrementY));


        buildGrid();
        setCellNeighbors();
    }

    public void fillGrid(Map<Particle, R> particles) {
        for (Map.Entry<Particle, R> entry : particles.entrySet()) {
            Pair position = entry.getValue().get(R0.ordinal());
            if (position.getY() >= 0 && position.getY() < L) {
                int x_index = (int) Math.floor(position.getX() / incrementX);
                int y_index = (int) Math.floor(position.getY() / incrementY);

                grid.get(y_index).get(x_index).addParticle(entry.getKey());
            }
        }
    }

    public void clear() {
        for (int y = 0; y < M; y++) {
            for (int x = 0; x < N; x++) {
                this.grid.get(y).get(x).getParticles().clear();
            }
        }
    }

    public List<List<Cell>> getGrid() {
        return grid;
    }

    public int getM() {
        return M;
    }

    public int getN() {
        return N;
    }

    private void buildGrid() {
        double leftBoundary;
        double rightBoundary;
        double bottomBoundary = 0;
        double topBoundary = incrementY;

        for (int y = 0; y < M; y++) {
            leftBoundary = 0;
            rightBoundary = leftBoundary + incrementX;
            this.grid.add(y, new ArrayList<>());
            for (int x = 0; x < N; x++) {
                this.grid.get(y).add(x, new Cell(x, y, leftBoundary, rightBoundary, topBoundary, bottomBoundary));
                leftBoundary += incrementX;
                rightBoundary += incrementX;
            }
            topBoundary += incrementY;
            bottomBoundary += incrementY;
        }

    }

    // W --> ancho = 20
    // L --> alto = 70
    // N --> 4 --> incrementX = 5
    // M --> 5 --> incrementY = 14

    private void setCellNeighbors() {
        for (int y = 0; y < M; y++) {
            for (int x = 0; x < N; x++) {

                final Cell currentCell = this.grid.get(y).get(x);

                Cell topCell;
                Cell rightCell;
                Cell topRightCell;
                Cell bottomRightCell;

                topCell = this.grid.get((y + 1) % M).get(x);
                topRightCell = this.grid.get((y + 1) % M).get((x + 1) % N);
                bottomRightCell = this.grid.get((y - 1) < 0 ? M - 1 : y - 1).get((x + 1) % N);
                rightCell = this.grid.get(y).get((x + 1) % N);

                if (y + 1 >= M) {
                    topCell = null;
                    topRightCell = null;
                }
                if (y - 1 < 0) {
                    bottomRightCell = null;
                }
                if (x + 1 >= N) {
                    topRightCell = null;
                    rightCell = null;
                    bottomRightCell = null;
                }

                currentCell.setNeighbors(topCell, topRightCell, rightCell, bottomRightCell);
            }
        }
    }

}
