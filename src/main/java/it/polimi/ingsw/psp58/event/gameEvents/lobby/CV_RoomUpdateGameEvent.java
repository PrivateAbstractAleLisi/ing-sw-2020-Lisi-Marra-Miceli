package it.polimi.ingsw.psp58.event.gameEvents.lobby;

import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;

/**
 * Event sent by the controller to the client when the player or another player enters in the room he is in.
 * Contains info about room such as the room size, the room name and the name of the other players in the room.
 */
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
