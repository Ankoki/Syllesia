package art.sylleth.syllesia.platform.screen;

import art.sylleth.syllesia.entities.Entity;
import art.sylleth.syllesia.misc.Misc;
import art.sylleth.syllesia.api.world.Location;
import art.sylleth.syllesia.platform.game.Camera;
import art.sylleth.syllesia.platform.textures.Texture;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Handles displaying a cameras view.
 */
public class Screen {

    private final int width;
    private final int height;

    /**
     * Creates a new screen.
     *
     * @param width the width of the screen.
     * @param height the height of the screen.
     */
    public Screen(int width, int height) {
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
        for (int i = 0; i < (pixels.length / 2); i++) // Sky
            if (pixels[i] != Misc.fromHex("263f5e").getRGB())
                pixels[i] = Misc.fromHex("263f5e").getRGB();
        for (int i = pixels.length / 2; i < pixels.length; i++) // Floor
            if (pixels[i] != Misc.fromHex("524f52").getRGB())
                pixels[i] = Misc.fromHex("524f52").getRGB();
        Location location = camera.getLocation();
        int[][] map = location.getMap().getMatrix();
        double[] zBuffer = new double[width];
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
            zBuffer[x] = perpWallDist;
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
            Texture texture = Texture.fromId(map[mapX][mapY]);
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
        this.renderEntities(camera, pixels, zBuffer);
    }

    private void renderEntities(Camera camera, int[] pixels, double[] zBuffer) {
        Location location = camera.getLocation();
        double xPos = location.getX();
        double yPos = location.getY();
        double xDir = location.getXDir();
        double yDir = location.getYDir();
        double xPlane = location.getXPlane();
        double yPlane = location.getYPlane();
        List<Entity> entities = location.getMap().getEntities();
        entities.sort((a, b) -> {
            double distA = Math.pow(xPos - a.getLocation().getX(), 2) + Math.pow(yPos - a.getLocation().getY(), 2);
            double distB = Math.pow(xPos - b.getLocation().getX(), 2) + Math.pow(yPos - b.getLocation().getY(), 2);
            return Double.compare(distB, distA);
        });
        for (Entity entity : entities) {
            double spriteX = entity.getLocation().getX() - xPos;
            double spriteY = entity.getLocation().getY() - yPos;
            double invDet = 1.0 / (xPlane * yDir - xDir * yPlane);
            double transformX = invDet * (yDir * spriteX - xDir * spriteY);
            double transformY = invDet * (-yPlane * spriteX + xPlane * spriteY);
            if (transformY <= 0)
                continue; // Entity is behind player.
            int screenX = (int) ((width/ 2.0) * (1 + transformX / transformY));
            int spriteHeight = Math.abs((int) (height / transformY));
            int drawEndY = height / 2 + spriteHeight / 2;
            int drawStartY = drawEndY - spriteHeight;
            BufferedImage sprite = this.getSpriteDirection(entity, transformX, transformY);
            int spriteWidth = spriteHeight; // Let's pretend they're squares for now.
            int drawStartX = -spriteWidth / 2 + screenX;
            int drawEndX = spriteWidth / 2 + screenX;
            for (int x = drawStartX; x < drawEndX; x++) {
                if (x < 0 || x >= width)
                    continue;
                if (transformY > zBuffer[x])
                    continue;
                int texX = (x - drawStartX) * sprite.getWidth() / spriteWidth;
                for (int y = drawStartY; y < drawEndY; y++) {
                    if (y < 0 || y >= height)
                        continue;
                    int texY = (y - drawStartY) * sprite.getHeight() / spriteHeight;
                    int colour = sprite.getRGB(texX, texY);
                    if ((colour >> 24) == 0x00)
                        continue; // Skip transparent pixels.
                    pixels[x + y * width] = colour;
                }
            }
        }

    }

    // TODO fix
    private BufferedImage getSpriteDirection(Entity entity, double transformX, double transformY) {
        if (entity.alwaysFacePlayer())
            return entity.getFrontTexture();
        double angle = Math.atan2(-transformX, transformY);
        if (angle < 0)
            angle += 2 * Math.PI;
        if (angle >= 7 * Math.PI / 4 || angle < Math.PI / 4)
            return entity.getFrontTexture();
        if (angle >= Math.PI / 4 && angle < 3 * Math.PI / 4)
            return entity.getLeftTexture();
        if (angle >= 3 * Math.PI / 4 && angle < 5 * Math.PI / 4)
            return entity.getBackTexture();
        return entity.getRightTexture();
    }

}
