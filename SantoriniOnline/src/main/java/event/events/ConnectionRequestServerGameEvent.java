package event.events;

import event.core.EventListener;
import view.VirtualView;

public class ConnectionRequestServerGameEvent extends GameEvent {

    private final String userIP;
    private final int userPort;
    private final String username;
    private final VirtualView virtualView;

    public ConnectionRequestServerGameEvent(String description, String userIP, int userPort, VirtualView virtualView, String username) {
        super(description);
        this.userIP = userIP;
        this.userPort = userPort;
        this.virtualView=virtualView;
        this.username = username;
    }

    public String getUserIP() {
        return userIP;
    }

    public int getUserPort() {
        return userPort;
    }

    public VirtualView getVirtualView() {
        return virtualView;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }
}
