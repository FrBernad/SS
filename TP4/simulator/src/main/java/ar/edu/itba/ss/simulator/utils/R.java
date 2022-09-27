package ar.edu.itba.ss.simulator.utils;

import java.util.ArrayList;
import java.util.List;

public class R {
    private final ArrayList<Pair> ri;

    public R() {
        this.ri = new ArrayList<>();
    }

    public void add(final double x, final double y) {
        ri.add(new Pair(x, y));
    }

    public void set(final int index, final double x, final double y) {
        ri.set(index, new Pair(x, y));
    }

    public Pair get(final int index) {
        return ri.get(index);
    }

    public enum values {
        R0,
        R1,
        R2,
        R3,
        R4,
        R5
    }

}
