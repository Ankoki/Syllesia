package art.sylleth.syllesia.misc;

import art.sylleth.syllesia.Syllesia;
import art.sylleth.syllesia.api.configs.Settings;
import art.sylleth.syllesia.files.ConfigurationFile;

/**
 * Used for sending messages to the console.
 */
public class Logger {

    // Console colours.
    private static final String YELLOW = "\033[0;33m",
                                RED = "\033[0;31m",
                                BLUE = "\033[0;34m";

    /**
     * Logs the given debug strings.
     *
     * @param message the messages to print.
     */
    public void debug(String... message) {
        Settings settings = (Settings) Syllesia.getInstance().getConfiguration(ConfigurationFile.SETTINGS);
        if (settings == null || settings.isDebug())
            for (String s : message)
                System.out.println(YELLOW + "∴ Syllesia [DEBUG] " + s); // Different colour for debug maybe?
    }

    /**
     * Sends information to the console.
     *
     * @param message the information to send to console.
     */
    public void info(String message) {
        System.out.println(BLUE + "∴ Syllesia [INFO] " + message);
    }

    /**
     * Sends a warning to the console.
     *
     * @param message the warning to send.
     */
    public void warn(String message) {
        System.out.println(YELLOW + "∴ Syllesia [WARN] " + message);
    }

    /**
     * Used to tell the console there has been an extreme error within the program.
     *
     * @param ex the exception thrown.
     * @param clazz the class of this exception.
     * @param line the line the exception is being caught on.
     */
    public void error(Exception ex, Class<?> clazz, int line) {
        System.out.println(RED + "∴ Syllesia # ");
        System.out.println(RED + "∴ Syllesia # There was a severe error with Syllesia.");
        System.out.println(RED + "∴ Syllesia # If you see this, please report this to the developer with the following information.");
        System.out.println(RED + "∴ Syllesia # ");
        System.out.println(RED + "∴ Syllesia # " + ex.toString());
        System.out.println(RED + "∴ Syllesia # ");
        for (StackTraceElement element : ex.getStackTrace())
            System.out.println(RED + "∴ Syllesia # " + element.toString());
        System.out.println(RED + "∴ Syllesia # ");
        System.out.println(RED + "∴ Syllesia # Version: " + Syllesia.getInstance().getVersion());
        System.out.println(RED + "∴ Syllesia # Class: " + clazz.getName());
        System.out.println(RED + "∴ Syllesia # Line: " + line);
        System.out.println(RED + "∴ Syllesia # ");
    }

}
