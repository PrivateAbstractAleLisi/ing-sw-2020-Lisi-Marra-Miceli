package it.polimi.ingsw.psp58.event.gameEvents;

import it.polimi.ingsw.psp58.event.core.EventListener;

public class CV_NewGameRequestEvent extends GameEvent {
    public CV_NewGameRequestEvent(String description) {
        super(description);
    }

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }
}
