package it.polimi.ingsw.psp58.event.gameEvents.lobby;

import it.polimi.ingsw.psp58.event.core.ControllerListener;
import it.polimi.ingsw.psp58.event.gameEvents.ControllerGameEvent;
import it.polimi.ingsw.psp58.view.VirtualView;

import java.net.InetAddress;

/**
 * Event sent by the {@link VirtualView} to the Controller to make a connection request for a player.
 * Contains the username of the player, port, ip address and the virtualview reference.
 */
public class CC_ConnectionRequestGameEvent extends ControllerGameEvent {

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
    public void notifyHandler(ControllerListener listener) {
        listener.handleEvent(this);
    }
}
