package art.sylleth.syllesia.api.events;

import art.sylleth.syllesia.entities.Player;
import art.sylleth.syllesia.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * Event fired when a player joins the game.
 */
public class PlayerJoinEvent extends Event {

    private final Player player;

    /**
     * Creates a new player join event that can be called.
     *
     * @param player the player.
     */
    public PlayerJoinEvent(Player player) {
        this.player = player;
    }

    @Override
    @NotNull
    public String getName() {
        return "PlayerJoinEvent";
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

}
