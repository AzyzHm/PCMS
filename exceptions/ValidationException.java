package exceptions;

public class ValidationException extends powerHouseException{
    public ValidationException(String field, String message) {
        super("Erreur de validation dans le champ '" + field + "': " + message);
    }
}
