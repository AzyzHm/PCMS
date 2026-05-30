package exceptions;

public class ActivityFullException extends powerHouseException {
    public ActivityFullException(String activityName) {
        super("L'activité '" + activityName + "' a atteint sa capacité maximale. Inscription impossible.");
    }
}