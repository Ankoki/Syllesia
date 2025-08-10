package art.sylleth.syllesia.misc;

import art.sylleth.syllesia.Syllesia;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;

/**
 * Class containing miscellaneous functions that can be utilised throughout the code.
 */
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

    /**
     * Gets the buffered image from the resource folder.
     *
     * @param path the path to the image.
     * @return the found image, if no image is found or an exception is thrown, null.
     */
    @Nullable
    public static BufferedImage getResourceImage(String path) {
        try {
            ClassLoader loader = Misc.class.getClassLoader();
            URL resource = loader.getResource(path);
            if (resource == null)
                throw new IllegalArgumentException("Resource not found: " + path);
            return ImageIO.read(new File(resource.toURI()));
        } catch (IOException | URISyntaxException ex) {
            Syllesia.getInstance().getLogger().error(ex, Misc.class, 84);
            return null;
        }
    }

}
