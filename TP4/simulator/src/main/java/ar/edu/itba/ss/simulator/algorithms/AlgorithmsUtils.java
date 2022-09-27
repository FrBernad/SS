package ar.edu.itba.ss.simulator.algorithms;

import ar.edu.itba.ss.simulator.utils.Pair;
import ar.edu.itba.ss.simulator.utils.Particle;
import ar.edu.itba.ss.simulator.utils.R;

import static ar.edu.itba.ss.simulator.utils.R.values.*;

public class AlgorithmsUtils {

    public static Pair calculateAcceleration(final double mass, final Pair r0,
                                             final Pair r1, final double k, final double gamma) {
        final double r2x = (-k * r0.getX() - gamma * r1.getX()) / mass;
        final double r2y = (-k * r0.getY() - gamma * r1.getY()) / mass;

        return new Pair(r2x, r2y);
    }

    public static R calculateInitialR(final double mass, final Particle.State state,
                                      final double k, final double gamma) {
        final R initialR = new R();
        //r0
        initialR.add(state.getPosition().getX(), state.getPosition().getY());
        //r1
        initialR.add(state.getVelocityX(), state.getVelocityY());
        //r2
        final Pair r2 = calculateAcceleration(mass, initialR.get(R0.ordinal()), initialR.get(R1.ordinal()), k, gamma);
        initialR.add(r2.getX(), r2.getY());
        //r3
        initialR.add(0, 0);
        //r4
        initialR.add(0, 0);
        //r5
        initialR.add(0, 0);

        return initialR;
    }


}
