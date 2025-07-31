package art.sylleth.syllesia.api.conversation;

public class Option {

    private final boolean end;

    public Option(String followup, Option... children) {
        this.end = children == null || children.length == 0;
    }

    /**
     * If this option is the end of the dialogue.
     *
     * @return true if the end of the dialogue.
     */
    public boolean isEnd() {
        return this.end;
    }

}
