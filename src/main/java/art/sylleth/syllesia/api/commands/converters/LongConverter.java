package art.sylleth.syllesia.api.commands.converters;

import art.sylleth.syllesia.api.commands.ArgumentConverter;
import org.jetbrains.annotations.NotNull;

/**
 * Class to handle conversions from string to integer.
 */
public class LongConverter extends ArgumentConverter<Long> {

    @Override
    public Long convert(String argument) {
        try {
            return Long.parseLong(argument);
        } catch (NumberFormatException ex) {
            return -1L;
        }
    }

    @Override
    @NotNull
    public Class<? extends Long> getReturnType() {
        return Long.class;
    }

}
