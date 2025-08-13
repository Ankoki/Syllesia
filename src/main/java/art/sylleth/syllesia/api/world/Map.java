package art.sylleth.syllesia.api.world;

import art.sylleth.syllesia.entities.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to navigate maps.
 */
public class Map {

    public static final int[][] RUINS = {
            {1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2},
            {1, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2},
            {1, 0, 3, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 2},
            {1, 0, 3, 0, 0, 0, 3, 0, 2, 0, 0, 0, 0, 0, 2},
            {1, 0, 3, 0, 0, 0, 3, 0, 2, 2, 2, 0, 2, 2, 2},
            {1, 0, 3, 0, 0, 0, 3, 0, 2, 0, 0, 0, 0, 0, 2},
            {1, 0, 3, 3, 0, 3, 3, 0, 2, 0, 0, 0, 0, 0, 2},
            {1, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2},
            {1, 1, 1, 1, 1, 1, 1, 1, 4, 4, 4, 0, 4, 4, 4},
            {1, 0, 0, 0, 0, 0, 1, 4, 0, 0, 0, 0, 0, 0, 4},
            {1, 0, 0, 0, 6, 0, 1, 4, 0, 0, 0, 0, 0, 0, 4},
            {1, 0, 0, 2, 0, 0, 1, 4, 0, 3, 3, 3, 3, 0, 4},
            {1, 0, 0, 0, 0, 0, 1, 4, 0, 3, 3, 3, 3, 0, 4},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4},
            {1, 1, 1, 1, 1, 1, 1, 4, 4, 4, 4, 4, 4, 4, 4}
    };

    private final String name;
    private final int id = 0; // Will be set when registered.
    private final int[][] matrix;
    private final List<Entity> entities = new ArrayList<>(); // Entities present in this world.

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

    /**
     * Gets the entities present in this world.
     *
     * @return the entities.
     */
    public List<Entity> getEntities() {
        return this.entities;
    }

    /**
     * Adds an entity to this map.
     *
     * @param entity the entity to add.
     */
    public void addEntity(Entity entity) {
        this.entities.add(entity);
    }

}
