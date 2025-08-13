package art.sylleth.syllesia.api.commands.converters;

import art.sylleth.syllesia.api.commands.ArgumentConverter;
import org.jetbrains.annotations.NotNull;

/**
 * Class to handle conversions from string to integer.
 */
public class IntegerConverter extends ArgumentConverter<Integer> {

    @Override
    public Integer convert(String argument) {
        try {
            return Integer.parseInt(argument);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    @Override
    @NotNull
    public Class<? extends Integer> getReturnType() {
        return Integer.class;
    }

}
