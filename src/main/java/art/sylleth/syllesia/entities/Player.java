package art.sylleth.syllesia.entities;

import art.sylleth.syllesia.platform.Location;
import art.sylleth.syllesia.platform.game.Camera;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Class to control the player that is playing.
 */
public class Player {

    private final String name;
    private final UUID uuid;
    private final Camera camera;

    /**
     * Creates a new player object with the given details.
     * UUID's are unused as of now, however we want to leave room for expansion to multi-player.
     *
     * @param name the name of the player.
     * @param uuid the UUID of the player.
     */
    public Player(String name, UUID uuid, Camera camera) {
        this.name = name;
        this.uuid = uuid;
        this.camera = camera;
    }

    /**
     * Gets the name of this player.
     *
     * @return the name.
     */
    @NotNull
    public String getName() {
        return this.name;
    }

    /**
     * Gets the UUID of this player.
     *
     * @return the uuid.
     */
    @NotNull
    public UUID getUuid() {
        return this.uuid;
    }

    /**
     * Gets the camera of this player.
     *
     * @return the camera.
     */
    @NotNull
    public Camera getCamera() {
        return this.camera;
    }

    /**
     * Gets the location of this player.
     *
     * @return the location.
     */
    @NotNull
    public Location getLocation() {
        return this.camera.getLocation();
    }

    /**
     * Gets the target location of this player.
     *
     * @param maxDistance the max distance of the block.
     * @return the result of the target, or if the distance is greater than the max distance, returns null.
     */
    @Nullable
    public Camera.Result getTargetLocation(int maxDistance) {
        Camera.Result result = this.camera.getTarget();
        return result.getDistance() > maxDistance ? null : result;
    }

}
