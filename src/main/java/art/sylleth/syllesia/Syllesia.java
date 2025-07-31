package art.sylleth.syllesia;

import art.sylleth.syllesia.api.world.Map;
import art.sylleth.syllesia.entities.Player;
import art.sylleth.syllesia.handlers.event.EventBus;
import art.sylleth.syllesia.misc.Logger;
import art.sylleth.syllesia.platform.Location;
import art.sylleth.syllesia.platform.game.Camera;
import art.sylleth.syllesia.platform.game.Platform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The main class used to run this application.
 */
public class Syllesia {

    private static Syllesia instance;

    /**
     * Gets the current instance of Syllesia.
     *
     * @return the instance of Syllesia.
     * @throws IllegalStateException if Syllesia hasn't been initialised yet.
     */
    public static Syllesia getInstance() {
        if (Syllesia.instance == null)
            throw new IllegalStateException("Syllesia not initialized.");
        return Syllesia.instance;
    }

    /**
     * The main method to start Syllesia.
     *
     * @param args N/A
     */
    public static void main(String[] args) {
        Syllesia.instance = new Syllesia();
        // Load textures before the platform starts.
        // We can safely ignore any thrown exception, as if the texture class isn't in the classpath, we have bigger problems.
        try {
            Class.forName("art.sylleth.syllesia.platform.textures.Texture");
        } catch (ReflectiveOperationException ignored) {}
        // Set up the maps.
        Syllesia.instance.registerMap(new Map("Base Map", Platform.BASE_MAP));
        // TODO register default events.
        Player player = new Player("?", UUID.randomUUID(), new Camera(new Location(4.5, 4.5, 1, 0, 0, -0.4, Syllesia.instance.getBaseMap())));
        Platform platform = new Platform(player);
    }

    private final EventBus eventBus = new EventBus();
    private final String version = "0.1-alpha";
    private final Logger logger = new Logger();
    private final List<Map> maps = new ArrayList<>();

    /**
     * Gets the event bus instance this game uses.
     *
     * @return the event bus.
     */
    public EventBus getEventBus() {
        return this.eventBus;
    }

    /**
     * Gets the current version of Syllesia.
     *
     * @return the version.
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Gets the logger of this application.
     *
     * @return the logger.
     */
    public Logger getLogger() {
        return this.logger;
    }

    /**
     * Gets a map with the given name.
     *
     * @param name the name of the map to retrieve.
     * @return the map if found, else null.
     */
    @Nullable
    public Map getMap(String name) {
        for (Map map : maps)
            if (map.getName().equals(name))
                return map;
        return null;
    }

    /**
     * Registers a map. Assigns an ID and returns the map.
     *
     * @param map the map to register.
     */
    public int registerMap(Map map) {
        if (!this.maps.contains(map))
            this.maps.add(map);
        int index = this.maps.indexOf(map);
        try {
            Field field = map.getClass().getDeclaredField("id");
            boolean before = field.canAccess(map);
            field.setAccessible(true);
            field.set(map, index);
            field.setAccessible(before);
        } catch (ReflectiveOperationException ex) {
            this.getLogger().error(ex, Syllesia.class, 118);
        }
        return index;
    }

    /**
     * Gets the base map of this game.
     *
     * @return the base map.
     */
    @NotNull
    public Map getBaseMap() {
        return this.maps.get(0);
    }

}