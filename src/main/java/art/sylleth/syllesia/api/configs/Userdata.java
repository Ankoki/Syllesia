package art.sylleth.syllesia.api.configs;

import art.sylleth.syllesia.files.ConfigurationFile;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

/**
 * Class used for loading and modifying userdata.
 */
public class Userdata extends ConfigurationFile {

    // Default values while the JSON parsing isn't done.
    private String name = "UserTest";
    private UUID uuid = UUID.randomUUID();
    private double coins = 0;

    /**
     * Creates a new userdata file.
     */
    public Userdata() {
        super(FileType.JSON, true);
    }

    @Override
    public void applyDefaults() {
        this.writeFile("{\n\t\"name\":\"TestUser\",\n\t\"uuid\":\"" + UUID.randomUUID() + "\",\n\t\"coins\":0\n}");
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
    }

    /**
     * Validates all necessary keys are present.
     *
     * @param data the map to check against.
     */
    public void validateMap(Map<String, Object> data) {
        for (String key : new String[]{"name", "uuid", "coins"})
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

}