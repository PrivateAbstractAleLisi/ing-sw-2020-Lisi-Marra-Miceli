package it.polimi.ingsw.psp58.event.gameEvents.lobby;

import it.polimi.ingsw.psp58.event.core.EventListener;
import it.polimi.ingsw.psp58.event.gameEvents.GameEvent;
import it.polimi.ingsw.psp58.view.VirtualView;

public class CC_NewGameResponseEvent extends GameEvent {
    private final String username;
    private final VirtualView virtualView;

    public CC_NewGameResponseEvent(String description, String username, VirtualView virtualView) {
        super(description);
        this.username = username;
        this.virtualView = virtualView;
    }

    public String getUsername() {
        return username;
    }

    public VirtualView getVirtualView() {
        return virtualView;
    }

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }
}
