package exceptions;

public class DatabaseException extends powerHouseException {
    public DatabaseException(String message, Throwable cause) {
        super("Database Error: " + message, cause);
    }
}
