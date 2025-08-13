package art.sylleth.syllesia.api.commands.converters;

import art.sylleth.syllesia.Syllesia;
import art.sylleth.syllesia.api.commands.ArgumentConverter;
import art.sylleth.syllesia.api.world.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class to handle conversions from string to map.
 * Please note this takes the map name, not the map ID.
 */
public class MapConverter extends ArgumentConverter<Map> {

    @Override
    @Nullable
    public Map convert(String argument) {
        return Syllesia.getInstance().getMap(argument);
    }

    @Override
    @NotNull
    public Class<? extends Map> getReturnType() {
        return Map.class;
    }

}
