package art.sylleth.syllesia.platform.game;

import art.sylleth.syllesia.Syllesia;
import art.sylleth.syllesia.api.events.PlayerInteractEvent;
import art.sylleth.syllesia.api.world.Map;
import art.sylleth.syllesia.entities.Player;
import art.sylleth.syllesia.api.world.Location;
import org.jetbrains.annotations.NotNull;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Class which is used to control the players view, handling the majority of the raycasting.
 * TODO entity and model rendering.
 */
public class Camera implements KeyListener, MouseListener {

    private static final double MOVE_SPEED = 0.1;
    private static final double ROTATE_SPEED = 0.05;

    private double xPos,
            yPos,
            xDir,
            yDir,
            xPlane,
            yPlane;
    private boolean left,
            right,
            forward,
            back;

    /**
     * Creates a new camera at the given location.
     *
     * @param location the location to position this camera.
     */
    public Camera(Location location) {
        this.xPos = location.getX();
        this.yPos = location.getY();
        this.xDir = location.getXDir();
        this.yDir = location.getYDir();
        this.xPlane = location.getXPlane();
        this.yPlane = location.getYPlane();
    }

    @Override
    public void keyTyped(KeyEvent event) {
        // TODO inventory interactions here?
    }

    @Override
    public void keyPressed(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.VK_LEFT || event.getKeyCode() == KeyEvent.VK_A)
            this.left = true;
        if (event.getKeyCode() == KeyEvent.VK_RIGHT || event.getKeyCode() == KeyEvent.VK_D)
            this.right = true;
        if (event.getKeyCode() == KeyEvent.VK_UP || event.getKeyCode() == KeyEvent.VK_W)
            this.forward = true;
        if (event.getKeyCode() == KeyEvent.VK_DOWN || event.getKeyCode() == KeyEvent.VK_S)
            this.back = true;
    }

    @Override
    public void keyReleased(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.VK_LEFT || event.getKeyCode() == KeyEvent.VK_A)
            this.left = false;
        if (event.getKeyCode() == KeyEvent.VK_RIGHT || event.getKeyCode() == KeyEvent.VK_D)
            this.right = false;
        if (event.getKeyCode() == KeyEvent.VK_UP || event.getKeyCode() == KeyEvent.VK_W)
            this.forward = false;
        if (event.getKeyCode() == KeyEvent.VK_DOWN || event.getKeyCode() == KeyEvent.VK_S)
            this.back = false;
    }

    /**
     * Moves this camera to a given location.
     *
     * @param location the updated location.
     */
    public void moveTo(Location location) {
        this.xPos = location.getX();
        this.yPos = location.getY();
        this.xDir = location.getXDir();
        this.yDir = location.getYDir();
        this.xPlane = location.getXPlane();
        this.yPlane = location.getYPlane();
        this.left = false;
        this.right = false;
        this.forward = false;
        this.back = false;
    }

    /**
     * Updates the world as the player sees it.
     *
     * @param world the world.
     */
    public void update(int[][] world) {
        if (this.forward) {
            int x = (int) (this.xPos + this.xDir * MOVE_SPEED);
            if (world[x][(int) this.yPos] == 0) // 0 is the id of air in the world.
                this.xPos += this.xDir * MOVE_SPEED;
            int y = (int) (this.yPos + this.yDir * MOVE_SPEED);
            if (world[(int) this.xPos][y] == 0)
                this.yPos += this.yDir * MOVE_SPEED;
        }
        if (this.back) {
            int x = (int) (this.xPos - this.xDir * MOVE_SPEED);
            if (world[x][(int) this.yPos] == 0)
                this.xPos -= this.xDir * MOVE_SPEED;
            int y = (int) (this.yPos - this.yDir * MOVE_SPEED);
            if (world[(int) this.xPos][y] == 0)
                this.yPos -= this.yDir * MOVE_SPEED;
        }
        if (this.left) {
            double xDirCopy = this.xDir;
            this.xDir = this.xDir * Math.cos(ROTATE_SPEED) - this.yDir * Math.sin(ROTATE_SPEED);
            this.yDir = xDirCopy * Math.sin(ROTATE_SPEED) + this.yDir * Math.cos(ROTATE_SPEED);
            double length = Math.sqrt(xDir * xDir + yDir * yDir);
            this.xDir /= length;
            this.yDir /= length;
            double xPlaneCopy = this.xPlane;
            this.xPlane = this.xPlane * Math.cos(ROTATE_SPEED) - this.yPlane * Math.sin(ROTATE_SPEED);
            this.yPlane = xPlaneCopy * Math.sin(ROTATE_SPEED) + this.yPlane * Math.cos(ROTATE_SPEED);
        }
        if (this.right) {
            double xDirCopy = this.xDir;
            this.xDir = this.xDir * Math.cos(-ROTATE_SPEED) - this.yDir * Math.sin(-ROTATE_SPEED);
            this.yDir = xDirCopy * Math.sin(-ROTATE_SPEED) + this.yDir * Math.cos(-ROTATE_SPEED);
            double length = Math.sqrt(xDir * xDir + yDir * yDir);
            this.xDir /= length;
            this.yDir /= length;
            double xPlaneCopy = this.xPlane;
            this.xPlane = this.xPlane * Math.cos(-ROTATE_SPEED) - this.yPlane * Math.sin(-ROTATE_SPEED);
            this.yPlane = xPlaneCopy * Math.sin(-ROTATE_SPEED) + this.yPlane * Math.cos(-ROTATE_SPEED);
        }
    }

    /**
     * Gets the location and distance in the form of a {@link Result} of this cameras target.
     *
     * @return the target.
     */
    public Result getTarget() {
        Location location = this.getLocation();
        double rayDirX = location.getXDir();
        double rayDirY = location.getYDir();
        int mapX = (int) location.getX();
        int mapY = (int) location.getY();
        double deltaDistX = Math.abs(1 / rayDirX);
        double deltaDistY = Math.abs(1 / rayDirY);
        int stepX, stepY;
        double sideDistX, sideDistY;
        if (rayDirX < 0) {
            stepX = -1;
            sideDistX = (location.getX() - mapX) * deltaDistX;
        } else {
            stepX = 1;
            sideDistX = (mapX + 1.0 - location.getX()) * deltaDistX;
        }
        if (rayDirY < 0) {
            stepY = -1;
            sideDistY = (location.getY() - mapY) * deltaDistY;
        } else {
            stepY = 1;
            sideDistY = (mapY + 1.0 - location.getY()) * deltaDistY;
        }
        int[][] map = location.getMap().getMatrix();
        int side;
        while (true) {
            if (sideDistX < sideDistY) {
                sideDistX += deltaDistX;
                mapX += stepX;
                side = 0;
            } else {
                sideDistY += deltaDistY;
                mapY += stepY;
                side = 1;
            }
            if (mapX < 0 || mapY < 0 || mapX >= map.length || mapY >= map[0].length)
                return null;
            if (map[mapX][mapY] != 0) {
                double perpWallDist;
                if (side == 0)
                    perpWallDist = (mapX - location.getX() + (1 - stepX) / 2.0) / rayDirX;
                else
                    perpWallDist = (mapY - location.getY() + (1 - stepY) / 2.0) / rayDirY;
                return new Result(mapX, mapY, location.getMap(), perpWallDist);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        switch (event.getButton()) {
            case MouseEvent.BUTTON1:
                Player player = Syllesia.getInstance().getPlatform().getMainPlayer();
                Location target = player.getTargetLocation(3);
                if (target == null)
                    return;
                PlayerInteractEvent playerInteractEvent = new PlayerInteractEvent(player, player.getTargetLocation(3), PlayerInteractEvent.ClickType.LEFT);
                Syllesia.getInstance().getEventBus().callEvent(playerInteractEvent);
                if (playerInteractEvent.isPrevented())
                    return;
                else {
                    Syllesia.getInstance().getLogger().debug("MouseEvent.BUTTON1[LEFT] " + playerInteractEvent.getName() + ", Location: " + playerInteractEvent.getLocation());
                    if (target.getTexture().getId() == 6) {
                        player.addCoins(1);
                        player.sendTitle("You have received a coin.");
                        Syllesia.getInstance().getLogger().debug("Player[" + player.getName() + "] has received a coin[" + player.getCoins() + "].");
                    }
                }
                break;
            case MouseEvent.BUTTON2:
                // Middle Click, no needed functionality at this time.
                break;
            case MouseEvent.BUTTON3:
                Player mainPlayer = Syllesia.getInstance().getPlatform().getMainPlayer();
                PlayerInteractEvent newEvent = new PlayerInteractEvent(mainPlayer, mainPlayer.getTargetLocation(3), PlayerInteractEvent.ClickType.RIGHT);
                Syllesia.getInstance().getEventBus().callEvent(newEvent);
                if (newEvent.isPrevented())
                    return;
                else {
                    Syllesia.getInstance().getLogger().debug("MouseEvent.BUTTON3[RIGHT] " + newEvent.getName() + ", Location: " + newEvent.getLocation());
                    // TODO right click functionality.
                }
                break;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    /**
     * Class to store the result of a target block raycast.
     */
    public static class Result extends Location {

        /**
         * Creates a new raycast result with the given options.
         *
         * @param x the x location.
         * @param y the y location.
         * @param map the map of this location.
         * @param distance the distance between the camera and this location.
         */
        public Result(double x, double y, Map map, double distance) {
            super(x, y, map);
            this.distance = distance;
        }

        private final double distance;

        /**
         * Gets the player's distance from the location.
         *
         * @return the distance.
         */
        public double getDistance() {
            return this.distance;
        }

    }

    /**
     * Gets the current location of this camera.
     *
     * @return the location.
     */
    @NotNull
    public Location getLocation() {
        return new Location(this.xPos, this.yPos, this.xDir, this.yDir, this.xPlane, this.yPlane);
    }

}
