package it.polimi.ingsw.psp58.event.gameEvents.lobby;

import it.polimi.ingsw.psp58.event.core.EventListener;
import it.polimi.ingsw.psp58.event.gameEvents.GameEvent;

public class CV_RoomUpdateGameEvent extends GameEvent {

    private final String [] usersInRoom;
    private final int roomSize;

    public CV_RoomUpdateGameEvent(String description, String[] usersInRoom, int roomSize) {
        super(description);
        this.usersInRoom=usersInRoom;
        this.roomSize = roomSize;
    }

    public String[] getUsersInRoom() {
        return usersInRoom;
    }

    public int getRoomSize() {
        return roomSize;
    }

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }
}
