package exceptions;

public class AuthenticationException extends powerHouseException {
    public AuthenticationException(String message) {
        super("Erreur d'authentification: " + message);
    }
}
