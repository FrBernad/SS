package ar.edu.itba.ss.simulator.algorithms;

import ar.edu.itba.ss.simulator.utils.Pair;
import ar.edu.itba.ss.simulator.utils.Particle.State;
import ar.edu.itba.ss.simulator.utils.R;

import static ar.edu.itba.ss.simulator.utils.R.values.*;
import static java.lang.Math.pow;

public class AlgorithmsUtils {

    public static Pair calculateAcceleration(final double mass, final Pair r0,
                                             final Pair r1, final double k, final double gamma) {
        final double r2x = (-k * r0.getX() - gamma * r1.getX()) / mass;
        final double r2y = (-k * r0.getY() - gamma * r1.getY()) / mass;

        return new Pair(r2x, r2y);
    }

    public static R calculateInitialR(final double mass, final State state,
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

    public static R euler(final R r, final double dt, final double mass, final double k, final double gamma) {

        final R eulerR = new R();
        final Pair r0 = r.get(R0.ordinal());
        final Pair r1 = r.get(R1.ordinal());
        final Pair r2 = r.get(R2.ordinal());

        // r0
        double r0x = r0.getX() + dt * r1.getX() + (pow(dt, 2) / (2 * mass)) * r2.getX() * mass;
        double r0y = r0.getY() + dt * r1.getY() + (pow(dt, 2) / (2 * mass)) * r2.getY() * mass;
        eulerR.add(r0x, r0y);

        // r1
        double r1x = r1.getX() + (dt / mass) * r2.getX() * mass;
        double r1y = r1.getY() + (dt / mass) * r2.getY() * mass;
        eulerR.add(r1x, r1y);

        final Pair eulerR2 = calculateAcceleration(mass, eulerR.get(R0.ordinal()), eulerR.get(R1.ordinal()), k, gamma);
        eulerR.add(eulerR2.getX(), eulerR2.getY());

        return eulerR;
    }

}
