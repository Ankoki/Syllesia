package art.sylleth.syllesia.event;

import org.jetbrains.annotations.NotNull;

/**
 * Class to be extended to create an event.
 */
public abstract class Event {

    /**
     * Gets a user-friendly name for this event.
     *
     * @return the name of this event.
     */
    @NotNull
    public abstract String getName();

}
