package art.sylleth.syllesia.api.world;

import art.sylleth.syllesia.Syllesia;
import art.sylleth.syllesia.files.json.JSONSerializable;
import art.sylleth.syllesia.misc.Misc;
import art.sylleth.syllesia.platform.textures.Texture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * Class to easier store locations.
 */
public class Location extends JSONSerializable {

    /**
     * Creates a location from map data.
     * Required by {@link JSONSerializable}.
     *
     * @param data the data to parse.
     * @return the location, or if invalid, null.
     */
    @NotNull
    public static Location deserialize(java.util.Map<String, Object> data) {
        for (String key : new String[]{"x-pos", "y-pos", "map-id"})
            if (!data.containsKey(key))
                throw new IllegalArgumentException("Required key '" + key + "' not found.");
        double xPos = (double) data.get("x-pos");
        double yPos = (double) data.get("y-pos");
        int mapId = (int) data.get("map-id");
        Map map = Syllesia.getInstance().getMap(mapId);
        if (map == null)
            map = Syllesia.getInstance().getBaseMap(); // Default to regular world.
        double xDir = 0;
        double yDir = 0;
        double xPlane = 0;
        double yPlane = 0;
        if (data.containsKey("x-dir"))
            xDir = (double) data.get("x-dir");
        if (data.containsKey("y-dir"))
            yDir = (double) data.get("y-dir");
        if (data.containsKey("x-plane"))
            xPlane = (double) data.get("x-plane");
        if (data.containsKey("y-plane"))
            yPlane = (double) data.get("y-plane");
        return new Location(xPos, yPos, xDir, yDir, xPlane, yPlane, map);
    }

    private final double xPos,
            yPos,
            xDir,
            yDir,
            xPlane,
            yPlane;
    private final Map map;

    /**
     * Creates a new location with the given coordinates.
     *
     * @param x the x position.
     * @param y the y position.
     */
    public Location(double x, double y) {
        this(x, y, 0, 0, 0, 0, Syllesia.getInstance().getBaseMap());
    }

    /**
     * Creates a new location with the given coordinates.
     *
     * @param x the x position.
     * @param y the y position.
     * @param map the map.
     */
    public Location(double x, double y, Map map) {
        this(x, y, 0, 0, 0, 0, map);
    }

    /**
     * Creates a new location with the given coordinates.
     *
     * @param x the x position.
     * @param y the y position.
     * @param xDir the x direction.
     * @param yDir the y direction.
     */
    public Location(double x, double y, double xDir, double yDir) {
        this(x, y, xDir, yDir, 0, 0, Syllesia.getInstance().getBaseMap());
    }

    /**
     * Creates a new location with the given coordinates.
     *
     * @param x the x position.
     * @param y the y position.
     * @param xDir the x direction.
     * @param yDir the y direction.
     */
    public Location(double x, double y, double xDir, double yDir, Map map) {
        this(x, y, xDir, yDir, 0, 0, map);
    }

    /**
     * Creates a new location with the given coordinates.
     *
     * @param x the x position.
     * @param y the y position.
     * @param xDir the x direction.
     * @param yDir the y direction.
     * @param xPlane the x plane.
     * @param yPlane the y plane.
     */
    public Location(double x, double y, double xDir, double yDir, double xPlane, double yPlane) {
        this(x, y, xDir, yDir, xPlane, yPlane, Syllesia.getInstance().getBaseMap());

    }

    /**
     * Creates a new location with the given coordinates.
     *
     * @param x the x position.
     * @param y the y position.
     * @param xDir the x direction.
     * @param yDir the y direction.
     * @param xPlane the x plane.
     * @param yPlane the y plane.
     * @param map the map of this location.
     */
    public Location(double x, double y, double xDir, double yDir, double xPlane, double yPlane, Map map) {
        this.xPos = x;
        this.yPos = y;
        this.xDir = xDir;
        this.yDir = yDir;
        this.xPlane = xPlane;
        this.yPlane = yPlane;
        this.map = map;
    }

    /**
     * Gets the X value of this location.
     *
     * @return the x value.
     */
    public double getX() {
        return this.xPos;
    }

    /**
     * Gets the Y value of this location.
     *
     * @return the y value.
     */
    public double getY() {
        return this.yPos;
    }

    /**
     * Gets the X direction value of this location.
     *
     * @return the x direction value.
     */
    public double getXDir() {
        return this.xDir;
    }

    /**
     * Gets the Y direction value of this location.
     *
     * @return the y direction value.
     */
    public double getYDir() {
        return this.yDir;
    }

    /**
     * Gets the X plane value of this location.
     *
     * @return the x plane value.
     */
    public double getXPlane() {
        return this.xPlane;
    }

    /**
     * Gets the Y plane value of this location.
     *
     * @return the y plane value.
     */
    public double getYPlane() {
        return this.yPlane;
    }

    /**
     * Gets the map of this location.
     *
     * @return the map.
     */
    @NotNull
    public Map getMap() {
        return this.map;
    }

    /**
     * Gets the texture at this location.
     *
     * @return gets the texture at this location.
     */
    @NotNull
    public Texture getTexture() {
        return Texture.fromId(this.map.getMatrix()[(int) this.xPos][(int) this.yPos]);
    }

    /**
     * Creates an easily readable string version of this location, containing only the X and Y positions.
     * To get more information through a string, refer to the {@see Location#toString()} method.
     *
     * @return the location as a readable string.
     */
    public String stringify() {
        return Misc.toNPoints(this.xPos, 3) + ", " + Misc.toNPoints(this.yPos, 3) + " [" + this.map.getName() + "]";
    }

    @Override
    public String toString() {
        return "Location[x=" + this.xPos + ",\ny=" + this.yPos + ",\nxDir=" + this.xDir + ",\nyDir=" + this.yDir + ",\nxPlane=" + this.xPlane + ",\nyPlane=" + this.yPlane + ",\nmap=" + this.map.getName() + "]";
    }

    @Override
    public java.util.Map<String, Object> serialize() {
        java.util.Map<String, Object> map = new HashMap<>();
        map.put("x-pos", this.xPos);
        map.put("y-pos", this.yPos);
        map.put("x-dir", this.xDir);
        map.put("y-dir", this.yDir);
        map.put("x-plane", this.xPlane);
        map.put("y-plane", this.yPlane);
        map.put("map-id", this.map.getId());
        return map;
    }

}
