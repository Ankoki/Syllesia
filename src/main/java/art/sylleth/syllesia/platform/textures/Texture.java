package art.sylleth.syllesia.platform.textures;

import art.sylleth.syllesia.misc.Misc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;

/**
 * Class to store texture information.
 */
public class Texture {

    private static final int MAX_TEXTURES = 512; // Can be updated if needed.

    private static final Texture[] textures = new Texture[MAX_TEXTURES];

    public static final Texture AIR;
    public static final Texture SLIME;
    public static final Texture GRASS;
    public static final Texture STONE_WALL;
    public static final Texture LIGHT_METAL;
    public static final Texture DARK_METAL;
    public static final Texture MOLTEN_GOLD;

    static {
        AIR = Texture.registerTexture("", 64, 0);
        SLIME = Texture.registerTexture("textures/blocks/slime.png", 64, 1);
        GRASS = Texture.registerTexture("textures/blocks/grass.png", 64, 2);
        STONE_WALL = Texture.registerTexture("textures/blocks/stone_wall.png", 64, 3);
        LIGHT_METAL = Texture.registerTexture("textures/blocks/light_metal.png", 64, 4);
        DARK_METAL = Texture.registerTexture("textures/blocks/dark_metal.png", 64, 5);
        MOLTEN_GOLD = Texture.registerTexture("textures/blocks/molten_gold.jpg", 512, 6);
    }

    /**
     * Registers a new texture from the given file path and size in pixels.
     * For example, a 512x512 image would provide 512.
     * ID's must be less than the value of {@link Texture#MAX_TEXTURES}, and must not already be in use.
     *
     * @param file the file directory.
     * @param size the size.
     * @param id   the id of this texture.
     * @return the created texture.
     * @throws IllegalArgumentException if the texture id is greater than the allowed size, or the id is in use.
     */
    @NotNull
    public static Texture registerTexture(String file, int size, int id) {
        if (id < 0 || id > MAX_TEXTURES || textures[id] != null)
            throw new IllegalArgumentException("Invalid id provided [" + id + "].");
        Texture texture = new Texture(file, size, id);
        textures[id] = texture;
        return texture;
    }

    /**
     * Gets a texture from the given id.
     *
     * @param id the id of the texture.
     * @return the texture, or null if it doesn't exist.
     * @throws IllegalArgumentException if the texture id is greater than the allowed size.
     */
    @Nullable
    public static Texture fromId(int id) {
        if (id < 0 || id > MAX_TEXTURES)
            throw new IllegalArgumentException("Invalid id provided [" + id + "].");
        return textures[id];
    }

    private BufferedImage image;
    private final String file;
    private final int size;
    private final int[] pixels;
    private final int id;

    /**
     * Creates a new texture from the given file path and size in pixels.
     * For example, a 512x512 image would provide 512.
     * ID's must be less than the value of {@link Texture#MAX_TEXTURES}, and must not already be in use.
     *
     * @param file the file directory.
     * @param size the size.
     * @param id   the id of this texture.
     */
    protected Texture(String file, int size, int id) {
        this.file = file;
        this.size = size;
        this.id = id;
        this.pixels = new int[size * size];
        if (id != 0) // We ignore the AIR texture, it's present to stop other textures being registered over it.
            this.assignPixels();
    }

    /**
     * Assigns each pixel in the {@link Texture#pixels} field to the correct RGB value.
     */
    private void assignPixels() {
        this.image = Misc.getResourceImage(file);
        int width = image.getWidth();
        int height = image.getHeight();
        image.getRGB(0, 0, width, height, pixels, 0, width);
    }

    /**
     * Gets the buffered image of this texture.
     * If this texture is {@link Texture#AIR}, returns null.
     *
     * @return the image of this texture.
     */
    @Nullable
    public BufferedImage getImage() {
        return this.image;
    }

    /**
     * Gets the RGB map of pixels for this texture.
     *
     * @return the pixels.
     */
    public int[] getPixels() {
        return this.pixels;
    }

    /**
     * Gets the size of this texture.
     *
     * @return the size,
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Gets the ID of this texture.
     *
     * @return the id.
     */
    public int getId() {
        return this.id;
    }

}
