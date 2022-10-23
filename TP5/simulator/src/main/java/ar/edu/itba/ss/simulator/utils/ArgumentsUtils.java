package ar.edu.itba.ss.simulator.utils;

import java.util.Optional;
import java.util.Properties;

public class ArgumentsUtils {

    public static String getPropertyOrFail(final Properties properties, final String property) throws IllegalArgumentException {
        return Optional.ofNullable(properties.getProperty(property)).orElseThrow(IllegalArgumentException::new);
    }

    public static String getPropertyOrDefault(final Properties properties, final String property, final String def) throws IllegalArgumentException {
        return Optional.ofNullable(properties.getProperty(property)).orElse(def);
    }

}
