package it.polimi.ingsw.psp58.event.gameEvents;

import it.polimi.ingsw.psp58.event.core.EventListener;

public class VC_NewGameResponseEvent extends GameEvent {
    private final boolean createNewGame;

    public VC_NewGameResponseEvent(String description, boolean createNewGame) {
        super(description);
        this.createNewGame=createNewGame;
    }

    public boolean isCreateNewGame() {
        return createNewGame;
    }

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }
}
