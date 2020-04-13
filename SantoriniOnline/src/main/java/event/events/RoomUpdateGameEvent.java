package event.events;

import event.core.EventListener;

public class RoomUpdateGameEvent extends GameEvent {

    private final String [] usersInRoom;
    private final int roomSize;

    public RoomUpdateGameEvent(String description, String[] usersInRoom, int roomSize) {
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
