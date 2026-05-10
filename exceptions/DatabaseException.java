package exceptions;

public class DatabaseException extends powerHouseException {
    public DatabaseException(String message, Throwable cause) {
        super("Erreur de base de données: " + message, cause);
    }
}
