package art.sylleth.syllesia.platform;

import art.sylleth.syllesia.misc.Misc;

public class Location {

    private final double xPos,
            yPos,
            xDir,
            yDir,
            xPlane,
            yPlane;

    /**
     * Creates a new location with the given coordinates.
     *
     * @param x the x position.
     * @param y the y position.
     */
    public Location(double x, double y) {
        this(x, y, 0, 0, 0, 0);
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
        this(x, y, xDir, yDir, 0, 0);
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
        this.xPos = x;
        this.yPos = y;
        this.xDir = xDir;
        this.yDir = yDir;
        this.xPlane = xPlane;
        this.yPlane = yPlane;
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
     * Creates an easily readable string version of this location, containing only the X and Y positions.
     * To get more information through a string, refer to the {@see Location#toString()} method.
     *
     * @return the location as a readable string.
     */
    public String stringify() {
        return Misc.toNPoints(this.xPos, 3) + ", " + Misc.toNPoints(this.yPos, 3);
    }

    @Override
    public String toString() {
        return "Location[x=" + this.xPos + ",\ny=" + this.yPos + ",\nxDir=" + this.xDir + ",\nyDir=" + this.yDir + ",\nxPlane=" + this.xPlane + ",\nyPlane=" + this.yPlane + "]";
    }

}
