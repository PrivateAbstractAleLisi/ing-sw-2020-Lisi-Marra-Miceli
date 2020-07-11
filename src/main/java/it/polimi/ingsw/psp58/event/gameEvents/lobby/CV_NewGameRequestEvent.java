package it.polimi.ingsw.psp58.event.gameEvents.lobby;

import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;

/**
 * Event sent by the Controller to the client asking for a new game after one has just finished.
 */
public class CV_NewGameRequestEvent extends ViewGameEvent {
    public CV_NewGameRequestEvent(String description) {
        super(description);
    }

    @Override
    public void notifyHandler(ViewListener listener) {
        listener.handleEvent(this);
    }
}
