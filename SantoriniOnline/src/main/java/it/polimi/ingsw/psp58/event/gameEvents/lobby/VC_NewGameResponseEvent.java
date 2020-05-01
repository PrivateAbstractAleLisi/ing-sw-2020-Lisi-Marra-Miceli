package it.polimi.ingsw.psp58.event.gameEvents.lobby;

import it.polimi.ingsw.psp58.event.core.EventListener;
import it.polimi.ingsw.psp58.event.gameEvents.GameEvent;

public class VC_NewGameResponseEvent extends GameEvent {
    private final boolean createNewGame;

    public VC_NewGameResponseEvent(String description, boolean createNewGame) {
        super(description);
        this.createNewGame=createNewGame;
    }

    public boolean createNewGame() {
        return createNewGame;
    }

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }
}
