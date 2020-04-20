package event.gameEvents;

import event.core.EventListener;

public class CV_GameErrorGameEvent extends GameEvent {

    private final String toUsername;

    public CV_GameErrorGameEvent(String description, String toUsername) {
        super(description);
        this.toUsername = toUsername;
    }

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }

    @Override
    public String getEventDescription() {
        return super.getEventDescription();
    }

    public String getToUsername() {
        return toUsername;
    }
}
