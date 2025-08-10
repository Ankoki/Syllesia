package art.sylleth.syllesia.api.quest;

import art.sylleth.syllesia.entities.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Class to create quests and declare the completion criteria.
 */
public class Quest {

    private final Function<Player, Boolean> criteria;
    private final String id;

    /**
     * Creates a new quest with the given data.
     *
     * @param id the id of this quest.
     * @param criteria the criteria to match the completion of this quest.
     */
    public Quest(String id, Function<Player, Boolean> criteria) {
        this.criteria = criteria;
        this.id = id;
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
        return criteria.apply(player);
    }

}
