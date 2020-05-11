package it.polimi.ingsw.psp58.event.gameEvents;

import it.polimi.ingsw.psp58.event.core.ViewListener;

public class PlayerDisconnectedViewEvent extends ViewGameEvent {

    String disconnectedUsername;
    String reason;

    public PlayerDisconnectedViewEvent(String description, String disconnectedUsername, String reason) {
        super(description);
        this.disconnectedUsername = disconnectedUsername;
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public String getDisconnectedUsername() {
        return disconnectedUsername;
    }

    @Override
    public void notifyHandler(ViewListener listener) {
        listener.handleEvent(this);
    }
}
