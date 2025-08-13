package art.sylleth.syllesia.api.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Should be extended to declare a command argument that can be converted to a given type.
 * In the package `art.sylleth.syllesia.api.commands.converters` there are default classes that are converted,
 * and can be used as a reference.
 *
 * @param <To> the class a string can be converted to.
 */
public abstract class ArgumentConverter<To> {

    @Nullable
    public abstract To convert(String argument);

    @NotNull
    public abstract Class<? extends To> getReturnType();

}