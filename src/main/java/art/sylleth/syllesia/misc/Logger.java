package art.sylleth.syllesia.misc;

public class Logger {

    public static void debug(String... message) {
        for (String s : message)
            System.out.println("[DEBUG] " + s);
    }

    public static void info(String message) {
        System.out.println("[INFO] " + message);
    }

    public static void warn(String message) {
        System.out.println("[WARN] " + message);
    }

}
