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

    private final Map<String, String> lang = new HashMap<>();

    /**
     * Creates a new language file.
     */
    public Lang() {
        super(FileType.KEY_VAL, true);
    }

    @Override
    public void applyDefaults() {
        this.writeFile("title=Syllesia");
    }

    @Override
    @NotNull
    public String getId() {
        return ConfigurationFile.LANG;
    }

    @Override
    @NotNull
    public String getPath() {
        return "lang_" + ((Settings) Syllesia.getInstance().getConfiguration(ConfigurationFile.SETTINGS)).getLang();
    }

    @Override
    public void processData(Map<String, Object> data) {
        this.lang.clear();
        this.lang.putAll((Map<String, String>) data.get("lang"));
    }

}