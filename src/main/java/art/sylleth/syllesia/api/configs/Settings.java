package art.sylleth.syllesia.api.configs;

import art.sylleth.syllesia.Syllesia;
import art.sylleth.syllesia.files.ConfigurationFile;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Class to control settings from the game files.
 */
public class Settings extends ConfigurationFile {

    private int fps;
    private boolean debug;
    private String lang;

    /**
     * Creates a new Settings file.
     */
    public Settings() {
        super(FileType.KEY_VAL, true);
    }

    @Override
    public void applyDefaults() {
        super.writeFile("frames-per-second=45\ndebug=true\nlang=en_GB");
    }

    @Override
    public void processData(Map<String, Object> data) {
        this.validateMap(data);
        this.fps = Integer.parseInt((String) data.get("frames-per-second"));
        this.debug = Boolean.parseBoolean((String) data.get("debug"));
        if (this.debug)
            Syllesia.getInstance().getLogger().debug("Settings#processData",
                    "data.get(\"frames-per-second\")=" + data.get("frames-per-second"),
                    "data.get(\"debug\")=" + data.get("debug"),
                    "data.get(\"lang\")=" + data.get("lang"));
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
     * Validates all necessary keys are present.
     *
     * @param data the map to check against.
     */
    public void validateMap(Map<String, Object> data) {
        for (String key : new String[]{"frames-per-second", "debug", "lang"})
            if (!data.containsKey(key))
                throw new IllegalArgumentException("The '" + key + "' is missing.");
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

}
