package art.sylleth.syllesia.api.world;

import art.sylleth.syllesia.Syllesia;
import art.sylleth.syllesia.entities.Entity;
import art.sylleth.syllesia.platform.textures.Texture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to navigate maps.
 */
public class Map {

    private final String name;
    private final List<Entity> entities = new ArrayList<>(); // Entities present in this world.
    private final int[][] matrix;
    private final Block[][] blockMatrix;

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
        this.blockMatrix = new Block[matrix.length][matrix[0].length];
        for (int x = 0; x < matrix.length; x++) {
            for (int y = 0; y < matrix[x].length; y++) {
                Location location = new Location(x, y, this);
                int id = matrix[x][y];
                Texture texture = Texture.fromId(id);
                if (texture == null)
                    texture = Texture.AIR;
                blockMatrix[x][y] = new Block(location, texture);
            }
        }
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
     * Gets the matrix of this map.
     *
     * @return the matrix.
     */
    public int[][] getMatrix() {
        return this.matrix;
    }

    /**
     * Gets the block at the given location.
     *
     * @param location the location of the block.
     * @return the block at the location, or null if out of map bounds.
     */
    @Nullable
    public Block getBlockAt(Location location) {
        return this.getBlockAt((int) location.getX(), (int) location.getY());
    }

    /**
     * Gets the block at the given location.
     *
     * @param x the x position of the block.
     * @param y the y position of the block.
     * @return the block at the location, or null if out of map bounds.
     */
    @Nullable
    public Block getBlockAt(int x, int y) {
        try {
            return this.blockMatrix[x][y];
        } catch (ArrayIndexOutOfBoundsException ignored) {
            return null;
        }
    }

    /**
     * Gets the entities present in this world.
     *
     * @return the entities.
     */
    @NotNull
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

    /**
     * Makes sure that the block data matches the matrix data.
     *
     * @param block the block to validate.
     */
    protected void update(Block block) {
        int x = (int) block.getLocation().getX();
        int y = (int) block.getLocation().getY();
        try {
            if (matrix[x][y] != block.getTexture().getId())
                matrix[x][y] = block.getTexture().getId();
        } catch (ArrayIndexOutOfBoundsException ignored) {
            Syllesia.getInstance().getLogger().warn("Block " + x + ", " + y + " is out of bounds of the map '" + this.getName() + "' coordinates.");
        }
    }

}
