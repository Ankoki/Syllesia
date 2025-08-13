package art.sylleth.syllesia.api.configs;

import art.sylleth.syllesia.Syllesia;
import art.sylleth.syllesia.files.ConfigurationFile;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * File to handle getting the text to be used.
 */
public class Lang extends ConfigurationFile {

    private static final Map<String, Object> DEFAULTS = new HashMap<>();
    private static String EXTENSION;

    static {
        Lang.EXTENSION = ((Settings) Syllesia.getInstance().getConfiguration(ConfigurationFile.SETTINGS)).getLang();
        if (Lang.EXTENSION.equals("en_GB")) {
            DEFAULTS.put("title", "Syllesia");
        }
    }

    private final Map<String, String> lang = new HashMap<>();

    /**
     * Creates a new language file.
     */
    public Lang() {
        super(FileType.KEY_VAL, true);
    }

    @Override
    @NotNull
    public Map<String, Object> getDefaults() {
        return Lang.DEFAULTS;
    }

    @Override
    @NotNull
    public String getId() {
        return ConfigurationFile.LANG;
    }

    @Override
    @NotNull
    public String getPath() {
        return "lang_" + Lang.EXTENSION;
    }

    @Override
    public void processData(Map<String, Object> data) {
        this.lang.clear();
        this.lang.putAll((Map<String, String>) data.get("lang"));
    }

    @Override
    public void writeData() {

    }

}