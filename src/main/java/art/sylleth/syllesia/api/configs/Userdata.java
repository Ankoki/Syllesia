package art.sylleth.syllesia.api.configs;

import art.sylleth.syllesia.Syllesia;
import art.sylleth.syllesia.api.world.Location;
import art.sylleth.syllesia.entities.Player;
import art.sylleth.syllesia.files.ConfigurationFile;
import art.sylleth.syllesia.files.json.JSONSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
        DEFAULTS.put("last-seen", System.currentTimeMillis());
        DEFAULTS.put("completed-quests", new ArrayList<>());
        DEFAULTS.put("metadata", new HashMap<>());
    }

    private String name;
    private UUID uuid;
    private double coins;
    private Location lastLocation;
    private long lastSeen;
    private List<String> completedQuests;
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
        this.completedQuests = new ArrayList<>();
        this.completedQuests.addAll((List<String>) data.get("completed-quests"));
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
        data.put("completed-quests", this.completedQuests);
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