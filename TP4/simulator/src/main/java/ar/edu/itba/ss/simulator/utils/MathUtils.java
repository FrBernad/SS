package ar.edu.itba.ss.simulator.utils;

public class MathUtils {

    public static int fact(int number) throws IllegalArgumentException {

        if (number < 0) {
            throw new IllegalArgumentException();
        }

        int factorial = 1;
        while (number != 0) {
            factorial = factorial * number;
            number--;
        }
        return factorial;
    }
}
