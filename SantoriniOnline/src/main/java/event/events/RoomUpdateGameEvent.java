package event.events;

import event.core.EventListener;

public class RoomUpdateGameEvent extends GameEvent {

    private final String [] usersInRoom;

    public RoomUpdateGameEvent(String description, String[] usersInRoom) {
        super(description);
        this.usersInRoom=usersInRoom;
    }

    public String[] getUsersInRoom() {
        return usersInRoom;
    }

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }
}
