package art.sylleth.syllesia.platform.game;

import art.sylleth.syllesia.Syllesia;
import art.sylleth.syllesia.api.configs.Settings;
import art.sylleth.syllesia.api.conversation.Dialogue;
import art.sylleth.syllesia.api.events.PlayerJoinEvent;
import art.sylleth.syllesia.entities.Player;
import art.sylleth.syllesia.files.ConfigurationFile;
import art.sylleth.syllesia.misc.Misc;
import art.sylleth.syllesia.api.world.Location;
import art.sylleth.syllesia.misc.Pair;
import art.sylleth.syllesia.platform.screen.Screen;
import art.sylleth.syllesia.platform.textures.Texture;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.List;

/**
 * Main class for controlling the main platform of this game.
 */
public class Platform extends JFrame implements Runnable {

    private static final int SCREEN_WIDTH = 720;
    private static final int SCREEN_HEIGHT = 520;

    private final Thread thread;
    private final BufferedImage image;
    private boolean active;
    private final int[] pixels;
    private final Player player;
    private final Camera camera;
    private final Screen screen;
    private final Canvas game = new Canvas();

    private final BufferedImage coinIcon = Misc.getResourceImage("game/icons/coin.png");
    private final BufferedImage dialogue = Misc.getResourceImage("game/misc/dialogue.png");

    /**
     * Creates and launches a new platform for Syllesia to run on.
     */
    public Platform(Player player) {
        this.thread = new Thread(this);
        this.image = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        this.pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        this.player = player;
        this.camera = this.player.getCamera();
        this.screen = new Screen(SCREEN_WIDTH, SCREEN_HEIGHT);
        this.game.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        this.game.addKeyListener(this.camera);
        this.game.addMouseListener(this.camera);
        this.getLayeredPane().add(game, JLayeredPane.DEFAULT_LAYER);
        this.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        this.setLayout(null);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setTitle("[ Syllesia ]");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowListener() {

            // TODO this is not called when right clicking the taskbar and exiting the application.
            @Override
            public void windowClosing(WindowEvent event) {
                Syllesia.getInstance().getLogger().debug("WindowClosingEvent fired. Saving Configurations.");
                for (String id : new String[]{ConfigurationFile.USERDATA, ConfigurationFile.SETTINGS, ConfigurationFile.MAPDATA/*, ConfigurationFile.LANG*/}) { // TODO lang file.
                    Syllesia.getInstance().getLogger().debug("Saving configuration file " + id + ".");
                    ConfigurationFile file = Syllesia.getInstance().getConfiguration(id);
                    file.writeData();
                    Syllesia.getInstance().getLogger().debug("Configuration file " + id + " saved.");
                }
                Platform.this.stop();
                System.exit(0);
            }

            // Unused methods for this window listener.
            @Override
            public void windowOpened(WindowEvent event) {}
            @Override
            public void windowClosed(WindowEvent event) {}
            @Override
            public void windowIconified(WindowEvent event) {}
            @Override
            public void windowDeiconified(WindowEvent event) {}
            @Override
            public void windowActivated(WindowEvent event) {}
            @Override
            public void windowDeactivated(WindowEvent event) {}
        });
        this.setBackground(Color.BLACK);
        this.setVisible(true);
        this.game.requestFocus();
        this.start();
        Syllesia.getInstance().getEventBus().callEvent(new PlayerJoinEvent(player));
    }

    /**
     * Draws the current image onto the screen, as well as the overlay.
     * TODO split this into different draw methods to reduce bulk.
     */
    public void render() {
        BufferStrategy buffer = this.game.getBufferStrategy();
        if (buffer == null)
            this.game.createBufferStrategy(3);
        else {
            Graphics graphics = buffer.getDrawGraphics();
            // Game
            graphics.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
            // Coin Counter
            graphics.drawImage(this.coinIcon, SCREEN_WIDTH - 125, SCREEN_HEIGHT - 100, null);
            graphics.setColor(Color.WHITE);
            graphics.setFont(new Font("monospaced", Font.BOLD, 20));
            graphics.drawString(player.getCoins() + "", SCREEN_WIDTH - 75, SCREEN_HEIGHT - 65);
            // Title Handler
            Pair<String, Double> title = player.getTitle();
            if (title.hasFirst() && title.hasSecond()) {
                if (title.getSecond() < System.currentTimeMillis())
                    player.sendTitle(null, null);
                else {
                    Font font = new Font("monospaced", Font.PLAIN, 30);
                    graphics.setFont(font);
                    FontMetrics metrics = graphics.getFontMetrics(font);
                    int x = (SCREEN_WIDTH / 2) - (metrics.stringWidth(title.getFirst()) / 2);
                    graphics.drawString(player.getTitle().getFirst(), x, SCREEN_HEIGHT / 2);
                }
            }
            // Command Bar
            if (player.isTyping()) {
                Color colour = new Color(84, 84, 84, 200);
                graphics.setColor(colour);
                graphics.fillRect(0, SCREEN_HEIGHT - 58, SCREEN_WIDTH - 100, 30);
                graphics.setColor(Color.WHITE);
                Font font = new Font("monospaced", Font.PLAIN, 20);
                graphics.setFont(font);
                FontMetrics metrics = graphics.getFontMetrics(font);
                graphics.setFont(font);
                int maxLength = 600;
                StringBuilder builder = new StringBuilder();
                String[] splitChat = player.getChat().split("");
                for (String c : splitChat) {
                    builder.insert(0, c);
                    if (metrics.stringWidth(builder.toString()) > maxLength)
                        builder.setLength(builder.length() - 1); // Since the input is reversed, removing the first characters conforms the builder to the bar length.
                }
                builder.reverse();
                graphics.drawString(builder.toString(), 5, SCREEN_HEIGHT - 38);
            }
            // Map
            Location location = player.getLocation();
            int xStart = (int) location.getX() - 2;
            int xEnd = (int) location.getX() + 2;
            int yStart = (int) location.getY() - 2;
            int yEnd = (int) location.getY() + 2;
            graphics.setColor(Misc.fromHex("#543732"));
            int MAP_WIDTH = 100;
            int MAP_HEIGHT = 100;
            int block = MAP_WIDTH / 5; // These are squares.
            int pointer = block / 4;
            graphics.fillRect(SCREEN_WIDTH - 100, 0, MAP_WIDTH, MAP_HEIGHT);
            int xOffset = 0;
            for (int x = xStart; x <= xEnd; x++) {
                int yOffset = 0;
                for (int y = yStart; y <= yEnd; y++) {
                    Texture texture;
                    try {
                        texture = Texture.fromId(location.getMap().getMatrix()[x][y]);
                    } catch (ArrayIndexOutOfBoundsException ignored) {
                        texture = null;
                    }
                    if (texture == null) {
                        graphics.setColor(Misc.fromHex("#0b0b0b"));
                        graphics.fillRect(SCREEN_WIDTH - MAP_WIDTH + xOffset, yOffset, block, block);
                    } else if (texture.getId() == 0) { // Air
                        graphics.setColor(Misc.fromHex("704F38"));
                        graphics.fillRect(SCREEN_WIDTH - MAP_WIDTH + xOffset, yOffset, block, block);
                    } else
                        graphics.drawImage(texture.getImage(), SCREEN_WIDTH - MAP_WIDTH + xOffset, yOffset, block, block, null);
                    if (x == (int) location.getX() && y == (int) location.getY()) {
                        graphics.setColor(Color.RED);
                        graphics.fillOval(SCREEN_WIDTH - MAP_WIDTH + xOffset + (block / 2) - (pointer / 2), yOffset + (block / 2) - (pointer / 2), pointer, pointer);
                    }
                    yOffset += 20;
                }
                xOffset += 20;
            }
            // Cursor
            graphics.setColor(Color.RED);
            graphics.fillOval(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2, 5, 5);
            // Location
            Settings settings = (Settings) Syllesia.getInstance().getConfiguration(ConfigurationFile.SETTINGS);
            graphics.setFont(new Font("monospaced", Font.PLAIN, 10));
            graphics.setColor(Color.WHITE);
            graphics.drawString("[" + Misc.toNPoints(location.getX(), 3) + ", " + Misc.toNPoints(location.getY(), 3) + "]", 5, 15);
            if (settings.isDebug()) {
                graphics.drawString("D[" + Misc.toNPoints(location.getXDir(), 3) + ", " + Misc.toNPoints(location.getYDir(), 3) + "]", 5, 25);
                graphics.drawString("P[" + Misc.toNPoints(location.getXPlane(), 3) + ", " + Misc.toNPoints(location.getYPlane(), 3) + "]", 5, 35);
            }
            // Conversation Display.
            Dialogue dialogue = this.player.getOpenDialogue();
            if (dialogue != null) {
                if (!dialogue.isValid())
                    player.openDialogue(null, null); // Don't continue trying to draw an invalid dialogue.
                else {
                    // Dialogue Background
                    Color shadow = new Color(84, 84, 84, 220);
                    graphics.setColor(shadow);
                    graphics.drawRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT); // Darkens the game behind the open dialogue.
                    graphics.drawImage(this.dialogue, 150, 20, null);
                    // Title
                    graphics.setColor(Color.WHITE);
                    graphics.setFont(new Font("monospaced", Font.BOLD, 20));
                    String dTitle = dialogue.getTitle();
                    if (dTitle != null)
                        graphics.drawString(dTitle, 180, 45);
                    // Content
                    graphics.setColor(Color.LIGHT_GRAY);
                    Font font = new Font("monospaced", Font.PLAIN, 12);
                    graphics.setFont(font);
                    FontMetrics metrics = graphics.getFontMetrics(font);
                    int lineLength = 326;
                    List<String> lines = new ArrayList<>();
                    StringBuilder lineBuilder = new StringBuilder();
                    for (String c : dialogue.getContent().split("")) {
                        lineBuilder.append(c);
                        if (metrics.stringWidth(lineBuilder.toString()) > lineLength) {
                            lines.add(lineBuilder.toString());
                            lineBuilder.setLength(0);
                        }
                    }
                    if (!lineBuilder.isEmpty())
                        lines.add(lineBuilder.toString());
                    int yMod = 0;
                    for (String line : lines) {
                        graphics.drawString(line, 190, 75 + yMod);
                        yMod += 20;
                    }
                    // Options
                    String[] choices = dialogue.getChoices();
                    yMod = 0;
                    for (String choice : choices) {
                        graphics.drawString(choice, 192, 318 + yMod);
                        yMod += 31;
                    }
                }
            }
            graphics.dispose();
            buffer.show();
        }
    }

    @Override
    public void run() {
        long last = System.nanoTime();
        Settings settings = (Settings) Syllesia.getInstance().getConfiguration(ConfigurationFile.SETTINGS);
        double interval = 1000000000.0 / settings.getFps();
        double delta = 0;
        while (this.isActive()) {
            long now = System.nanoTime();
            delta += (now - last) / interval;
            last = now;
            while (delta >= 1) {
                this.screen.update(this.player.getCamera(), this.pixels);
                this.camera.update(player.getLocation().getMap().getMatrix());
                delta--;
            }
            this.render();
        }
    }

    /**
     * Gets the player this platform is in control of.
     *
     * @return the main player.
     */
    @NotNull
    public Player getMainPlayer() {
        return this.player;
    }

    /**
     * Checks if this platform is active.
     *
     * @return true if active, else false.
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Starts the thread running this game.
     */
    private synchronized void start() {
        this.active = true;
        thread.start();
    }

    /**
     * Terminates the thread and stops the game.
     */
    private synchronized void stop() {
        this.active = false;
        try {
            thread.join();
        } catch (InterruptedException ex) {
            Syllesia.getInstance().getLogger().error(ex, Platform.class, 311);
        }
    }

}
