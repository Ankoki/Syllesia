package art.sylleth.syllesia.api.world;

/**
 * Class used to navigate maps.
 */
public class Map {

    private final String name;
    private final int id = 0; // Will be set when registered.
    private final int[][] matrix;

    /**
     * Creates a new map with the given properties.
     * The matrix should be in the form of a 2D map.
     *
     * @param name the name of this map.
     * @param matrix the matrix of this map.
     */
    public Map(String name, int[][] matrix) {
        this.name = name;
        this.matrix = matrix;
    }

    /**
     * Gets the name of this map.
     *
     * @return the name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the id of this map.
     *
     * @return the id.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets the matrix of this map.
     *
     * @return the matrix.
     */
    public int[][] getMatrix() {
        return this.matrix;
    }

}
