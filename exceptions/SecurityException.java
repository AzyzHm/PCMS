package exceptions;

public class SecurityException extends powerHouseException {
    public SecurityException(String message, Throwable cause) {
            super("Erreur de sécurité: " + message, cause);
        }
}
