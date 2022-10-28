package ar.edu.itba.ss.simulator.utils;

import java.util.Arrays;
import java.util.List;

public class R {
    private final List<Pair<Double, Double>> ri;

    public R() {
        this.ri = Arrays.asList(new Pair<Double, Double>(0.0, 0.0), new Pair<Double, Double>(0.0, 0.0), new Pair<Double, Double>(0.0, 0.0));
    }

    public void set(final int index, final double x, final double y) {
        ri.set(index, new Pair<Double, Double>(x, y));
    }

    public Pair<Double, Double> get(final int index) {
        return ri.get(index);
    }

    public enum values {
        R0,
        R1,
        R2
    }

}
