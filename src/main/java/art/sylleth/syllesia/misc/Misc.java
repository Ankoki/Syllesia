package art.sylleth.syllesia.misc;

import java.awt.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class Misc {

    private static final DecimalFormat DECIMAL_FORMATTER = new DecimalFormat("#.###");

    static {
        DECIMAL_FORMATTER.setRoundingMode(RoundingMode.CEILING);
    }

    /**
     * Gets the colour object from a given hex code.
     *
     * @param hex the hex to parse. May start with #.
     * @return the parsed Color object.
     */
    public static Color fromHex(String hex) {
        if (hex.startsWith("#"))
            hex = hex.substring(1);
        if (hex.length() == 6)
            return new Color(
                    Integer.valueOf(hex.substring(0, 2), 16),
                    Integer.valueOf(hex.substring(2, 4), 16),
                    Integer.valueOf(hex.substring(4, 6), 16)
            );
        else if (hex.length() == 8)
            return new Color(
                    Integer.valueOf(hex.substring(0, 2), 16),
                    Integer.valueOf(hex.substring(2, 4), 16),
                    Integer.valueOf(hex.substring(4, 6), 16),
                    Integer.valueOf(hex.substring(6, 8), 16)
            );
        else
            throw new IllegalArgumentException("Invalid hex code: #" + hex);
    }

    /**
     * Rounds a double to the given amount of points. Must be greater than 0.
     * Uses the ceiling rounding method.
     *
     * @param value the double to round.
     * @param points the amount of points.
     * @return the value to the given amount of points.
     * @throws IllegalArgumentException if points is smaller than or equal to 0.
     */
    public static String toNPoints(double value, int points) {
        if (points <= 0)
            throw new IllegalArgumentException("Points must be greater than zero.");
        DECIMAL_FORMATTER.applyPattern("#." + ("#").repeat(points));
        return DECIMAL_FORMATTER.format(value);
    }

}
