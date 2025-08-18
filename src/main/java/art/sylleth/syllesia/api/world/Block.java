package art.sylleth.syllesia.api.world;

import art.sylleth.syllesia.platform.textures.Texture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

// TODO convert matrix to support blocks, so if needed, map structure can be changed at runtime.
public class Block {

    private final Location location;
    private Texture texture;

    /**
     * Creates a new block object for the given location.
     *
     * @param location the location of this block.
     * @param texture the texture of this block.
     */
    protected Block(Location location, Texture texture) {
        this.location = location;
        this.texture = texture;
    }

    /**
     * Gets the location of this block.
     *
     * @return the location.
     */
    @NotNull
    public Location getLocation() {
        return this.location;
    }

    /**
     * Gets the texture of this block.
     *
     * @return the texture of this block.
     */
    @NotNull
    public Texture getTexture() {
        return this.texture;
    }

    /**
     * Sets the texture of this block.
     *
     * @param texture the updated texture of this block.
     */
    public void setTexture(@Nullable Texture texture) {
        this.texture = Objects.requireNonNullElse(texture, Texture.AIR);
        this.location.getMap().update(this);
    }

    /**
     * Checks if this block is air.
     *
     * @return true if air, else false.
     */
    public boolean isAir() {
        return texture == null || texture.getId() == 0;
    }

}
