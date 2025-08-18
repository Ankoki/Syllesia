package art.sylleth.syllesia.api.events;

import art.sylleth.syllesia.entities.Player;
import art.sylleth.syllesia.event.PreventableEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Event fired when a player attempts to execute a command.
 */
public class PlayerCommandProcessEvent extends PreventableEvent {

    private final Player player;
    private final String command;
    private final String[] args;

    /**
     * Creates a new player command event that can be called.
     * Please note this is also called for invalid commands, as no checks are done before calling,
     *
     * @param player the player.
     * @param command the command the player is executing.
     * @param args the arguments provided.
     */
    public PlayerCommandProcessEvent(Player player, String command, String[] args) {
        this.player = player;
        this.command = command;
        this.args = args;
    }

    @Override
    @NotNull
    public String getName() {
        return "PlayerCommandProcessEvent";
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
     * Gets the name of the command attempted.
     *
     * @return the name.
     */
    @NotNull
    public String getCommand() {
        return this.command;
    }

    /**
     * Gets the arguments provided.
     *
     * @return the arguments.
     */
    @NotNull
    public String[] getArguments() {
        return this.args;
    }

}
