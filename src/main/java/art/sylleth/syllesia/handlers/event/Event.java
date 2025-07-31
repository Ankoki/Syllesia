package art.sylleth.syllesia.handlers.event;

import org.jetbrains.annotations.NotNull;

public abstract class Event {

    /**
     * Gets a user-friendly name for this event.
     *
     * @return the name of this event.
     */
    @NotNull
    public abstract String getName();

}
