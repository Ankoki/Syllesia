package art.sylleth.syllesia.platform.game;

import art.sylleth.syllesia.Syllesia;
import art.sylleth.syllesia.config.Settings;
import art.sylleth.syllesia.entities.Player;
import art.sylleth.syllesia.misc.Misc;
import art.sylleth.syllesia.platform.Location;
import art.sylleth.syllesia.platform.screen.Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Platform extends JFrame implements Runnable {

    public static final int[][] BASE_MAP = {
            {1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2},
            {1, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2},
            {1, 0, 3, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 2},
            {1, 0, 3, 0, 0, 0, 3, 0, 2, 0, 0, 0, 0, 0, 2},
            {1, 0, 3, 0, 0, 0, 3, 0, 2, 2, 2, 0, 2, 2, 2},
            {1, 0, 3, 0, 0, 0, 3, 0, 2, 0, 0, 0, 0, 0, 2},
            {1, 0, 3, 3, 0, 3, 3, 0, 2, 0, 0, 0, 0, 0, 2},
            {1, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2},
            {1, 1, 1, 1, 1, 1, 1, 1, 4, 4, 4, 0, 4, 4, 4},
            {1, 0, 0, 0, 0, 0, 1, 4, 0, 0, 0, 0, 0, 0, 4},
            {1, 0, 0, 0, 0, 0, 1, 4, 0, 0, 0, 0, 0, 0, 4},
            {1, 0, 0, 2, 0, 0, 1, 4, 0, 3, 3, 3, 3, 0, 4},
            {1, 0, 0, 0, 0, 0, 1, 4, 0, 3, 3, 3, 3, 0, 4},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4},
            {1, 1, 1, 1, 1, 1, 1, 4, 4, 4, 4, 4, 4, 4, 4}
    };

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
    private final JLayeredPane pane = this.getLayeredPane();
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
        this.screen = new Screen(BASE_MAP, SCREEN_WIDTH, SCREEN_HEIGHT);
        this.game.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        this.game.addKeyListener(this.camera);
        this.game.addMouseListener(this.camera);
        this.pane.add(game, JLayeredPane.DEFAULT_LAYER);
        this.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        this.setLayout(null);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setTitle("[ Syllesia ]");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBackground(Color.LIGHT_GRAY);
        if (Settings.DEBUG)
            this.initiateDebug();
        this.setVisible(true);
        this.start();
    }

    /**
     * Initiates the debug method. Allows us to see the game options easier.
     */
    private void initiateDebug() {
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
     * Updates the debug menu. If debug is disabled, nothing will happen.
     */
    private void updateDebug() {
        if (!Settings.DEBUG)
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
        double interval = 1000000000.0 / Settings.FRAMES_PER_SECOND;
        double delta = 0;
        while (this.isActive()) {
            long now = System.nanoTime();
            delta += (now - last) / interval;
            last = now;
            while (delta >= 1) {
                this.screen.update(this.player.getCamera(), this.pixels);
                this.camera.update(Platform.BASE_MAP);
                if (Settings.DEBUG)
                    this.updateDebug();
                delta--;
            }
            this.render();
        }
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
