package art.sylleth.syllesia.api.conversation;

import art.sylleth.syllesia.Syllesia;
import art.sylleth.syllesia.entities.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class Dialogue {

    public static final int MAXIMUM_CHOICE_COUNT = 5;
    public static final String EXIT_POINTER = "EXIT_CURRENT_CONVERSATION";

    private final String id;
    private String title;
    private String content;
    private final Map<String, Function<Player, String>> optionPointers = new LinkedHashMap<>(); // Linked to preserve order added.
    private boolean valid = false;

    /**
     * Creates a new dialogue with the given id.
     * This class may not be used until it is validated using {@link Dialogue#validate()}.
     *
     * For a class to be valid, it must meet the following criteria.
     * - Content via {@link Dialogue#setContent(String)}.
     * - A minimum of 1 option to choose from using {@link Dialogue#addChoice(String, Function)}
     *
     * @param id the id this dialogue should have.
     */
    public Dialogue(String id) {
        this.id = id;
    }

    /**
     * The id of this dialogue.
     *
     * @return this dialogue's id.
     */
    @NotNull
    public String getId() {
        return this.id;
    }

    /**
     * Gets the title of this dialogue.
     *
     * @return the title.
     */
    @Nullable
    public String getTitle() {
        return this.title;
    }

    /**
     * Sets the title of this dialogue.
     *
     * @param title the title.
     * @return this dialogue, for chaining.
     */
    @NotNull
    public Dialogue setTitle(@Nullable String title) {
        this.title = title;
        return this;
    }

    /**
     * Gets the content of this dialogue.
     *
     * @return the content.
     */
    @NotNull
    public String getContent() {
        return this.content;
    }

    /**
     * Sets the content of this dialogue.
     *
     * @param content the content.
     * @return this dialogue for chaining.
     */
    @NotNull
    public Dialogue setContent(@NotNull String content) {
        this.content = content;
        return this;
    }

    /**
     * Gets all the choices of this dialogue.
     *
     * @return the response choices for this dialogue.
     */
    @NotNull
    public String[] getChoices() {
        return this.optionPointers.keySet().toArray(new String[0]);
    }

    /**
     * Adds a player choice to this dialogue which will point to the next dialogue to start.
     * The supplier should return the id of the dialogue this option should open.
     * If the conversation should be ended, use {@link Dialogue#EXIT_POINTER} as the supplier response.
     * At this time, a maximum of 5 options are allowed. An exception will be thrown if this number is exceeded.
     *
     * @param response the player's proposed choice of response.
     * @param exitEvent the supplier to retrieve
     *
     * @return this dialogue, for chaining.
     */
    @NotNull
    public Dialogue addChoice(@NotNull String response, @NotNull Function<Player, String> exitEvent) {
        if (this.optionPointers.size() >= Dialogue.MAXIMUM_CHOICE_COUNT)
            throw new IllegalStateException("Dialogue's have a maximum of 5 choices");
        this.optionPointers.put(response, exitEvent);
        return this;
    }

    /**
     * Gets the id of the next dialogue to display from the response given.
     * Also calls the exit event, get the conversation pointer when update is needed.
     *
     * @param response the response to get the pointer of.
     * @return the next dialogue this option points too.
     */
    @Nullable
    public String getPointer(@NotNull String response) {
        if (this.optionPointers.containsKey(response))
            return this.optionPointers.get(response).apply(Syllesia.getInstance().getPlatform().getMainPlayer());
        return null;
    }

    /**
     * Checks if this dialogue has been validated. See {@link Dialogue#validate()}.
     *
     * @return true if validated.
     */
    public boolean isValid() {
        return this.valid;
    }

    /**
     * Validates this dialogue. Should be called at the very end of building, as it allows this
     * dialogue to be used in conversation. For validation criteria, see {@link Dialogue(String)}.
     *
     * @return this dialogue, for chaining.
     */
    @NotNull
    public Dialogue validate() {
        if (this.id == null || this.title == null || this.content == null ||
                this.id.isEmpty() || this.content.isEmpty() || this.optionPointers.isEmpty())
            throw new IllegalArgumentException("Dialogue validation failed.");
        this.valid = true;
        return this;
    }

}
