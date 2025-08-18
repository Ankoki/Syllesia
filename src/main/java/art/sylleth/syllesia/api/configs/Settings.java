package art.sylleth.syllesia.api.configs;

import art.sylleth.syllesia.Syllesia;
import art.sylleth.syllesia.files.ConfigurationFile;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to control settings from the game files.
 */
public class Settings extends ConfigurationFile {

    private static final Map<String, Object> DEFAULTS = new HashMap<>();

    static {
        DEFAULTS.put("frames-per-second", "45");
        DEFAULTS.put("debug", "true");
        DEFAULTS.put("lang", "en_GB");
        DEFAULTS.put("admin-commands", "false");
    }

    private int fps;
    private boolean debug;
    private String lang;
    private boolean adminCommandsEnabled;

    /**
     * Creates a new Settings file.
     */
    public Settings() {
        super(FileType.KEY_VAL, true);
    }

    @Override
    @NotNull
    public Map<String, Object> getDefaults() {
        return Settings.DEFAULTS;
    }

    @Override
    public void processData(Map<String, Object> data) {
        this.validateMap(data);
        this.fps = Integer.parseInt((String) data.get("frames-per-second"));
        this.debug = Boolean.parseBoolean((String) data.get("debug"));
        this.lang = (String) data.get("lang");
        this.adminCommandsEnabled = Boolean.parseBoolean((String) data.get("admin-commands"));
    }

    @Override
    public void writeData() {
        Map<String, Object> map = new HashMap<>();
        map.put("frames-per-second", this.fps);
        map.put("debug", this.debug);
        map.put("lang", this.lang);
        map.put("admin-commands", this.adminCommandsEnabled);
        this.writeFile(map);
    }

    @Override
    @NotNull
    public String getId() {
        return ConfigurationFile.SETTINGS;
    }

    @Override
    @NotNull
    public String getPath() {
        return "settings.txt";
    }

    /**
     * Gets the frames-per-second configuration setting.
     *
     * @return the fps to be used.
     */
    public int getFps() {
        return this.fps;
    }

    /**
     * Gets the debug configuration setting.
     *
     * @return true if debug mode.
     */
    public boolean isDebug() {
        return this.debug;
    }

    /**
     * Gets the language file to be used by this game.
     *
     * @return the lang file.
     */
    @NotNull
    public String getLang() {
        return this.lang;
    }

    /**
     * Checks if admin commands should be registered.
     *
     * @return true if should be enabled, else false.
     */
    public boolean isAdminCommandsEnabled() {
        return this.adminCommandsEnabled;
    }
}
