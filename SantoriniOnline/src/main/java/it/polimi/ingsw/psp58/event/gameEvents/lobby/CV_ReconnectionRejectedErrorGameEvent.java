package it.polimi.ingsw.psp58.event.gameEvents.lobby;

import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;

public class CV_ReconnectionRejectedErrorGameEvent extends ViewGameEvent {

    private final String errorMessage;
    private final String errorCode;
    private final String username;

    //USER_TAKEN
    //ROOM_FULL


    public CV_ReconnectionRejectedErrorGameEvent(String description, String errorCode, String errorMessage, String username) {
        super(description);
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.username = username;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getUsername() {
        return username;
    }

    public void notifyHandler(ViewListener listener) {
        listener.handleEvent(this);
    }

}
