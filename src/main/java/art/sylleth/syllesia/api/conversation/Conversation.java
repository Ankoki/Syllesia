package art.sylleth.syllesia.api.conversation;

/**
 * Class to create and navigate conversations between players and in-game entities.
 */
public class Conversation {

    private final String opener;

    /**
     * Creates a new conversation.
     * The opener given will be the first dialog this conversation provides.
     *
     * @param opener the opening conversation dialog.
     */
    public Conversation(String opener) {
        this.opener = opener;
    }

    /**
     * Creates a response option for this conversation.
     * Multiple options can be chained in a row, they will be shown in
     * ascending order of the option id.
     * This response will be sent by the player who interacts with it.
     *
     * @param option the id for this response.
     * @param text the text of this response.
     * @return the current Conversation for chaining.
     */
    public Conversation responseChoice(int option, String text) {
        return this;
    }

    /**
     * Creates a reply for the option id given.
     * The reply will be sent by the entity this conversation is attached too.
     *
     * @param option the option id which initiates this reply.
     * @param text the reply.
     * @return the current Conversation for chaining.
     */
    public Conversation reply(int option, String text) {
        return this;
    }

}
