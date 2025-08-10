package art.sylleth.syllesia.platform.game;

import art.sylleth.syllesia.Syllesia;
import art.sylleth.syllesia.api.configs.Settings;
import art.sylleth.syllesia.entities.Player;
import art.sylleth.syllesia.files.ConfigurationFile;
import art.sylleth.syllesia.misc.Misc;
import art.sylleth.syllesia.api.world.Location;
import art.sylleth.syllesia.platform.screen.Screen;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * Main class for controlling the main platform of this game.
 */
public class Platform extends JFrame implements Runnable {

    private static final int SCREEN_WIDTH = 720;
    private static final int SCREEN_HEIGHT = 520;
    private static final int OVERLAY_INDEX = 99;

    private final Thread thread;
    private final BufferedImage image;
    private boolean active;
    private final int[] pixels;
    private final Player player;
    private final Camera camera;
    private final Screen screen;
    private final Canvas game = new Canvas();
    private final JLayeredPane pane = this.getLayeredPane();

    // Overlay components.
    private final JLabel location = new JLabel();
    private final JLabel coins = new JLabel();
    private final JPanel map = new JPanel();

    // Debug panel components.
    private JPanel debugPanel;
    private JLabel coords,
            xCoord,
            yCoord,
            xDir,
            yDir,
            xPlane,
            yPlane;

    /**
     * Creates and launches a new platform for Syllesia to run on.
     */
    public Platform(Player player) {
        this.thread = new Thread(this);
        this.image = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        this.pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        this.player = player;
        this.camera = this.player.getCamera();
        this.screen = new Screen(this.player.getLocation().getMap().getMatrix(), SCREEN_WIDTH, SCREEN_HEIGHT);
        this.game.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        this.game.addKeyListener(this.camera);
        this.game.addMouseListener(this.camera);
        this.pane.add(game, JLayeredPane.DEFAULT_LAYER);
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
                for (String id : new String[]{ConfigurationFile.USERDATA, ConfigurationFile.SETTINGS/*, ConfigurationFile.LANG*/}) { // TODO lang file.
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
        this.setBackground(Color.LIGHT_GRAY);
        this.setupOverlay();
        Settings settings = (Settings) Syllesia.getInstance().getConfiguration(ConfigurationFile.SETTINGS);
        if (settings.isDebug())
            this.initiateDebug();
        this.setVisible(true);
        this.start();
    }

    /**
     * Initiates the debug method. Allows us to see the game options easier.
     */
    private void initiateDebug() {
        Syllesia.getInstance().getLogger().debug("Enabling debug screen.");
        Location location = this.camera.getLocation();
        this.coords = new JLabel("Location: " + location.stringify());
        this.xCoord = new JLabel("X: " + Misc.toNPoints(location.getX(), 3));
        this.yCoord = new JLabel("Y: " + Misc.toNPoints(location.getY(), 3));
        this.xDir = new JLabel("XDir: " + Misc.toNPoints(location.getXDir(), 3));
        this.yDir = new JLabel("YDir: " + Misc.toNPoints(location.getYDir(), 3));
        this.xPlane = new JLabel("XPln: " + Misc.toNPoints(location.getXPlane(), 3));
        this.yPlane = new JLabel("YPln: " + Misc.toNPoints(location.getYPlane(), 3));
        this.coords.setBounds(0, 0, 215, 20);
        this.xCoord.setBounds(0, 25, 150, 20);
        this.yCoord.setBounds(0, 50, 150, 20);
        this.xDir.setBounds(0, 75, 150, 20);
        this.yDir.setBounds(0, 100, 150, 20);
        this.xPlane.setBounds(0, 125, 150, 20);
        this.yPlane.setBounds(0, 150, 150, 20);
        this.coords.setForeground(Color.DARK_GRAY);
        this.xCoord.setForeground(Color.DARK_GRAY);
        this.yCoord.setForeground(Color.DARK_GRAY);
        this.xDir.setForeground(Color.DARK_GRAY);
        this.yDir.setForeground(Color.DARK_GRAY);
        this.xPlane.setForeground(Color.DARK_GRAY);
        this.yPlane.setForeground(Color.DARK_GRAY);
        this.debugPanel = new JPanel();
        this.debugPanel.setLayout(null);
        this.debugPanel.setBounds(0, 0, 215, 175);
        this.debugPanel.add(coords);
        this.debugPanel.add(xCoord);
        this.debugPanel.add(yCoord);
        this.debugPanel.add(xDir);
        this.debugPanel.add(yDir);
        this.debugPanel.add(xPlane);
        this.debugPanel.add(yPlane);
        this.debugPanel.setDoubleBuffered(true);
        this.debugPanel.setBackground(Color.orange);
        this.pane.add(debugPanel, JLayeredPane.PALETTE_LAYER);
    }

    /**
     * Sets up the games overlay.
     */
    private void setupOverlay() {
        Settings settings = (Settings) Syllesia.getInstance().getConfiguration(ConfigurationFile.SETTINGS);
        if (!settings.isDebug()) {
            // Location, if debug is enabled then there will be a more in depth location shown.
            this.location.setBounds(0, 0, 215, 50);
            this.location.setText(this.player.getLocation().stringify());
            this.pane.add(this.location, JLayeredPane.PALETTE_LAYER);
        }
        // Coin Counter
        this.coins.setBounds(SCREEN_WIDTH - 100, SCREEN_HEIGHT - 75, 100, 50);
        ImageIcon coinIcon = new ImageIcon(Misc.getResourceImage("textures/icons/coin.png"));
        this.coins.setIcon(coinIcon);
        this.coins.setIconTextGap(10);
        this.coins.setText(this.player.getCoins() + "");
        this.coins.setOpaque(false);
        this.pane.add(this.coins, JLayeredPane.PALETTE_LAYER);
        // Map TODO
        GridLayout gridLayout = new GridLayout(5, 5);
        this.map.setLayout(gridLayout);
        Location location = this.player.getLocation();
        int x = (int) location.getX();
        int y = (int) location.getY();
        /*
        0 0 0 0 0
        0 0 0 0 0
        0 0 X 0 0
        0 0 0 0 0
        0 0 0 0 0
         */
        // Quest Toast TODO
    }

    /**
     * Updates the overlay components.
     */
    private void updateOverlay() {
        this.location.setText(this.player.getLocation().stringify());
        this.coins.setText(this.player.getCoins() + "");
    }

    /**
     * Updates the debug menu. If debug is disabled, nothing will happen.
     */
    private void updateDebug() {
        Settings settings = (Settings) Syllesia.getInstance().getConfiguration(ConfigurationFile.SETTINGS);
        if (!settings.isDebug())
            return;
        Location location = this.camera.getLocation();
        this.coords.setText("Location: " + location.stringify());
        this.xCoord.setText("X: " + Misc.toNPoints(location.getX(), 3));
        this.yCoord.setText("Y: " + Misc.toNPoints(location.getY(), 3));
        this.xDir.setText("XDir: " + Misc.toNPoints(location.getXDir(), 3));
        this.yDir.setText("YDir: " + Misc.toNPoints(location.getYDir(), 3));
        this.xPlane.setText("XPln: " + Misc.toNPoints(location.getXPlane(), 3));
        this.yPlane.setText("YPln: " + Misc.toNPoints(location.getYPlane(), 3));
    }

    /**
     * Draws the current image onto the screen.
     */
    public void render() {
        BufferStrategy buffer = this.game.getBufferStrategy();
        if (buffer == null)
            this.game.createBufferStrategy(3);
        else {
            Graphics graphics = buffer.getDrawGraphics();
            graphics.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
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
                this.updateOverlay();
                this.updateDebug();
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
    public synchronized void stop() {
        this.active = false;
        try {
            thread.join();
        } catch (InterruptedException ex) {
            Syllesia.getInstance().getLogger().error(ex, Platform.class, 203);
        }
    }

}
