package art.sylleth.syllesia.api.configs;

import art.sylleth.syllesia.Syllesia;
import art.sylleth.syllesia.api.world.Location;
import art.sylleth.syllesia.entities.Player;
import art.sylleth.syllesia.files.ConfigurationFile;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Class used for loading and modifying userdata.
 */
public class Userdata extends ConfigurationFile {

    private String name;
    private UUID uuid;
    private double coins = 0;
    private Location lastLocation;
    private long lastSeen;
    private final List<String> completedQuests = new ArrayList<>();

    /**
     * Creates a new userdata file.
     */
    public Userdata() {
        super(FileType.JSON, true);
    }

    @Override
    public void applyDefaults() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("name", "TestUser");
        defaults.put("uuid", UUID.randomUUID());
        defaults.put("coins", coins);
        defaults.put("last-location", new Location(4.5, 4.5, 1, 0, 0, -0.4, Syllesia.getInstance().getBaseMap()));
        defaults.put("last-seen", System.currentTimeMillis());
        defaults.put("completed-quests", completedQuests);
        this.writeFile(defaults);
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
        this.coins = Double.parseDouble(data.get("coins").toString());
        this.lastLocation = (Location) data.get("last-location");
        this.lastSeen = Long.parseLong(data.get("last-seen").toString());
        this.completedQuests.clear();
        this.completedQuests.addAll(Arrays.asList((String[]) data.get("completed-quests")));
    }

    @Override
    public void writeData() {
        Player player = Syllesia.getInstance().getPlatform().getMainPlayer();
        Map<String, Object> data = new HashMap<>();
        data.put("name", this.name);
        data.put("uuid", this.uuid.toString());
        data.put("coins", player.getCoins());
        data.put("last-location", player.getLocation());
        data.put("last-seen", System.currentTimeMillis());
        data.put("completed-quests", this.completedQuests);
        this.writeFile(data);
    }

    /**
     * Validates all necessary keys are present.
     *
     * @param data the map to check against.
     */
    public void validateMap(Map<String, Object> data) {
        for (String key : new String[]{"name", "uuid", "coins", "last-location", "last-seen"})
            if (!data.containsKey(key))
                throw new IllegalArgumentException("The '" + key + "' is missing.");
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

}