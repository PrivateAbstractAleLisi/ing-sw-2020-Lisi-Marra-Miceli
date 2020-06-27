package it.polimi.ingsw.psp58.event.gameEvents.lobby;

import it.polimi.ingsw.psp58.event.core.ControllerListener;
import it.polimi.ingsw.psp58.event.gameEvents.ControllerGameEvent;
import it.polimi.ingsw.psp58.view.VirtualView;

/**
 * Event sent to the controller when a game has just finished. Responds to a {@link CV_NewGameRequestEvent}.
 */
public class CC_NewGameResponseEvent extends ControllerGameEvent {
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
    public void notifyHandler(ControllerListener listener) {
        listener.handleEvent(this);
    }
}
