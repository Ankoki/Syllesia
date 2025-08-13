package art.sylleth.syllesia.api.entities.npc;

import art.sylleth.syllesia.api.world.Location;
import art.sylleth.syllesia.entities.Entity;
import art.sylleth.syllesia.misc.Misc;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;

public class ElvenDeity extends Entity {

    private static final BufferedImage front = Misc.getResourceImage("textures/entities/elf/elven_front.png");
    private static final BufferedImage back = Misc.getResourceImage("textures/entities/elf/elven_back.png");
    private static final BufferedImage left = Misc.getResourceImage("textures/entities/elf/elven_left.png");
    private static final BufferedImage right = Misc.getResourceImage("textures/entities/elf/elven_right.png");
    private static final BufferedImage top = Misc.getResourceImage("textures/entities/elf/elven_top.png");
    private static final BufferedImage bottom = Misc.getResourceImage("textures/entities/elf/elven_bottom.png");

    /**
     * Creates a new elven deity entity with the given location.
     */
    public ElvenDeity(Location location) {
        super(location, front, back, left, right, top, bottom);
    }

    @Override
    public boolean alwaysFacePlayer() {
        return false;
    }

    @Override
    @NotNull
    public String getId() {
        return "elven_deity";
    }

}
