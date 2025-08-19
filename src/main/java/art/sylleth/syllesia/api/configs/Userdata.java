package art.sylleth.syllesia.api.configs;

import art.sylleth.syllesia.Syllesia;
import art.sylleth.syllesia.api.quest.Quest;
import art.sylleth.syllesia.api.world.Location;
import art.sylleth.syllesia.entities.Player;
import art.sylleth.syllesia.files.ConfigurationFile;
import art.sylleth.syllesia.files.json.JSONSerializable;
import art.sylleth.syllesia.handlers.QuestHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Class used for loading and modifying userdata.
 */
public class Userdata extends ConfigurationFile {

    private static final Map<String, Object> DEFAULTS = new HashMap<>();

    static {
        DEFAULTS.put("name", "TestUser");
        DEFAULTS.put("uuid", UUID.randomUUID());
        DEFAULTS.put("coins", 0);
        DEFAULTS.put("last-location", new Location(4.5, 4.5, 1, 0, 0, -0.4, Syllesia.getInstance().getBaseMap()));
        DEFAULTS.put("last-seen", 0);
        DEFAULTS.put("current-quests", new ArrayList<>());
        DEFAULTS.put("completed-quests", new ArrayList<>());
        DEFAULTS.put("metadata", new HashMap<>());
    }

    private String name;
    private UUID uuid;
    private double coins;
    private Location lastLocation;
    private long lastSeen;
    private List<Quest> currentQuests;
    private List<Quest> completedQuests;
    private Map<String, Object> metadata;

    /**
     * Creates a new userdata file.
     */
    public Userdata() {
        super(FileType.JSON, true);
    }

    @Override
    @NotNull
    public Map<String, Object> getDefaults() {
        return Userdata.DEFAULTS;
    }

    @Override
    @NotNull
    public String getId() {
        return ConfigurationFile.USERDATA;
    }

    @Override
    @NotNull
    public String getPath() {
        return "userdata.json";
    }

    @Override
    public void processData(Map<String, Object> data) {
        this.validateMap(data);
        this.name = data.get("name").toString();
        this.uuid = UUID.fromString(data.get("uuid").toString());
        Syllesia.getInstance().getLogger().debug("Userdata#coins=" + data.get("coins"),
                                                 "Userdata#coins#toString=" + data.get("coins").toString() +
                                                 "parsed=" + Double.parseDouble(data.get("coins").toString()));
        this.coins = Double.parseDouble(data.get("coins").toString());
        Syllesia.getInstance().getLogger().debug("this.coins=" + this.coins);
        this.lastLocation = (Location) data.get("last-location");
        this.lastSeen = Long.parseLong(data.get("last-seen").toString());
        QuestHandler questHandler = Syllesia.getInstance().getQuestHandler();
        this.currentQuests = new ArrayList<>();
        List<String> rawCurrent = (List<String>) data.get("current-quests");
        for (String raw : rawCurrent) {
            Quest quest = questHandler.getQuest(raw);
            if (quest != null)
                this.currentQuests.add(quest);
            else
                Syllesia.getInstance().getLogger().warn("Userdata#current-quests#" + raw + " not found.");
        }
        this.completedQuests = new ArrayList<>();
        List<String> rawCompleted = (List<String>) data.get("completed-quests");
        for (String raw : rawCompleted) {
            Quest quest = questHandler.getQuest(raw);
            if (quest != null)
                this.completedQuests.add(quest);
            else
                Syllesia.getInstance().getLogger().warn("Userdata#completed-quests#" + raw + " not found.");
        }
        this.metadata = new ConcurrentHashMap<>();
        this.metadata.putAll((Map<String, Object>) data.get("metadata"));
    }

    @Override
    public void writeData() {
        Player player = Syllesia.getInstance().getPlatform().getMainPlayer();
        Map<String, Object> data = new HashMap<>();
        data.put("name", this.name);
        data.put("uuid", this.uuid.toString());
        data.put("coins", this.coins);
        data.put("last-location", player.getLocation());
        data.put("last-seen", System.currentTimeMillis());
        data.put("current-quests", this.currentQuests.stream().map(Quest::getId).toList());
        data.put("completed-quests", this.completedQuests.stream().map(Quest::getId).toList());
        data.put("metadata", this.metadata);
        this.writeFile(data);
    }

    /**
     * Gets the name of this userdata.
     *
     * @return the name.
     */
    @NotNull
    public String getName() {
        return this.name;
    }

    /**
     * Gets the UUID of this userdata.
     *
     * @return the UUID.
     */
    @NotNull
    public UUID getUuid() {
        return this.uuid;
    }

    /**
     * Gets the coins of this userdata.
     *
     * @return the coins.
     */
    public double getCoins() {
        return this.coins;
    }

    /**
     * Changes the amount of coins this userdata has.
     *
     * @param coins the new amount of coins.
     */
    public void setCoins(double coins) {
        if (coins < 0)
            throw new IllegalArgumentException("The coins cannot be negative.");
        this.coins = coins;
    }

    /**
     * Gets the last location this userdata was seen at.
     *
     * @return the last location.
     */
    @NotNull
    public Location getLastLocation() {
        return this.lastLocation;
    }

    /**
     * Gets the last time this userdata was logged onto.
     *
     * @return the last seen.
     */
    public long getLastSeen() {
        return this.lastSeen;
    }

    /**
     * Assigns a quest for this player to complete.
     *
     * @param quest the quest to assign.
     */
    public void assignQuest(Quest quest) {
        if (!this.currentQuests.contains(quest))
            this.currentQuests.add(quest);
    }

    /**
     * Completes the given quest if it's in their current quest.
     * Calls the completion event, so users should not do this.
     *
     * @param quest the quest to complete.
     */
    public void completeQuest(Quest quest) {
        if (this.currentQuests.contains(quest)) {
            this.currentQuests.remove(quest);
            this.completedQuests.add(quest);
            quest.runCompletion(Syllesia.getInstance().getPlatform().getMainPlayer());
        }
    }

    /**
     * Gets the quest with the given id if this user is assigned it.
     *
     * @param id the id of the quest.
     * @return the quest if found, else null.
     */
    @Nullable
    public Quest getQuest(String id) {
        for (Quest quest : this.currentQuests)
            if (quest.getId().equals(id))
                return quest;
        return null;
    }

    /**
     * Checks if this user has completed a quest with the given id.
     *
     * @param id the id to check for.
     * @return true if completed, else false.
     */
    public boolean hasCompletedQuest(String id) {
        for (Quest quest : this.completedQuests)
            if (quest.getId().equals(id))
                return true;
        return false;
    }

    /**
     * Gets the quests this user has assigned to them.
     *
     * @return the current quests.
     */
    @NotNull
    public Quest[] getQuests() {
        return this.currentQuests.toArray(new Quest[0]);
    }

    /**
     * Gets the quests this user has completed.
     *
     * @return the completed quests.
     */
    @NotNull
    public Quest[] getCompletedQuests() {
        return this.completedQuests.toArray(new Quest[0]);
    }

    /**
     * Writes persistent metadata to this userdata file.
     * This will write over any existing key.
     *
     * @param key the key of this data.
     * @param value the value.
     * @return the previous value if overwritten, otherwise null.
     */
    @Nullable
    public Object writeMetadata(String key, Object value) {
        if (!(value instanceof Number ||
                value instanceof String ||
                value instanceof Boolean ||
                value instanceof Map ||
                value instanceof List<?> ||
                value instanceof JSONSerializable))
            throw new IllegalArgumentException("Invalid metadata value for persistent storage.");
        return this.metadata.put(key, value);
    }

    /**
     * Checks if the userdata has metadata matching the given key.
     *
     * @param key the key to check for.
     * @return true if the metadata contains the key, else false.
     */
    public boolean hasMetadata(String key) {
        return this.metadata.containsKey(key);
    }

    /**
     * Removes the given metadata key from storage.
     *
     * @param key the key to erase.
     * @return the value of the removed key, or if it doesn't exist, null.
     */
    @Nullable
    public Object removeMetadata(String key) {
        return this.metadata.remove(key);
    }

}