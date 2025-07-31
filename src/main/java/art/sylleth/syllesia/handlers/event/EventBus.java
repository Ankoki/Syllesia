package art.sylleth.syllesia.handlers.event;

import art.sylleth.syllesia.Syllesia;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * The main handler for events for this program.<br>
 * The usage of this bus is quite simple, and can be done with minimal effort from users.<br>
 * You must create a class which implements {@link EventHandler}, and create a method<br>
 * which takes in a parameter of the {@link Event} you would like to listen too,<br>
 * and be annotated with {@link Handler}. These methods shouldn't be static, but may be<br>
 * private, protected or public. An example usage has been provided below.<br>
 * <code>public class SyllesiaHandler implements EventHandler {</code><br>
 * <code>    @Handler </code><br>
 * <code>    private void onInteract(PlayerInteractEvent event) { </code><br>
 * <code>        Player player = event.getPlayer(); </code><br>
 * <code>        if (player.getLocation().getX() > 2) { </code><br>
 * <code>            // Do something! </code><br>
 * <code>        } </code><br>
 * <code>    } </code><br>
 * <code>} </code><br>
 */
public class EventBus {

    private final List<EventHandler> handlers = new ArrayList<>();
    private final List<Class<? extends Event>> registered = new ArrayList<>();

    /**
     * Creates a new event bus instance to track all registered listeners.
     */
    public EventBus() {}

    /**
     * Registers the given handlers to the event bus.
     *
     * @param handlers the handlers to register.
     */
    public void registerHandlers(EventHandler... handlers) {
        for (EventHandler handler : handlers) {
            for (Method method : this.getHandlerMethods(handler))
                registered.add((Class<? extends Event>) method.getParameterTypes()[0]); // This is safe as this check is done in this#getHandlerMethods(EventHandler).
        }
        this.handlers.addAll(List.of(handlers));
    }

    /**
     * Unregisters the given handlers. They may be re-registered, however they will no longer be called.
     *
     * @param handlers the handlers to unregister.
     */
    public void unregisterHandlers(EventHandler... handlers) {
        for (EventHandler handler : handlers) {
            for (Method method : this.getHandlerMethods(handler))
                registered.remove(method.getParameterTypes()[0]);
            this.handlers.remove(handler);
        }
    }

    /**
     * Calls the given event to each of the registered handlers.
     *
     * @param event the event to call.
     */
    public void callEvent(Event event) {
        if (!registered.contains(event.getClass()))
            return; // No reason to try and call an event that isn't registered.
        try {
            for (EventHandler handler : handlers) {
                for (Method method : this.getHandlerMethods(handler)) {
                    if (method.getParameterTypes()[0] == event.getClass()) {
                        boolean before = method.canAccess(handler);
                        method.setAccessible(true);
                        method.invoke(handler, event);
                        method.setAccessible(before);
                    }
                }
            }
        } catch (ReflectiveOperationException ex) {
            Syllesia.getInstance().getLogger().error(ex, EventBus.class, 83);
        }
    }

    /**
     * Gets all methods from a handler which are applicable for event calling.
     *
     * @param handler the handler to fetch from.
     * @return the handler methods.
     */
    private Method[] getHandlerMethods(EventHandler handler) {
        List<Method> methods = new ArrayList<>();
        for (Method method : handler.getClass().getDeclaredMethods()) {
            if (method.getAnnotation(Handler.class) != null && method.getReturnType() == Void.TYPE) {
                Class<?>[] parameters = method.getParameterTypes();
                if (parameters.length == 1 && Event.class.isAssignableFrom(parameters[0]))
                    methods.add(method);
            }
        }
        return methods.toArray(new Method[0]);
    }

}
