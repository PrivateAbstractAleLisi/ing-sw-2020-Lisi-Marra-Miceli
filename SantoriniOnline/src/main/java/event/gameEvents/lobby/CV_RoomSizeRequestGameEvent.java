package event.gameEvents.lobby;

import event.core.EventListener;
import event.gameEvents.GameEvent;

public class CV_RoomSizeRequestGameEvent extends GameEvent {
    public CV_RoomSizeRequestGameEvent(String description) {
        super(description);
    }

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }
}
