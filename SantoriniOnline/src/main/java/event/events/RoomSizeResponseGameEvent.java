package event.events;

import event.core.EventListener;

public class RoomSizeResponseGameEvent extends GameEvent {

    private final int size;

    public int getSize() {
        return size;
    }

    @Override
    public String getEventDescription() {
        return super.getEventDescription();
    }

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }

    public RoomSizeResponseGameEvent(String description, int size) {
        super(description);
        this.size = size;
    }
}
