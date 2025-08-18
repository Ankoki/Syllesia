package art.sylleth.syllesia.platform.game;

import art.sylleth.syllesia.Syllesia;
import art.sylleth.syllesia.api.conversation.Conversation;
import art.sylleth.syllesia.api.conversation.Dialogue;
import art.sylleth.syllesia.api.events.PlayerCommandProcessEvent;
import art.sylleth.syllesia.api.events.PlayerInteractEvent;
import art.sylleth.syllesia.api.world.Map;
import art.sylleth.syllesia.entities.Player;
import art.sylleth.syllesia.api.world.Location;
import art.sylleth.syllesia.misc.Timespan;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Class which is used to control the players view, handling the majority of the raycasting.
 * TODO fix entity and model rendering.
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
            back,
            shift;

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
        Player player = Syllesia.getInstance().getPlatform().getMainPlayer();
        if (player.isTyping() && event.getKeyCode() != KeyEvent.VK_ENTER) {
            if (event.getKeyCode() == KeyEvent.VK_SHIFT)
                return;
            else if (event.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                player.deleteLast();
                return;
            }
            char key = event.getKeyChar();
            if (shift)
                key = Character.toUpperCase(key);
            player.appendChat(String.valueOf(key));
            return;
        }
        // Movement
        if (event.getKeyCode() == KeyEvent.VK_LEFT || event.getKeyCode() == KeyEvent.VK_A)
            this.left = true;
        if (event.getKeyCode() == KeyEvent.VK_RIGHT || event.getKeyCode() == KeyEvent.VK_D)
            this.right = true;
        if (event.getKeyCode() == KeyEvent.VK_UP || event.getKeyCode() == KeyEvent.VK_W)
            this.forward = true;
        if (event.getKeyCode() == KeyEvent.VK_DOWN || event.getKeyCode() == KeyEvent.VK_S)
            this.back = true;
        if (event.getKeyCode() == KeyEvent.VK_SHIFT)
            this.shift = true;
        // Command Handler
        if (event.getKeyCode() == KeyEvent.VK_ENTER) {
            if (player.isTyping()) {
                String chat = player.getChat();
                if (chat.isBlank()) {
                    player.clearChat();
                    player.setTyping(false);
                    return;
                }
                String[] split = chat.split(" ");
                String command = split[0];
                PlayerCommandProcessEvent commandEvent = new PlayerCommandProcessEvent(player, command, new String[0]);
                if (split.length == 1) {
                    Syllesia.getInstance().getEventBus().callEvent(commandEvent);
                    if (!commandEvent.isPrevented()) {
                        if (!Syllesia.getInstance().getCommandHandler().runCommand(player, command, new String[0]))
                            player.sendTitle("Invalid command [" + command + "]", Timespan.of("2 seconds"));
                        else
                            Syllesia.getInstance().getLogger().debug(player.getName() + " ran the command [" + command + "]");
                    }
                } else {
                    String[] args = new String[split.length - 1];
                    int i = 0;
                    boolean isFirst = true;
                    for (String string : split) {
                        if (isFirst) {
                            isFirst = false;
                            continue;
                        }
                        args[i] = string;
                        i++;
                    }
                    commandEvent = new PlayerCommandProcessEvent(player, command, args);
                    Syllesia.getInstance().getEventBus().callEvent(commandEvent);
                    if (!commandEvent.isPrevented()) {
                        if (!Syllesia.getInstance().getCommandHandler().runCommand(player, command, args))
                            player.sendTitle("Invalid command [" + command + " " + String.join(" ", args) + "]", Timespan.of("2 seconds"));
                        else
                            Syllesia.getInstance().getLogger().debug(player.getName() + " ran the command [" + command + " " + String.join(" ", args) + "]");
                    }
                }
                player.clearChat();
                player.setTyping(false);
            } else {
                player.clearChat(); // Just in case.
                player.setTyping(true);
            }
        }
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
        if (event.getKeyCode() == KeyEvent.VK_SHIFT)
            this.shift = false;
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
     * Updates the player's view.
     *
     * @param world the world.
     */
    public void update(int[][] world) {
        Dialogue dialogue = Syllesia.getInstance().getPlatform().getMainPlayer().getOpenDialogue();
        if (dialogue != null)
            return;
        double moveSpeed = Camera.MOVE_SPEED * (this.shift ? 2 : 1); // Shift sprint logic.
        // Doesn't fix drift entirely, however makes it a little bit closer to intended movement.
        double driftFix = this.xDir < 0 ? 0.15 : -0.15;
        if (this.forward) {
            double x = this.xPos + (this.xDir + driftFix) * moveSpeed;
            double y = this.yPos + (this.yDir + driftFix) * moveSpeed;
            if (world[(int) x][(int) this.yPos] == 0) // 0 is the id of air in the world.
                this.xPos = x;
            if (world[(int) this.xPos][(int) y] == 0)
                this.yPos = y;
        }
        if (this.back) {
            double x = this.xPos - (this.xDir + driftFix) * moveSpeed;
            double y = this.yPos - (this.yDir + driftFix) * moveSpeed;
            if (world[(int) x][(int) this.yPos] == 0)
                this.xPos = x;
            if (world[(int) this.xPos][(int) y] == 0)
                this.yPos = y;
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
    @Nullable
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
        PlayerInteractEvent.ClickType type;
        Player player = Syllesia.getInstance().getPlatform().getMainPlayer();
        Dialogue dialogue = player.getOpenDialogue();
        // Dialogue Interaction
        if (dialogue != null && event.getButton() == MouseEvent.BUTTON1) { // No dialogue functionality for middle or right click.
            int leftX = 190;
            int rightX = 520;
            int tbStep = 15;
            int oStep = 20;
            int currentY = 305;
            Syllesia.getInstance().getLogger().debug("Click at [" + event.getX() + ", " + event.getY() + "]");
            Location location = new Location(event.getX(), event.getY());
            String[] choices = dialogue.getChoices();
            for (int i = 0; i < choices.length; i++) {
                int stepY = currentY + tbStep;
                if (location.isWithin(leftX, currentY, rightX, stepY)) {
                    String pointer = dialogue.getPointer(dialogue.getChoices()[i]);
                    Conversation conversation = player.getCurrentConversation();
                    if (pointer.equals(Dialogue.EXIT_POINTER))
                        player.openDialogue(null, null);
                    else
                        player.openDialogue(conversation, conversation.getDialogue(pointer));
                    Syllesia.getInstance().getLogger().debug("Option " + (i + 1) + " clicked.");
                    return;
                }
                currentY += tbStep + oStep;
            }
            return;
        }
        // World Interaction
        switch (event.getButton()) {
            case MouseEvent.BUTTON1:
                Location target = player.getTargetLocation(3);
                if (target == null)
                    return;
                type = shift ? PlayerInteractEvent.ClickType.SHIFT_LEFT : PlayerInteractEvent.ClickType.LEFT;
                PlayerInteractEvent playerInteractEvent = new PlayerInteractEvent(player, player.getTargetLocation(3), type);
                Syllesia.getInstance().getEventBus().callEvent(playerInteractEvent);
                if (playerInteractEvent.isPrevented())
                    return;
                else {
                    Syllesia.getInstance().getLogger().debug("MouseEvent.BUTTON1[LEFT] " + playerInteractEvent.getName() + ", Location: " + playerInteractEvent.getLocation());
                    if (target.getTexture().getId() == 6) {
                        player.addCoins(1);
                        player.sendTitle("You have received a coin.", Timespan.of("2 seconds"));
                        Syllesia.getInstance().getLogger().debug("Player[" + player.getName() + "] has received a coin[" + player.getCoins() + "].");
                    }
                }
                break;
            case MouseEvent.BUTTON2:
                // Middle Click, no needed functionality at this time.
                break;
            case MouseEvent.BUTTON3:
                type = shift ? PlayerInteractEvent.ClickType.SHIFT_RIGHT : PlayerInteractEvent.ClickType.RIGHT;
                PlayerInteractEvent newEvent = new PlayerInteractEvent(player, player.getTargetLocation(3), type);
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
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Class to store the result of a target block raycast.
     */
    public static class Result extends Location {

        /**
         * Creates a new raycast result with the given options.
         *
         * @param x        the x location.
         * @param y        the y location.
         * @param map      the map of this location.
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
