package art.sylleth.syllesia.api.conversation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class to create and navigate conversations between players and in-game entities.
 */
public class Conversation {

    private final String id;
    private final Dialogue start;
    private final List<Dialogue> dialogues = new ArrayList<>();

    /**
     * Creates a new conversation with the given id and dialogues to navigate between.
     * @param id the id of this conversation.
     * @param dialogues the dialogues to add.
     */
    public Conversation(String id, Dialogue... dialogues) {
        if (dialogues.length == 0)
            throw new IllegalArgumentException("Conversations must have at least one dialogue.");
        this.start = dialogues[0];
        this.id = id;
        this.dialogues.addAll(Arrays.asList(dialogues));
    }

    /**
     * Gets the id of this conversation.
     *
     * @return this conversations' id.
     */
    @NotNull
    public String getId() {
        return this.id;
    }

    /**
     * Gets the starting dialogue of this conversation.
     *
     * @return the start dialogue.
     */
    @NotNull
    public Dialogue getStartDialogue() {
        return this.start;
    }

    /**
     * Gets the dialogue with the given id.
     *
     * @param id the id of the dialogue to look for.
     * @return the dialogue, or null if the dialogue is invalid, or no dialogue is found.
     */
    @Nullable
    public Dialogue getDialogue(String id) {
        for (Dialogue dialogue : dialogues)
            if (dialogue.getId().equals(id) && dialogue.isValid())
                return dialogue;
        return null;
    }

    /**
     * Checks if this conversation contains the given dialogue.
     *
     * @param dialogue the dialogue to look for.
     * @return true if present, else false.
     */
    public boolean contains(Dialogue dialogue) {
        return this.dialogues.contains(dialogue);
    }

}
