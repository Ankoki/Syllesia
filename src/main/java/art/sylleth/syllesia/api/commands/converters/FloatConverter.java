package art.sylleth.syllesia.api.commands.converters;

import art.sylleth.syllesia.api.commands.ArgumentConverter;
import org.jetbrains.annotations.NotNull;

/**
 * Class to handle conversions from string to float.
 */
public class FloatConverter extends ArgumentConverter<Float> {

    @Override
    public Float convert(String argument) {
        try {
            return Float.parseFloat(argument);
        } catch (NumberFormatException ex) {
            return -1F;
        }
    }

    @Override
    @NotNull
    public Class<? extends Float> getReturnType() {
        return Float.class;
    }

}
