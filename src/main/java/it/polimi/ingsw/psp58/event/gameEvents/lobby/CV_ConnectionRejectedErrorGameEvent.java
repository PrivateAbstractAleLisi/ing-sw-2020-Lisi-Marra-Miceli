package it.polimi.ingsw.psp58.event.gameEvents.lobby;

import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;

import java.net.InetAddress;

/**
 * Event sent by the Controller to the client saying that the connection has not established.
 * Contains the error message and the error code
 */
public class CV_ConnectionRejectedErrorGameEvent extends ViewGameEvent {

    private final String errorMessage;
    private final String errorCode;
    private final InetAddress userIP;
    private final int userPort;
    private final String wrongUsername;
    //USER_TAKEN
    //ROOM_FULL


    public CV_ConnectionRejectedErrorGameEvent(String description, String errorCode, String errorMessage, InetAddress userIP, int userPort, String wrongUsername) {
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

    public InetAddress getUserIP() {
        return userIP;
    }

    public String getWrongUsername() {
        return wrongUsername;
    }


    @Override
    public void notifyHandler(ViewListener listener) {
        listener.handleEvent(this);
    }
}
