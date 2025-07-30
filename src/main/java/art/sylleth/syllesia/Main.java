package art.sylleth.syllesia;

import art.sylleth.syllesia.platform.game.Platform;

public class Main {

    public static void main(String[] args) {
        // Load textures before the platform starts.
        // We can safely ignore any thrown exception, as if the texture class isn't in the classpath, we have bigger problems.
        try {
            Class.forName("art.sylleth.syllesia.platform.textures.Texture");
        } catch (ReflectiveOperationException ignored) {}
        Platform platform = new Platform();
    }

}