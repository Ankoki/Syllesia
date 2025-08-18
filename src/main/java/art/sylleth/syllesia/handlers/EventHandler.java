package art.sylleth.syllesia.handlers;

import art.sylleth.syllesia.Syllesia;
import art.sylleth.syllesia.event.Event;
import art.sylleth.syllesia.event.EventManager;
import art.sylleth.syllesia.event.Listener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * The main handler for events for this program.<br>
 * The usage of this bus is quite simple, and can be done with minimal effort from users.<br>
 * You must create a class which implements {@link EventManager}, and create a method<br>
 * which takes in a parameter of the {@link Event} you would like to listen too,<br>
 * and be annotated with {@link Listener}. These methods shouldn't be static, but may be<br>
 * private, protected or public. An example usage has been provided below.<br>
 * <code>public class SyllesiaHandler implements EventManager {</code><br>
 * <code>    @Handler </code><br>
 * <code>    private void onInteract(PlayerInteractEvent event) { </code><br>
 * <code>        Player player = event.getPlayer(); </code><br>
 * <code>        if (player.getLocation().getX() > 2) { </code><br>
 * <code>            // Do something! </code><br>
 * <code>        } </code><br>
 * <code>    } </code><br>
 * <code>} </code><br>
 */
public class EventHandler {

    private final List<EventManager> managers = new ArrayList<>();
    private final List<Class<? extends Event>> registered = new ArrayList<>();

    /**
     * Creates a new event bus instance to track all registered listeners.
     */
    public EventHandler() {}

    /**
     * Registers the given managers to the event bus.
     *
     * @param managers the managers to register.
     */
    public void registerHandlers(EventManager... managers) {
        for (EventManager manager : managers) {
            for (Method method : this.getManagerMethods(manager))
                registered.add((Class<? extends Event>) method.getParameterTypes()[0]); // This is safe as this check is done in this#getHandlerMethods(EventHandler).
        }
        this.managers.addAll(List.of(managers));
    }

    /**
     * Unregisters the given managers. They may be re-registered, however they will no longer be called.
     *
     * @param managers the managers to unregister.
     */
    public void unregisterHandlers(EventManager... managers) {
        for (EventManager manager : managers) {
            for (Method method : this.getManagerMethods(manager))
                registered.remove(method.getParameterTypes()[0]);
            this.managers.remove(manager);
        }
    }

    /**
     * Calls the given event to each of the registered managers.
     *
     * @param event the event to call.
     */
    public void callEvent(Event event) {
        if (!registered.contains(event.getClass()))
            return; // No reason to try and call an event that isn't registered.
        try {
            for (EventManager manager : this.managers) {
                for (Method method : this.getManagerMethods(manager)) {
                    if (method.getParameterTypes()[0] == event.getClass()) {
                        boolean before = method.canAccess(manager);
                        method.setAccessible(true);
                        method.invoke(manager, event);
                        method.setAccessible(before);
                    }
                }
            }
        } catch (ReflectiveOperationException ex) {
            Syllesia.getInstance().getLogger().error(ex, EventHandler.class, 85);
        }
    }

    /**
     * Gets all methods from a manager which are applicable for event calling.
     *
     * @param manager the manager to fetch from.
     * @return the manager methods.
     */
    private Method[] getManagerMethods(EventManager manager) {
        List<Method> methods = new ArrayList<>();
        for (Method method : manager.getClass().getDeclaredMethods()) {
            if (method.getAnnotation(Listener.class) != null && method.getReturnType() == Void.TYPE) {
                Class<?>[] parameters = method.getParameterTypes();
                if (parameters.length == 1 && Event.class.isAssignableFrom(parameters[0]))
                    methods.add(method);
            }
        }
        return methods.toArray(new Method[0]);
    }

}
