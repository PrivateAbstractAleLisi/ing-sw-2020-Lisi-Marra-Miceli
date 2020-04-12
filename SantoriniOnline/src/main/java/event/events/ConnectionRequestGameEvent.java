package event.events;

import event.core.EventListener;

public class ConnectionRequestGameEvent extends GameEvent {

    private final String IP;
    private final String username;

    public ConnectionRequestGameEvent(String description, String IP, String username) {
        super(description);
        this.IP = IP;
        this.username = username;
    }

    public String getIP() {
        return IP;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }
}
