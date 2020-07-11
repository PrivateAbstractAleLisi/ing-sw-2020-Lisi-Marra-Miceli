package it.polimi.ingsw.psp58.event.gameEvents.prematch;

import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;

/**
 * Event sent by the controller to the client when is another player turn saying why the player has to wait.
 */
public class CV_WaitPreMatchGameEvent extends ViewGameEvent {

    private final String actingPlayer;
    private final String recipient;
    private final String waitCode;

    /*
    waitCode:
    - CHALLENGER_CARDS
    - PLAYER_CARD
    - FIRST_PLAYER
    - PLACE_WORKER
     */

    public CV_WaitPreMatchGameEvent(String description, String actingPlayer, String recipient, String waitCode) {
        super(description);
        this.actingPlayer = actingPlayer;
        this.recipient = recipient;
        this.waitCode = waitCode;
    }

    public String getActingPlayer() {
        return actingPlayer;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getWaitCode() {
        return waitCode;
    }

    @Override
    public void notifyHandler(ViewListener listener) {
        listener.handleEvent(this);
    }
}
