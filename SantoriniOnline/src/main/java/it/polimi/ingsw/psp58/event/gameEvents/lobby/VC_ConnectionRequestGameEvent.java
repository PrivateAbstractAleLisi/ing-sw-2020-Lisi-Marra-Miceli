package it.polimi.ingsw.psp58.event.gameEvents.lobby;

import it.polimi.ingsw.psp58.event.core.EventListener;
import it.polimi.ingsw.psp58.event.gameEvents.GameEvent;

public class VC_ConnectionRequestGameEvent extends GameEvent {

    private final String IP;
    private final int port;
    private final String username;

    public VC_ConnectionRequestGameEvent(String description, String IP, int port, String username) {
        super(description);
        this.IP = IP;
        this.port=port;
        this.username = username;
    }

    public String getIP() {
        return IP;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }
}
