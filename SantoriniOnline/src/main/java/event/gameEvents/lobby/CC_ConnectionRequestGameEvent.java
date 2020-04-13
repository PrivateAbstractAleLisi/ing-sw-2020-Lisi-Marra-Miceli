package event.gameEvents.lobby;

import event.core.EventListener;
import event.gameEvents.GameEvent;
import view.VirtualView;

public class CC_ConnectionRequestGameEvent extends GameEvent {

    private final String userIP;
    private final int userPort;
    private final String username;
    private final VirtualView virtualView;

    public CC_ConnectionRequestGameEvent(String description, String userIP, int userPort, VirtualView virtualView, String username) {
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
