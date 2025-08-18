package art.sylleth.syllesia.event;

/**
 * Class to create events that can be prevented from happening.<br>
 * If you're creating an event, after calling it, you should check the {@link PreventableEvent#isPrevented()} value<br>
 * before executing the event's code.
 */
public abstract class PreventableEvent extends Event {

    private boolean prevented = false;

    /**
     * See's if this event has been prevented from occurring.
     *
     * @return true if prevented.
     */
    public boolean isPrevented() {
        return this.prevented;
    }

    /**
     * Set's the event's prevented value.
     *
     * @param prevented true if wanted to be prevented.
     */
    public void setPrevented(boolean prevented) {
        this.prevented = prevented;
    }

}
