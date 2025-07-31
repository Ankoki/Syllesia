package art.sylleth.syllesia.handlers.event;

/**
 * Class to allow events to be prevented from happening.<br>
 * If you're creating an event, after calling it, you should check the {@link Preventable#isPrevented()} value<br>
 * before executing the event's code.
 */
public abstract class Preventable {

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
