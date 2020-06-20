package it.polimi.ingsw.psp58.event.gameEvents.lobby;

import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;

public class CV_RoomUpdateGameEvent extends ViewGameEvent {

    private final String [] usersInRoom;
    private final String roomName;
    private final int roomSize;

    public CV_RoomUpdateGameEvent(String description, String[] usersInRoom, int roomSize, String roomName) {
        super(description);
        this.usersInRoom=usersInRoom;
        this.roomSize = roomSize;
        this.roomName=roomName;
    }

    public String[] getUsersInRoom() {
        return usersInRoom;
    }

    public int getRoomSize() {
        return roomSize;
    }

    public String getRoomName() {
        return roomName;
    }

    @Override
    public void notifyHandler(ViewListener listener) {
        listener.handleEvent(this);
    }
}
