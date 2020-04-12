package event.events;

import event.core.EventListener;

public class ConnectionRequestServerGameEvent extends GameEvent {

    private final String userIP;
    private final int userPort;
    private final String username;

    public ConnectionRequestServerGameEvent(String description, String userIP, int userPort, String username) {
        super(description);
        this.userIP = userIP;
        this.userPort = userPort;
        this.username = username;
    }

    public String getUserIP() {
        return userIP;
    }

    public int getUserPort() {
        return userPort;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }
}
