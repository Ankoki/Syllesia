package art.sylleth.syllesia.platform.game;

import art.sylleth.syllesia.platform.Location;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Camera implements KeyListener {

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
    public void keyTyped(KeyEvent e) {
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
     * Updates the world as we see it.
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
            double xPlaneCopy = this.xPlane;
            this.xPlane = this.xPlane * Math.cos(ROTATE_SPEED) - this.yPlane * Math.sin(ROTATE_SPEED);
            this.yPlane = xPlaneCopy * Math.sin(ROTATE_SPEED) + this.yPlane * Math.cos(ROTATE_SPEED);
        }

        if (this.right) {
            double xDirCopy = this.xDir;
            this.xDir = this.xDir * Math.cos(-ROTATE_SPEED) - this.yDir * Math.sin(-ROTATE_SPEED);
            this.yDir = xDirCopy * Math.sin(-ROTATE_SPEED) + this.yDir * Math.cos(-ROTATE_SPEED);
            double xPlaneCopy = this.xPlane;
            this.xPlane = this.xPlane * Math.cos(-ROTATE_SPEED) - this.yPlane * Math.sin(-ROTATE_SPEED);
            this.yPlane = xPlaneCopy * Math.sin(-ROTATE_SPEED) + this.yPlane * Math.cos(-ROTATE_SPEED);
        }

    }

    /**
     * Gets the current location of this camera.
     *
     * @return the location.
     */
    public Location getLocation() {
        return new Location(this.xPos, this.yPos, this.xDir, this.yDir, this.xPlane, this.yPlane);
    }

}
