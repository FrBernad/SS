package ar.edu.itba.ss.simulator.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class RandomGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(RandomGenerator.class);

    private final Random random;
    private static RandomGenerator instance;

    private RandomGenerator(Long seed) {
        if (seed == null) {
            seed = new Random().nextLong();
        }
        random = new Random(seed);
        LOGGER.info(String.format("Generating random number generator with seed: %d", seed));
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
