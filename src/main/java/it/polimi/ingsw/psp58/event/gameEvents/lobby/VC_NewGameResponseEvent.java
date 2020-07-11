package it.polimi.ingsw.psp58.event.gameEvents.lobby;

import it.polimi.ingsw.psp58.event.core.ControllerListener;
import it.polimi.ingsw.psp58.event.gameEvents.ControllerGameEvent;

/**
 * Event sent by the client to the server responding to a {@link CV_NewGameRequestEvent} saying if he wants or not to play another game.
 */
public class VC_NewGameResponseEvent extends ControllerGameEvent {
    private final boolean createNewGame;

    public VC_NewGameResponseEvent(String description, boolean createNewGame) {
        super(description);
        this.createNewGame=createNewGame;
    }

    public boolean createNewGame() {
        return createNewGame;
    }

    @Override
    public void notifyHandler(ControllerListener listener) {
        listener.handleEvent(this);
    }
}
