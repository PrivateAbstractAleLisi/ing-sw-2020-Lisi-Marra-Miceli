package event.gameEvents;

import event.core.EventListener;

public class PingEvent extends GameEvent {

    public PingEvent(String description) {
        super(description);
    }

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }
}
