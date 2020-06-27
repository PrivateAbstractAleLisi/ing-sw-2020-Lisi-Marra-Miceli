package it.polimi.ingsw.psp58.event.gameEvents.lobby;

import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;

/**
 * Event sent by the Controller to the client asking to input the size of the room to create.
 */
public class CV_RoomSizeRequestGameEvent extends ViewGameEvent {
    private String username;
    public CV_RoomSizeRequestGameEvent(String description, String username) {
        super(description);
        this.username=username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void notifyHandler(ViewListener listener) {
        listener.handleEvent(this);
    }
}
