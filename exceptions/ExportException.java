package exceptions;

public class ExportException extends powerHouseException {
    public ExportException(String message, Throwable cause) {
        super("Erreur lors de l'exportation PDF : " + message, cause);
    }
}
