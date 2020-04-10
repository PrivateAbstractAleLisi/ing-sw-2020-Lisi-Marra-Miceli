package event.events;

/**
 * @author Alessandro Lisi
 * event generate when an invalid username reaches the server, for example: already taken, too short, invalid characters
 */
public class InvalidUsernameGameEvent extends GameEvent {

    private final String errorMessage, referredToPlayerUsername;

    public InvalidUsernameGameEvent(String description, String errorMessage, String referredToPlayerUsername) {
        super(description);
        this.errorMessage = errorMessage;
        this.referredToPlayerUsername = referredToPlayerUsername;
    }


    public String getErrorMessage() {
        return errorMessage;
    }

    public String getReferredToPlayerUsername() {
        return referredToPlayerUsername;
    }
}
