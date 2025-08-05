package art.sylleth.syllesia.api.configs;

/**
 * TODO finish when lang file has been completed.
 */
@SuppressWarnings("SpellCheckingInspection")
public enum Placeholder {
    PLAYER_NAME("PNM"),
    ENTITY_NAME("ENTNM"),
    CURRENCY_NAME("CNM");

    // 22dx6 ??
    private static final String WRAP_START = "⋖";
    // 22dx7 ??
    private static final String WRAP_END = "⋗";

    /**
     * Wraps a placeholder in the starting and ending in the wrap characters.
     *
     * @param text the placeholder to wrap.
     * @return the wrapped placeholder.
     */
    private static String wrapPlaceholder(String text) {
        return WRAP_START + text + WRAP_END;
    }

    public static String translate(Placeholder placeholder) {
        return "TODO";
    }

    private final String placeholder;

    /**
     * Sets the placeholders translated value.
     */
    Placeholder(String placeholder) {
        this.placeholder = Placeholder.wrapPlaceholder(placeholder);
    }

    public static String of(Placeholder placeholder) {
        return "";
    }

}
