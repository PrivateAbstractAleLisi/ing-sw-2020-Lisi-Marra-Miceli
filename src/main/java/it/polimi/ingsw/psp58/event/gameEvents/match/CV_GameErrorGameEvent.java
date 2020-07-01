package it.polimi.ingsw.psp58.event.gameEvents.match;

import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;

/**
 * Event sent by the controller to the client when an error has occurred during the actual game.
 * Contains the description of what happened.
 */
public class CV_GameErrorGameEvent extends ViewGameEvent {

    private final String toUsername;

    public CV_GameErrorGameEvent(String description, String toUsername) {
        super(description);
        this.toUsername = toUsername;
    }

    @Override
    public void notifyHandler(ViewListener listener) {
        listener.handleEvent(this);
    }

    public String getToUsername() {
        return toUsername;
    }
}
