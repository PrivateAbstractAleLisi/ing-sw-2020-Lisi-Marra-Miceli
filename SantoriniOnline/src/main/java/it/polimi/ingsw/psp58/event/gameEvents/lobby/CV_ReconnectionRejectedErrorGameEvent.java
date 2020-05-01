package it.polimi.ingsw.psp58.event.gameEvents.lobby;

import it.polimi.ingsw.psp58.event.core.EventListener;
import it.polimi.ingsw.psp58.event.gameEvents.GameEvent;

import java.net.InetAddress;

public class CV_ReconnectionRejectedErrorGameEvent extends GameEvent {

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

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }
}
