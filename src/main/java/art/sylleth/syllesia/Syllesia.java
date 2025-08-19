package art.sylleth.syllesia;

import art.sylleth.syllesia.api.Defaults;
import art.sylleth.syllesia.api.configs.Placeholder;
import art.sylleth.syllesia.api.conversation.Conversation;
import art.sylleth.syllesia.api.conversation.Dialogue;
import art.sylleth.syllesia.api.quest.Quest;
import art.sylleth.syllesia.handlers.CommandHandler;
import art.sylleth.syllesia.api.commands.converters.*;
import art.sylleth.syllesia.api.commands.impl.AdminCommands;
import art.sylleth.syllesia.api.configs.Mapdata;
import art.sylleth.syllesia.api.configs.Settings;
import art.sylleth.syllesia.api.configs.Userdata;
import art.sylleth.syllesia.api.world.Map;
import art.sylleth.syllesia.entities.Player;
import art.sylleth.syllesia.api.entities.npc.ElvenDeity;
import art.sylleth.syllesia.files.ConfigurationFile;
import art.sylleth.syllesia.files.json.JSONSerializable;
import art.sylleth.syllesia.handlers.ConversationHandler;
import art.sylleth.syllesia.handlers.EventHandler;
import art.sylleth.syllesia.handlers.QuestHandler;
import art.sylleth.syllesia.listeners.GameListener;
import art.sylleth.syllesia.misc.Logger;
import art.sylleth.syllesia.api.world.Location;
import art.sylleth.syllesia.misc.Timespan;
import art.sylleth.syllesia.platform.game.Camera;
import art.sylleth.syllesia.platform.game.Platform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        Syllesia.instance.declareQuests();
        Syllesia.instance.declareConversations();
        Syllesia.instance.declareEvents();
        Syllesia.instance.setupConfigurations();
        Syllesia.instance.declareCommands();
        Syllesia.instance.spawnEntities();
        Userdata userdata = (Userdata) Syllesia.instance.getConfiguration(ConfigurationFile.USERDATA);
        Player player = new Player(userdata.getName(), userdata.getUuid(), new Camera(userdata.getLastLocation()));
        Syllesia.instance.setPlatform(new Platform(player));
    }

    private final EventHandler eventHandler = new EventHandler();
    private final String version = "0.1-alpha";
    private final Logger logger = new Logger();
    private final List<Map> maps = new ArrayList<>();
    private final List<ConfigurationFile> configurations = new ArrayList<>();
    private final CommandHandler commandHandler = new CommandHandler();
    private final ConversationHandler conversationHandler = new ConversationHandler();
    private final QuestHandler questHandler = new QuestHandler();
    private Platform platform;

    /**
     * Gets the event bus instance this game uses.
     *
     * @return the event bus.
     */
    @NotNull
    public EventHandler getEventBus() {
        return this.eventHandler;
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
     * Gets all the registered maps.
     *
     * @return the maps.
     */
    @NotNull
    public Map[] getMaps() {
        return this.maps.toArray(new Map[0]);
    }

    /**
     * Registers a map.
     *
     * @param map the map to register.
     */
    public void registerMap(Map map) {
        if (!this.maps.contains(map))
            this.maps.add(map);
    }

    /**
     * Clears the map registry.
     */
    public void clearMapRegistry() {
        this.maps.clear();
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
     * Gets the conversation handler of this instance.
     *
     * @return the conversation handler.
     */
    @NotNull
    public ConversationHandler getConversationHandler() {
        return this.conversationHandler;
    }

    /**
     * Gets the quest handler of this instance.
     *
     * @return the quest handler.
     */
    @NotNull
    public QuestHandler getQuestHandler() {
        return this.questHandler;
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
     * Registers any serializable classes with JSONSerializable.
     */
    private void setupJsonSerializable() {
        JSONSerializable.register(Location.class);
    }

    /**
     * Spawns the entities used by the base game.
     */
    private void spawnEntities() {
        Map ruins = this.getMap("ruins");
        this.getMap("ruins").addEntity(new ElvenDeity(new Location(2.5, 12, 0.2, -0.9, -0.3, -0.1, ruins)));
    }

    /**
     * Initiates the configurations used by the base game.
     */
    private void setupConfigurations() {
        this.registerConfiguration(new Mapdata()); // Mapdata before userdata so locations don't get scrambled.
        this.registerConfiguration(new Settings());
        this.registerConfiguration(new Userdata());
        // TODO lang files.
    }

    /**
     * Declares all the default event listeners in the base Syllesia game.
     */
    private void declareEvents() {
        this.eventHandler.registerHandlers(new GameListener());
    }

    /**
     * Declares all the default conversations in the base Syllesia game.
     * TODO all titles, contents and choices to be converted to lang files.
     */
    private void declareConversations() {
        Conversation conversation = new Conversation(Defaults.Conversation.FIRST_JOIN,
                new Dialogue("GREETING")
                        .setTitle("?")
                        .setContent("Hello " + Placeholder.of(Placeholder.PLAYER_NAME) + "! Nice to finally meet you.")
                        .addChoice("Who's there?", (player) -> "WHO_AM_I")
                        .addChoice("Where am I?", (player) -> "SYLLESIA_INTRO")
                        .validate(),
                new Dialogue("WHO_AM_I")
                        .setTitle("Maria")
                        .setContent("I'm Maria. I'll be the entity overseeing your journey throughout your journey through Syllesia!")
                        .addChoice("What's Syllesia?", (player) -> "SYLLESIA_INTRO")
                        .validate(),
                new Dialogue("SYLLESIA_INTRO")
                        .setTitle("Maria")
                        .setContent("This land you're standing in is Syllesia. This used to be where elven descendants roamed freely among each other, however we are currently in the midst of the Marken War.")
                        .addChoice("The... Marken War???", (player) -> "WAR_INTRO")
                        .addChoice("What's this got to do with me?", (player) -> "WAR_INTRO")
                        .addChoice("So... why am I here? I'm not of elven descent.", (player) -> "WAR_INTRO")
                        .validate(),
                new Dialogue("WAR_INTRO")
                        .setTitle("Maria")
                        .setContent("We have many hunters, due to our unfaltering knowledge, and one has finally caught up with us. The warlocks. They have overtaken our city and forced us into hiding. You were transported here to help save us from captivity.")
                        .addChoice("That sounds awful, how can I help?", (player) -> "QUEST_SPEECH")
                        .validate(),
                new Dialogue("QUEST_SPEECH")
                        .setTitle("Maria")
                        .setContent("Well... you're currently stuck in our labyrinth. You need to find your way out. You need to collect gold from the molten blocks to open the master door to our world. Each block has a limited supply, so you'll need to hunt.")
                        .addChoice("How much gold will I need?", (player) -> "QUEST_ASSIGNMENT_H")
                        .addChoice("I don't really feel like doing that.", (player) -> "QUEST_ASSIGNMENT_S")
                        .validate(),
                new Dialogue("QUEST_ASSIGNMENT_H")
                        .setTitle("Maria")
                        .setContent("You'll need to obtain 50 gold! Each block contains roughly 10 gold, so get hunting. We're counting on you!")
                        .addChoice("I'll get on it!", (player) -> {
                            player.sendTitle("You have accepted a quest!\nObtain 50 gold.", Timespan.of("3 seconds"));
                            player.getUserdata().assignQuest(this.questHandler.getQuest(Defaults.Quest.HUMBLE_BEGINNINGS));
                            return Dialogue.EXIT_POINTER;
                        })
                        .validate(),
                new Dialogue("QUEST_ASSIGNMENT_S")
                        .setTitle("Maria")
                        .setContent("Well unfortunately, the device used to bring you here broke the second you landed. We tried to repair it while you were asleep but it didn't work. So you'll need to obtain 50 gold, and each block contains roughly 10 gold, we are counting on you!")
                        .addChoice("I mean, if I don't have a choice...", (player) -> {
                            player.sendTitle("You've got a quest!\nObtain 50 gold.", Timespan.of("3 seconds"));
                            player.getUserdata().assignQuest(this.questHandler.getQuest(Defaults.Quest.HUMBLE_BEGINNINGS));
                            return Dialogue.EXIT_POINTER;
                        })
                        .validate());
        this.conversationHandler.registerConversation(conversation);
    }

    /**
     * Declares all the default quests in the base Syllesia game.
     */
    private void declareQuests() {
        Quest quest = new Quest(Defaults.Quest.HUMBLE_BEGINNINGS, (player) -> player.getCoins() >= 50, (player) -> {
            player.sendTitle("You have obtained the 50 gold!", Timespan.of("5 seconds"));
            // TODO complete level storyline from this quest.
        });
        this.questHandler.registerQuest(quest);
    }

    /**
     * Initialises all commands that come with Syllesia.
     */
    private void declareCommands() {
        this.commandHandler.registerConverters(
                new TextureConverter(),
                new MapConverter(),
                new DoubleConverter(),
                new FloatConverter(),
                new IntegerConverter(),
                new LongConverter(),
                new NumberConverter());
        Settings settings = (Settings) getConfiguration(ConfigurationFile.SETTINGS);
        if (settings.isAdminCommandsEnabled()) {
            this.logger.debug("Admin Commands Enabled");
            this.commandHandler.registerCommandClass(new AdminCommands());
        }
    }

}