package art.sylleth.syllesia.entities;

import art.sylleth.syllesia.Syllesia;
import art.sylleth.syllesia.api.configs.Userdata;
import art.sylleth.syllesia.files.ConfigurationFile;
import art.sylleth.syllesia.api.world.Location;
import art.sylleth.syllesia.platform.game.Camera;
import art.sylleth.syllesia.platform.textures.Texture;
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
    private final Userdata userdata = (Userdata) Syllesia.getInstance().getConfiguration(ConfigurationFile.USERDATA);

    /**
     * Creates a new player object with the given details.
     * UUID's are unused as of now, however we want to leave room for expansion to multi-player.
     *
     * @param name the name of the player.
     * @param uuid the UUID of the player.
     * @param camera the camera that this player will be seeing through.
     */
    public Player(String name, UUID uuid, Camera camera) {
        this.name = name;
        this.uuid = uuid;
        this.camera = camera;
    }

    /**
     * Moves this player to a new location.
     * Will not move the player if the location is invalid, or not air.
     *
     * @param location the new location.
     * @return true if successful, else false.
     */
    public boolean moveTo(Location location) {
        int[][] mapMatrix = location.getMap().getMatrix();
        try {
            if (mapMatrix[(int) location.getX()][(int) location.getY()] == Texture.AIR.getId()) {
                this.camera.moveTo(location);
                return true;
            } else
                return false;
        } catch (ArrayIndexOutOfBoundsException ignored) {
            return false; // X, Y location doesn't exist on the map.
        }
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
     * Gets the amount of coins this player has.
     *
     * @return the coin amount.
     */
    public double getCoins() {
        return this.userdata.getCoins();
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

    /**
     * Adds coins to this player.
     *
     * @param amount the amount of coins to give the player.
     */
    public void addCoins(double amount) {
        this.userdata.setCoins(this.getCoins() + amount);
    }

    /**
     * Removes coins from the player.
     * If the current amount of coins minus the amount equates to below 0, 0 will be used.
     *
     * @param amount the amount of coins to remove from the player.
     */
    public void removeCoins(double amount) {
        this.userdata.setCoins(Math.max(0, this.userdata.getCoins() - amount));
    }

    /**
     * Sets the amount of coins this player should have.
     * If below 0, 0 will be used.
     *
     * @param amount the new amount of coins.
     */
    public void setCoins(double amount) {
        this.userdata.setCoins(Math.max(0, amount));
    }

    /**
     * Sends a title message to the player.
     *
     * @param text the text to show.
     */
    public void sendTitle(String text) {
        // TODO title mechanics.
    }

}
