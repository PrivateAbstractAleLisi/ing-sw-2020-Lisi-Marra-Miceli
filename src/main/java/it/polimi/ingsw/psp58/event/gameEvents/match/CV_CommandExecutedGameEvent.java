package it.polimi.ingsw.psp58.event.gameEvents.match;

import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;

/**
 * Event sent by the server to the client when a command is actually executed.
 * Contains the worker that was used for the command.
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
