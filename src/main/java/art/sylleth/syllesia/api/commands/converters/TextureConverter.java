package art.sylleth.syllesia.api.commands.converters;

import art.sylleth.syllesia.api.commands.ArgumentConverter;
import art.sylleth.syllesia.platform.textures.Texture;
import org.jetbrains.annotations.NotNull;

/**
 * Class to handle conversions from string to texture.
 */
public class TextureConverter extends ArgumentConverter<Texture> {

    @Override
    public Texture convert(String argument) {
        return Texture.fromId(Integer.parseInt(argument));
    }

    @Override
    @NotNull
    public Class<? extends Texture> getReturnType() {
        return Texture.class;
    }

}
