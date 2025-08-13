package art.sylleth.syllesia.api.commands.converters;

import art.sylleth.syllesia.api.commands.ArgumentConverter;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Class to handle conversions from string to number.
 */
public class NumberConverter extends ArgumentConverter<Number> {

    @Override
    public Number convert(String argument) {
        try {
            return NumberFormat.getInstance().parse(argument);
        } catch (ParseException ex) {
            return -1;
        }
    }

    @Override
    @NotNull
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

}
