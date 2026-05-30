package exceptions;

public class AlreadyEnrolledException extends powerHouseException {
    public AlreadyEnrolledException(String memberName, String activityName) {
        super("Le membre " + memberName + " est déjà inscrit à l'activité '" + activityName + "'.");
    }
}
