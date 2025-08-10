package art.sylleth.syllesia.files.json;

/**
 * Exception to be thrown when there's an issue with JSON parsing.
 */
public class MalformedJsonException extends RuntimeException {

    public MalformedJsonException() {
        super("There was an issue parsing your JSON.");
    }

    public MalformedJsonException(String message) {
        super(message);
    }

}
