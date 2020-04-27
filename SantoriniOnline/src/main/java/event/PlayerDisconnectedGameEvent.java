package event;

import event.core.EventListener;
import event.gameEvents.GameEvent;

public class PlayerDisconnectedGameEvent extends GameEvent {

    String disconnectedUsername;
    String reason;

    public PlayerDisconnectedGameEvent(String description, String disconnectedUsername, String reason) {
        super(description);
        this.disconnectedUsername = disconnectedUsername;
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public String getDisconnectedUsername() {
        return disconnectedUsername;
    }

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }
}
