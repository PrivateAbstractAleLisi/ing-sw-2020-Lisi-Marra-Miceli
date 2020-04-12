package event.core;

import event.events.ConnectionRejectedErrorGameEvent;
import event.events.RoomSizeRequestGameEvent;

public interface ViewEventListener extends EventListener {
    public void handleEvent(RoomSizeRequestGameEvent event);
    public void handleEvent(ConnectionRejectedErrorGameEvent event);
}
