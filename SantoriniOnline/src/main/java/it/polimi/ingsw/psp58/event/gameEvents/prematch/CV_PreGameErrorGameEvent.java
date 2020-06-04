package it.polimi.ingsw.psp58.event.gameEvents.prematch;

import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;

public class CV_PreGameErrorGameEvent extends ViewGameEvent {

    private final String toUsername;

    public CV_PreGameErrorGameEvent(String description, String toUsername) {
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
