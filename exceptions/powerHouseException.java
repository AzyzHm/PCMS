package exceptions;

public class powerHouseException extends Exception {
    public powerHouseException(String message) {
        super(message);
    }

    public powerHouseException(String message, Throwable cause) {
        super(message, cause);
    }
}
