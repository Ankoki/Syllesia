package art.sylleth.syllesia.handlers;

import art.sylleth.syllesia.api.conversation.Conversation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

// change map registration into a handler.
public class ConversationHandler {

    private final Map<String, Conversation> conversationStorage = new HashMap<>();

    /**
     * Registers a conversation to be obtainable from this storage.
     *
     * @param conversation the conversation.
     * @return true if successful, false if another conversation is stored by the same name.
     */
    public boolean registerConversation(Conversation conversation) {
        String id = conversation.getId();
        if (this.conversationStorage.containsKey(id))
            return false;
        this.conversationStorage.put(id, conversation);
        return true;
    }

    /**
     * Gets the conversation under the given id.
     *
     * @param id the id to search for.
     * @return if found, the conversation linked to the given id, else null.
     */
    @Nullable
    public Conversation getConversation(String id) {
        return this.conversationStorage.get(id);
    }

    /**
     * Deletes the conversation with the given id.
     *
     * @param id the id to remove.
     * @return true if successfully deleted, else false.
     */
    public boolean deleteConversation(String id) {
        return this.conversationStorage.remove(id) != null;
    }

}
