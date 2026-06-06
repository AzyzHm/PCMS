package exceptions;

public class AlreadyEnrolledException extends powerHouseException {
    public AlreadyEnrolledException(String memberName, String activityName) {
        super("Vous étes dejà inscrit à l'activité ");
    }
}
