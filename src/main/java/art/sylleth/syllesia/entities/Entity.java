package art.sylleth.syllesia.entities;

import art.sylleth.syllesia.api.world.Location;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;

public abstract class Entity {

    protected Location location;
    private final BufferedImage front;
    private final BufferedImage back;
    private final BufferedImage left;
    private final BufferedImage right;
    private final BufferedImage top;
    private final BufferedImage bottom;

    /**
     * Creates a new entity with the given location.
     *
     * @param location location of the entity.
     * @param front the front texture of the entity.
     * @param back the back texture of the entity.
     * @param left the left texture of the entity.
     * @param right the right texture of the entity.
     * @param top the top texture of the entity.
     * @param bottom the bottom texture of the entity.
     */
    public Entity(Location location,
                  BufferedImage front,
                  BufferedImage back,
                  BufferedImage left,
                  BufferedImage right,
                  BufferedImage top,
                  BufferedImage bottom) {
        this.location = location;
        this.front = front;
        this.back = back;
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    /**
     * Gets the location of this entity.
     *
     * @return the location.
     */
    @NotNull
    public Location getLocation() {
        return this.location;
    }

    /**
     * Whether this entity should always face the player.
     *
     * @return true if should.
     */
    public abstract boolean alwaysFacePlayer();

    /**
     * Gets the ID of this entity.
     *
     * @return the id of this entity.
     */
    @NotNull
    public abstract String getId();

    /**
     * Gets the front texture of this entity.
     *
     * @return the front texture.
     */
    @NotNull
    public BufferedImage getFrontTexture() {
        return this.front;
    }

    /**
     * Gets the back texture of this entity.
     *
     * @return the back texture.
     */
    @NotNull
    public BufferedImage getBackTexture() {
        return this.back;
    }

    /**
     * Gets the left texture of this entity.
     *
     * @return the left texture.
     */
    @NotNull
    public BufferedImage getLeftTexture() {
        return this.left;
    }

    /**
     * Gets the right texture of this entity.
     *
     * @return the right texture.
     */
    @NotNull
    public BufferedImage getRightTexture() {
        return this.right;
    }

    /**
     * Gets the top texture of this entity.
     *
     * @return the top texture.
     */
    @NotNull
    public BufferedImage getTopTexture() {
        return this.top;
    }


    /**
     * Gets the bottom texture of this entity.
     *
     * @return the bottom texture.
     */
    @NotNull
    public BufferedImage getBottomTexture() {
        return this.bottom;
    }

}
