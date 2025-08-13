package art.sylleth.syllesia;

import art.sylleth.syllesia.api.commands.CommandHandler;
import art.sylleth.syllesia.api.configs.Settings;
import art.sylleth.syllesia.api.configs.Userdata;
import art.sylleth.syllesia.api.quest.Quest;
import art.sylleth.syllesia.api.world.Map;
import art.sylleth.syllesia.entities.Player;
import art.sylleth.syllesia.api.entities.npc.ElvenDeity;
import art.sylleth.syllesia.files.ConfigurationFile;
import art.sylleth.syllesia.files.json.JSONSerializable;
import art.sylleth.syllesia.handlers.event.EventBus;
import art.sylleth.syllesia.misc.Logger;
import art.sylleth.syllesia.api.world.Location;
import art.sylleth.syllesia.platform.game.Camera;
import art.sylleth.syllesia.platform.game.Platform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
        Syllesia.instance.setupJsonSerializable();
        Syllesia.instance.setupMaps();
        Syllesia.instance.declareQuests();
        Syllesia.instance.setupConfigurations();
        Userdata userdata = (Userdata) Syllesia.instance.getConfiguration(ConfigurationFile.USERDATA);
        Player player = new Player(userdata.getName(), userdata.getUuid(), new Camera(userdata.getLastLocation()));
        Syllesia.getInstance().setPlatform(new Platform(player));
    }

    private final EventBus eventBus = new EventBus();
    private final String version = "0.1-alpha";
    private final Logger logger = new Logger();
    private final List<Map> maps = new ArrayList<>();
    private final List<ConfigurationFile> configurations = new ArrayList<>();
    private final List<Quest> quests = new ArrayList<>();
    private final CommandHandler commandHandler = new CommandHandler();
    private Platform platform;

    /**
     * Gets the event bus instance this game uses.
     *
     * @return the event bus.
     */
    @NotNull
    public EventBus getEventBus() {
        return this.eventBus;
    }

    /**
     * Gets the current version of Syllesia.
     *
     * @return the version.
     */
    @NotNull
    public String getVersion() {
        return this.version;
    }

    /**
     * Gets the logger of this application.
     *
     * @return the logger.
     */
    @NotNull
    public Logger getLogger() {
        return this.logger;
    }

    /**
     * Gets the platform this instance is running on.
     *
     * @return the platform.
     */
    @NotNull
    public Platform getPlatform() {
        return this.platform;
    }

    /**
     * Sets the platform this instance should be running on.
     *
     * @param platform the platform.
     */
    private void setPlatform(Platform platform) {
        if (this.platform != null)
            throw new IllegalStateException("You cannot write over a running platform.");
        this.platform = platform;
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
     * Gets a map with the given id.
     *
     * @param id the id of the map to retrieve.
     * @return the map if found, else null.
     */
    @Nullable
    public Map getMap(int id) {
        for (Map map : maps)
            if (map.getId() == id)
                return map;
        return null;
    }

    /**
     * Registers a map. Assigns an ID and returns the map.
     *
     * @param map the map to register.
     * @return the id of the newly added map.
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
            this.getLogger().error(ex, Syllesia.class, 154);
        }
        return index;
    }

    /**
     * Registers a new configuration with this file manager.
     *
     * @param config the configuration to register.
     * @throws IllegalArgumentException if the configuration id is already in use.
     */
    public void registerConfiguration(ConfigurationFile config) {
        for (ConfigurationFile file : configurations)
            if (file.getId().equals(config.getId()))
                throw new IllegalArgumentException("Duplicate configuration file id: " + config.getId());
        configurations.add(config);
    }

    /**
     * Retrieves the configuration with the given ID.
     *
     * @param id the ID of the configuration file.
     * @return the configuration
     */
    @Nullable
    public ConfigurationFile getConfiguration(String id) {
        return configurations.stream().filter(config -> config.getId().equals(id)).findFirst().orElse(null);
    }

    /**
     * Gets the command handler of this instance.
     *
     * @return the command handler.
     */
    @NotNull
    public CommandHandler getCommandHandler() {
        return this.commandHandler;
    }

    /**
     * Registers any serializable classes with JSONSerializable.
     */
    private void setupJsonSerializable() {
        JSONSerializable.register(Location.class);
    }

    /**
     * Registers the maps used by the base game.
     */
    private void setupMaps() {
        Map ruins = new Map("Ruins", Map.RUINS);
        Syllesia.instance.registerMap(ruins);
        ruins.addEntity(new ElvenDeity(new Location(2.5, 12, 0.2, -0.9, -0.3, -0.1, ruins)));
    }

    /**
     * Initiates the configurations used by the base game.
     */
    private void setupConfigurations() {
        this.registerConfiguration(new Settings());
        this.registerConfiguration(new Userdata());
        // TODO lang files.
    }

    /**
     * Declares all the quests in the base Syllesia game.
     */
    private void declareQuests() {

    }

}