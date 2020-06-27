package it.polimi.ingsw.psp58.event.gameEvents.match;

import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;

/**
 * Event sent by the controller to the client saying what is happening. Contains a description and the name of the acting player.
 */
public class CV_WaitMatchGameEvent extends ViewGameEvent {

    private final String actingPlayer;
    private final String recipient;

    public CV_WaitMatchGameEvent(String description, String actingPlayer, String recipient) {
        super(description);
        this.actingPlayer = actingPlayer;
        this.recipient = recipient;
    }

    public String getActingPlayer() {
        return actingPlayer;
    }

    public String getRecipient() {
        return recipient;
    }

    @Override
    public void notifyHandler(ViewListener listener) {
        listener.handleEvent(this);
    }
}
