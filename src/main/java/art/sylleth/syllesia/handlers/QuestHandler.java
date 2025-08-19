package art.sylleth.syllesia.handlers;

import art.sylleth.syllesia.api.quest.Quest;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to handle the registration, application and completion of quests.
 */
public class QuestHandler {

    private final List<Quest> registeredQuests = new ArrayList<>();

    /**
     * Registers a quest to this handler.
     *
     * @param quest the quest to register.
     * @return true if successful.
     */
    public boolean registerQuest(Quest quest) {
        if (this.registeredQuests.contains(quest))
            throw new IllegalArgumentException("Quest already registered: " + quest.getId());
        return this.registeredQuests.add(quest);
    }

    /**
     * Unregisters the given quest.
     *
     * @param quest the quest to unregister.
     * @return true if successful.
     */
    public boolean unregisterQuest(Quest quest) {
        return this.registeredQuests.remove(quest);
    }

    /**
     * Gets the quest with the given id.
     *
     * @param id the id to search for.
     * @return the quest with the given id, or null if not found.
     */
    @Nullable
    public Quest getQuest(String id) {
        for (Quest quest : this.registeredQuests)
            if (quest.getId().equals(id))
                return quest;
        return null;
    }

}
