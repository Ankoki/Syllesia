package art.sylleth.syllesia.platform.screen;

import art.sylleth.syllesia.misc.Misc;
import art.sylleth.syllesia.api.world.Location;
import art.sylleth.syllesia.platform.game.Camera;
import art.sylleth.syllesia.platform.textures.Texture;

/**
 * Handles displaying a cameras view.
 */
public class Screen {

    private final int[][] map;
    private final int width;
    private final int height;

    /**
     * Creates a new screen.
     *
     * @param map the map to view.
     * @param width the width of the screen.
     * @param height the height of the screen.
     */
    public Screen(int[][] map, int width, int height) {
        this.map = map;
        this.width = width;
        this.height = height;
    }

    /**
     * Updates the given camera.
     *
     * @param camera the camera.
     * @param pixels the pixels to change.
     */
    public void update(Camera camera, int[] pixels) {
        // Clear camera.
        for (int i = 0; i < (pixels.length / 2); i++)
            if (pixels[i] != Misc.fromHex("82C8E5").getRGB())
                pixels[i] = Misc.fromHex("82C8E5").getRGB();
        for (int i = pixels.length / 2; i < pixels.length; i++)
            if (pixels[i] != Misc.fromHex("704F38").getRGB())
                pixels[i] = Misc.fromHex("704F38").getRGB();
        Location location = camera.getLocation();
        for (int x = 0; x < this.width; x++) {
            double cameraX = (2.0 * x) / (this.width - 1.0);
            double rayDirX = location.getXDir() + location.getXPlane() * cameraX;
            double rayDirY = location.getYDir() + location.getYPlane() * cameraX;
            int mapX = (int) location.getX();
            int mapY = (int) location.getY();
            double sideDistX;
            double sideDistY;
            double deltaDistX = Math.sqrt(1 + (rayDirY * rayDirY) / (rayDirX * rayDirX));
            double deltaDistY = Math.sqrt(1 + (rayDirX * rayDirX) / (rayDirY * rayDirY));
            int stepX, stepY;
            if (rayDirX < 0) {
                stepX = -1;
                sideDistX = (location.getX() - mapX) * deltaDistX;
            } else {
                stepX = 1;
                sideDistX = (mapX + 1 - location.getX()) * deltaDistX;
            }
            if (rayDirY < 0) {
                stepY = -1;
                sideDistY = (location.getY() - mapY) * deltaDistY;
            } else {
                stepY = 1;
                sideDistY = (mapY + 1 - location.getY()) * deltaDistY;
            }
            boolean hit = false;
            int side = 0;
            while (!hit) {
                if (sideDistX < sideDistY) {
                    sideDistX += deltaDistX;
                    mapX += stepX;
                    side = 0;
                } else {
                    sideDistY += deltaDistY;
                    mapY += stepY;
                    side = 1;
                }
                if (map[mapX][mapY] > 0)
                    hit = true;
            }
            double perpWallDist;
            if (side == 0)
                perpWallDist = Math.abs((mapX - location.getX() + (1.0 - stepX) / 2) / rayDirX);
            else
                perpWallDist = Math.abs((mapY - location.getY() + (1.0 - stepY) / 2 ) / rayDirY);
            int lineHeight;
            if (perpWallDist > 0)
                lineHeight = Math.abs((int) (height / perpWallDist));
            else
                lineHeight = height;
            int drawStart = -lineHeight / 2 + height / 2;
            if (drawStart < 0)
                drawStart = 0;
            int drawEnd = lineHeight / 2 + height / 2;
            if (drawEnd >= height)
                drawEnd = height - 1;
            Texture texture = Texture.fromId(map[mapX][mapY] - 1);
            double wallX;
            if (side == 0)
                wallX = location.getY() + perpWallDist * rayDirY;
            else
                wallX = location.getX() + perpWallDist * rayDirX;
            wallX -= Math.floor(wallX);
            int textureX = (int) (wallX * texture.getSize()); // Texture won't be null as any value that is 0 is skipped over.
            for (int y = drawStart; y < drawEnd; y++) {
                int textureY = (((y * 2 - height + lineHeight) << 6) / lineHeight) / 2;
                int texIndex = Math.min(textureX + textureY * texture.getSize(), texture.getPixels().length - 1);
                int colour = texture.getPixels()[texIndex];
                if (side == 1)
                    colour = (colour >> 1) & 0x7F7F7F; // Bitshift to darken color by 50%.
                pixels[x + y * width] = colour;
            }
        }
    }

}
