package exceptions;

public class SecurityException extends powerHouseException {
    public SecurityException(String message, Throwable cause) {
        super("Security Error: " + message, cause);
    }
}
