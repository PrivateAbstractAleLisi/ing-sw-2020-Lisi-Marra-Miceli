package event.gameEvents.lobby;

import event.core.EventListener;
import event.gameEvents.GameEvent;

public class CV_RoomSizeRequestGameEvent extends GameEvent {
    private String username;
    public CV_RoomSizeRequestGameEvent(String description, String username) {
        super(description);
        this.username=username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }
}
