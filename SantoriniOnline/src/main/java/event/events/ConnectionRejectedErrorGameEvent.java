package event.events;

import event.core.EventListener;

public class ConnectionRejectedErrorGameEvent extends GameEvent {

    private final String errorMessage, wrongUsername;
    private final String errorCode;
    //USER_TAKEN
    //ROOM_FULL


    public ConnectionRejectedErrorGameEvent(String description, String errorCode, String errorMessage, String wrongUsername) {
        super(description);
        this.errorMessage = errorMessage;
        this.wrongUsername = wrongUsername;
        this.errorCode = errorCode;
    }

    public String getWrongUsername() {
        return wrongUsername;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }
}
