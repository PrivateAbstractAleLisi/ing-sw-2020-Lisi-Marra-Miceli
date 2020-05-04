package it.polimi.ingsw.psp58.event.gameEvents.lobby;

import it.polimi.ingsw.psp58.event.core.ControllerListener;
import it.polimi.ingsw.psp58.event.gameEvents.ControllerGameEvent;

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
