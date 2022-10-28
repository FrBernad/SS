package ar.edu.itba.ss.simulator.utils;

import java.util.Random;

public class RandomGenerator {
    private final Random random;
    private static RandomGenerator instance;

    private RandomGenerator(Long seed) {
        if (seed != null) {
            this.random = new Random(seed);
        } else {
            this.random = new Random();
        }
    }

    public static RandomGenerator getInstance() {
        if (instance == null) {
            throw new RuntimeException();
        }
        return instance;
    }

    public static void setInstance(Long seed) {
        if (instance == null) {
            instance = new RandomGenerator(seed);
        }
    }

    public Random getRandom() {
        return random;
    }

}
