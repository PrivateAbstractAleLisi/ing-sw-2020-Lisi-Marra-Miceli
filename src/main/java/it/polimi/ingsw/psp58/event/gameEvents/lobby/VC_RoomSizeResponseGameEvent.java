package it.polimi.ingsw.psp58.event.gameEvents.lobby;

import it.polimi.ingsw.psp58.event.core.ControllerListener;
import it.polimi.ingsw.psp58.event.gameEvents.ControllerGameEvent;

/**
 * Event sent by the client to the server responding to a {@link CV_RoomSizeRequestGameEvent}.
 * Contains the size of the room the player wants to create.
 */
public class VC_RoomSizeResponseGameEvent extends ControllerGameEvent {

    private final int size;

    public VC_RoomSizeResponseGameEvent(String description, int size) {
        super(description);
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    @Override
    public void notifyHandler(ControllerListener listener) {
        listener.handleEvent(this);
    }


}
