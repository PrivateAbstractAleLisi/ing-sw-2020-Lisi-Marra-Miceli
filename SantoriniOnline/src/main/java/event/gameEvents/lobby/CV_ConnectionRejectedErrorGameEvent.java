package event.gameEvents.lobby;

import event.core.EventListener;
import event.gameEvents.GameEvent;

public class CV_ConnectionRejectedErrorGameEvent extends GameEvent {

    private final String errorMessage;
    private final String errorCode;
    private final String userIP;
    private final int userPort;
    private final String wrongUsername;
    //USER_TAKEN
    //ROOM_FULL


    public CV_ConnectionRejectedErrorGameEvent(String description, String errorCode, String errorMessage, String userIP, int userPort, String wrongUsername) {
        super(description);
        this.errorMessage = errorMessage;
        this.userIP = userIP;
        this.userPort = userPort;
        this.errorCode = errorCode;
        this.wrongUsername = wrongUsername;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getUserPort() {
        return userPort;
    }

    public String getUserIP() {
        return userIP;
    }

    public String getWrongUsername() {
        return wrongUsername;
    }


    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }
}
