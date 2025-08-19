package art.sylleth.syllesia.api.quest;

import art.sylleth.syllesia.entities.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Class to create quests and declare the completion criteria.
 */
public class Quest {

    private final Function<Player, Boolean> criteria;
    private final String id;
    private final Consumer<Player> onComplete;

    /**
     * Creates a new quest with the given data.
     *
     * @param id the id of this quest.
     * @param criteria the criteria to match the completion of this quest.
     * @param onComplete what to run when the player completes this quest.
     */
    public Quest(String id, Function<Player, Boolean> criteria, Consumer<Player> onComplete) {
        this.criteria = criteria;
        this.id = id;
        this.onComplete = onComplete;
    }

    /**
     * Gets the ID of this quest.
     *
     * @return the id.
     */
    @NotNull
    public String getId() {
        return this.id;
    }

    /**
     * Checks if the player can complete this quest.
     *
     * @param player the player to check against.
     * @return true if completed.
     */
    public boolean checkCompletion(Player player) {
        return this.criteria.apply(player);
    }

    /**
     * Runs the on complete event for the given player.
     *
     * @param player the player.
     */
    public void runCompletion(Player player) {
        this.onComplete.accept(player);
    }

}
