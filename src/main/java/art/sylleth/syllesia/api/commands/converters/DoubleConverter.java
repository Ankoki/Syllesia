package art.sylleth.syllesia.api.commands.converters;

import art.sylleth.syllesia.api.commands.ArgumentConverter;
import org.jetbrains.annotations.NotNull;

/**
 * Class to handle conversions from string to double.
 */
public class DoubleConverter extends ArgumentConverter<Double> {

    @Override
    public Double convert(String argument) {
        try {
            return Double.parseDouble(argument);
        } catch (NumberFormatException ex) {
            return -1D;
        }
    }

    @Override
    @NotNull
    public Class<? extends Double> getReturnType() {
        return Double.class;
    }

}
