package it.polimi.ingsw.psp58.event.gameEvents;

import it.polimi.ingsw.psp58.event.core.ViewListener;

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
