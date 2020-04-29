package it.polimi.ingsw.psp58.event.gameEvents.lobby;

import it.polimi.ingsw.psp58.event.core.EventListener;
import it.polimi.ingsw.psp58.event.gameEvents.GameEvent;
import it.polimi.ingsw.psp58.view.VirtualView;

import java.net.InetAddress;

public class CC_ConnectionRequestGameEvent extends GameEvent {

    private final InetAddress userIP;
    private final int userPort;
    private final String username;
    private final VirtualView virtualView;

    public CC_ConnectionRequestGameEvent(String description, InetAddress userIP, int userPort, VirtualView clientVV, String username) {
        super(description);
        this.userIP = userIP;
        this.userPort = userPort;
        this.virtualView = clientVV;
        this.username = username;
    }

    public InetAddress getUserIP() {
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
