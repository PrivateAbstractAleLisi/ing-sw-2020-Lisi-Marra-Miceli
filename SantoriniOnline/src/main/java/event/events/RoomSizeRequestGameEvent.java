package event.events;

import event.core.EventListener;

public class RoomSizeRequestGameEvent extends GameEvent {
    public RoomSizeRequestGameEvent(String description) {
        super(description);
    }

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }
}
