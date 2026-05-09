package exceptions;

public class ValidationException extends powerHouseException{
    public ValidationException(String field, String message) {
        super("Validation error in field '" + field + "': " + message);
    }
}
