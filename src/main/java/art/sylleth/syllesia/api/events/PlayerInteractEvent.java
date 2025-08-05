package art.sylleth.syllesia.api.events;

import art.sylleth.syllesia.entities.Player;
import art.sylleth.syllesia.handlers.event.PreventableEvent;
import art.sylleth.syllesia.api.world.Location;
import org.jetbrains.annotations.NotNull;

/**
 * Event fired when a player interacts with a non-air location.
 */
public class PlayerInteractEvent extends PreventableEvent {

    /**
     * Enum to determine what type of click was made on this event.
     */
    public enum ClickType {
        LEFT,
        RIGHT,
        SHIFT_LEFT,
        SHIFT_RIGHT;
    }

    private final Player player;
    private final Location location;
    private final ClickType clickType;

    /**
     * Creates a new player interact event that can be called.
     *
     * @param player the player.
     * @param location the location the player is interacting with.
     * @param clickType the click type of this event.
     */
    public PlayerInteractEvent(Player player, Location location, ClickType clickType) {
        this.player = player;
        this.location = location;
        this.clickType = clickType;
    }

    @Override
    @NotNull
    public String getName() {
        return "PlayerInteractEvent";
    }

    /**
     * Gets the player of this event.
     *
     * @return the player.
     */
    @NotNull
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Gets the location interacted with.
     *
     * @return the location.
     */
    @NotNull
    public Location getLocation() {
        return this.location;
    }

    /**
     * Gets the type of click used for this interaction.
     *
     * @return the click type.
     */
    @NotNull
    public ClickType getClickType() {
        return this.clickType;
    }

}
