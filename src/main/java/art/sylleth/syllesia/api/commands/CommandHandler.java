package art.sylleth.syllesia.api.commands;

import art.sylleth.syllesia.Syllesia;
import art.sylleth.syllesia.entities.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to handle registering and running commands that can be ran by the player.
 */
public class CommandHandler {

    private final Map<String, Command> commands = new HashMap<String, Command>();
    private final List<ArgumentConverter<?>> converters = new ArrayList<>();

    /**
     * Registers created converters.
     *
     * @param converters the converters.
     */
    public void registerConverters(ArgumentConverter<?>... converters) {
        this.converters.addAll(List.of(converters));
    }

    /**
     * Checks if a converter for a type exists.
     *
     * @param clazz the class to check for.
     * @return true if it exists.
     */
    public boolean hasConverter(Class<?> clazz) {
        for (ArgumentConverter<?> converter : this.converters)
            if (converter.getReturnType() == clazz)
                return true;
        return false;
    }

    /**
     * Gets the converter for a class.
     *
     * @param clazz the class to check for.
     * @return the converter for a class, if it doesn't exist, return null;
     */
    @Nullable
    public ArgumentConverter<?> getConverter(Class<?> clazz) {
        for (ArgumentConverter<?> converter : this.converters)
            if (converter.getReturnType() == clazz)
                return converter;
        return null;
    }

    /**
     * Registers a class to have its annotated methods be processed as commands.
     * If a command with a name that is provided in this class is already registered, this will skip it and return false.
     *
     * @param object the instance of the class to register.
     * @return true if successful, else false.
     */
    public boolean registerCommandClass(Object object) {
        boolean allSuccess = true;
        for (Method method : object.getClass().getMethods()) {
            method.setAccessible(true);
            if (method.isAnnotationPresent(CommandHook.class) &&
               (method.getReturnType() == void.class ||
                method.getReturnType() == boolean.class)) {
                CommandHook hook = method.getAnnotation(CommandHook.class);
                if (commands.containsKey(hook.name())) {
                    allSuccess = false;
                    continue;
                }
                Command command = new Command() {

                    @Override
                    protected boolean runCommand(Player player, String[] args) {
                        Class<?>[] types = method.getParameterTypes();
                        Object[] parameters = new Object[types.length];
                        int i = 0;
                        boolean skipFirst = true;
                        if (types.length > 0 && types[0].isAssignableFrom(Player.class)) {
                            parameters[0] = player;
                            i++;
                        } else
                            skipFirst = false;
                        for (Class<?> parameter : types) {
                            if (skipFirst) {
                                skipFirst = false;
                                continue;
                            }
                            if (args.length < (i - 1))
                                parameters[i] = null;
                            else if (parameter.isAssignableFrom(String.class))
                                parameters[i] = args[i];
                            else if (!CommandHandler.this.hasConverter(parameter))
                                parameters[i] = null;
                            else
                                parameters[i] = CommandHandler.this.getConverter(parameter).convert(args[i]);
                            i++;
                        }
                        boolean isPlayerFirst = types.length > 0 && types[0].isAssignableFrom(Player.class);
                        if (types.length > 0 &&
                            types[types.length - 1] == String.class &&
                            args.length > (isPlayerFirst ? types.length - 1 : types.length)) {
                            int size = args.length;
                            String[] fin = new String[size];
                            int index = 0;
                            for (String arg : args) {
                                if (index < (types.length - (isPlayerFirst ? 2 : 1))) {
                                    index++;
                                    continue;
                                }
                                fin[index] = arg + " ";
                                index++;
                            }
                            String last = fin[fin.length - 1];
                            fin[fin.length - 1] = last.substring(0, last.length() - 1);
                            parameters[parameters.length - 1] = String.join("", fin);
                            try {
                                Object returned = method.invoke(object, parameters);
                                return returned instanceof Boolean bool ? bool : true;
                            } catch (ReflectiveOperationException ex) {
                                Syllesia.getInstance().getLogger().error(ex, CommandHandler.class, 106);
                                return false;
                            }
                        }
                        return true;
                    }
                };
                this.commands.put(hook.name(), command);
            }
        }
        return allSuccess;
    }

    /**
     * Runs a command with the given name and arguments.
     *
     * @param player the player running the command.
     * @param name the name of the command.
     * @param args the arguments of the command.
     * @return true if successful, else false. If command is not found, this will also return false.
     */
    public boolean runCommand(Player player, String name, String[] args) {
        if (!this.commands.containsKey(name))
            return false;
        return this.commands.get(name).runCommand(player, args);
    }

    /**
     * A class to store command logic.
     */
    private static abstract class Command {

        /**
         * Runs a command with the given arguments.
         *
         * @param args the provided arguments.
         * @return true if successful, else false.
         */
        protected abstract boolean runCommand(Player player, String[] args);

    }

}
