package art.sylleth.syllesia.files;

import art.sylleth.syllesia.Syllesia;
import art.sylleth.syllesia.files.json.JSON;
import art.sylleth.syllesia.files.json.MalformedJsonException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The class to extend and handle configuration files.
 */
public abstract class ConfigurationFile {

    private static final Map<String, ConfigurationFile> configurations = new ConcurrentHashMap<>();

    /**
     * Registers a configuration file, allowing for easy access throughout the game.
     *
     * @param configuration the configuration to register.
     * @throws IllegalArgumentException if the configurations ID is already registered.
     */
    public static void registerConfiguration(ConfigurationFile configuration) {
        if (configurations.containsKey(configuration.getId()))
            throw new IllegalArgumentException("Duplicate configuration id: " + configuration.getId());
        configurations.put(configuration.getId(), configuration);
    }

    /**
     * Gets a configuration file with the given id.
     *
     * @param id the id of the configuration to get.
     * @return the configuration with the given id, or null.
     */
    @Nullable
    public static ConfigurationFile getConfiguration(String id) {
        return configurations.get(id);
    }

    /**
     * The file types that this class supports loading.
     */
    public enum FileType {
        KEY_VAL,
        JSON;
    }

    // IDs used for internal file managers.
    public static final String SETTINGS = "GAME_SETTINGS";
    public static final String LANG = "GAME_LANGUAGE";
    public static final String USERDATA = "USERDATA";

    private final File root;
    private final FileType type;

    /**
     * Creates a new configuration file.
     *
     * @param type the file type of this configuration.
     * @param read true if the file should be read after loading defaults.
     */
    public ConfigurationFile(FileType type, boolean read) {
        this.type = type;
        this.root = new File(System.getProperty("user.home") + File.separator + "Syllesia" + File.separator, this.getPath());
        if (!this.root.exists()) {
            try {
                root.createNewFile();
                this.applyDefaults();
            } catch (IOException ex) {
                Syllesia.getInstance().getLogger().error(ex, ConfigurationFile.class, 74);
            }
        }
        if (read)
            this.readFile();
    }

    /**
     * Gets the unique identification string of this configuration.
     *
     * @return the id.
     */
    @NotNull
    public abstract String getId();

    /**
     * Gets the path to this configuration file, from the game's root folder.
     * For any folder additions, please use {@link File#separator} instead of /, for multi-platform compatibility.
     *
     * @return the path to this file.
     */
    @NotNull
    public abstract String getPath();

    /**
     * Handle the reading of the data.<br>
     *
     * @param data the data from the file.
     */
    public abstract void processData(Map<String, Object> data);

    /**
     * Method to save the configurations data to the file.
     * Will not be called by this class, should be implemented by the users.
     * Should call the {@link ConfigurationFile#writeFile(Map)} method for ease of use.
     */
    public abstract void writeData();

    /**
     * Gets the file type of this configuration file.
     *
     * @return the type of file this is.`
     */
    @NotNull
    public FileType getType() {
        return this.type;
    }

    /**
     * Method to copy a default configuration to the file.
     * Will be used when the file has not been found, and has been newly created.
     * Should call the {@link ConfigurationFile#writeFile(Map)} method for ease of use.
     */
    public abstract void applyDefaults();

    /**
     * Writes the given map to the file, using either KEY_VAL or JSON storage.
     *
     * @param map the string to write.
     */
    public void writeFile(Map<String, Object> map) {
        String fin = null;
        switch (this.type) {
            case KEY_VAL:
                StringBuilder builder = new StringBuilder();
                for (Map.Entry<String, Object> entry : map.entrySet())
                    builder.append(entry.getKey()).append("=").append(String.valueOf(entry.getValue())).append("\n");
                builder.setLength(builder.length() - 1);
                fin = builder.toString();
                break;
            case JSON:
                fin = JSON.toString(map, true, 4);
                break;
        }
        try (FileWriter writer = new FileWriter(this.root)) {
            Syllesia.getInstance().getLogger().debug("fin[" + fin + "]");
            writer.write(fin);
        } catch (IOException ex) {
            Syllesia.getInstance().getLogger().error(ex, ConfigurationFile.class, 144);
        }
    }

    /**
     * Reads the file and passes the data to the {@link ConfigurationFile#processData(Map)} method.
     */
    public void readFile() {
        switch (this.type) {
            case KEY_VAL:
                try (Scanner scanner = new Scanner(this.root)) {
                    Map<String, Object> pairs = new HashMap<>();
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if (!line.contains("=") && line.length() < 3)
                            throw new IllegalStateException("Invalid configuration file: " + this.getId());
                        pairs.put(line.split("=")[0], line.split("=")[1]);
                    }
                    this.processData(pairs);
                } catch (IOException ex) {
                    Syllesia.getInstance().getLogger().error(ex, ConfigurationFile.class, 165);
                }
                break;
            case JSON:
                try {
                    JSON json = new JSON(this.root);
                    this.processData(json);
                } catch (IOException | MalformedJsonException ex) {
                    Syllesia.getInstance().getLogger().error(ex, ConfigurationFile.class, 173);
                }
                break;
        }
    }

}
