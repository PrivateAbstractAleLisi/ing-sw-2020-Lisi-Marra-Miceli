package it.polimi.ingsw.psp58.event.gameEvents.match;

import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;
import it.polimi.ingsw.psp58.model.gamemap.Worker;

/**
 * sent by the Game when a command is executed
 * Contain the worker that was used
 */
public class CV_CommandExecutedGameEvent extends ViewGameEvent {
    private final String recipient;

    public CV_CommandExecutedGameEvent(String description, String recipient) {
        super(description);
        this.recipient = recipient;
    }

    public String getRecipient() {
        return recipient;
    }

    @Override
    public void notifyHandler(ViewListener listener) {
        listener.handleEvent(this);
    }
}
