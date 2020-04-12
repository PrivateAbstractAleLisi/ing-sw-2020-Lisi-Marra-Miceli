package event.events;

import event.core.EventListener;

public class ConnectionSuccessfulGameEvent extends GameEvent {

    private final String username;

    public ConnectionSuccessfulGameEvent(String description, String username) {
        super(description);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }


    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }
}
