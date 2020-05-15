package it.polimi.ingsw.psp58.event.gameEvents.prematch;

import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;

/**
 * sent by the PreGame when another player is choosing a card (challenger or not)
 * the it.polimi.ingsw.sp58.view receiving this it.polimi.ingsw.sp58.event must notify the user that is in waiting status
 * description contains info about who's chosing the card
 * choosingPlayer the player that is choosing the card
 * <p>
 * NB: /TODO esplicitare perchè stai aspettando e chi stai aspettando (scelta carta o scelta CARTE del challenger)
 * tutto in description (ale) è l'unica cosa che stampo quando mi arriva questo evento.
 */
public class CV_WaitPreMatchGameEvent extends ViewGameEvent {

    private final String actingPlayer;
    private final String recipient;
    private final String challenger;
    private final String waitCode;

    public CV_WaitPreMatchGameEvent(String description, String actingPlayer, String recipient) {
        super(description);
        this.actingPlayer = actingPlayer;
        this.recipient = recipient;
        this.challenger = null;
        this.waitCode = "GENERIC_WAIT";
    }

    public CV_WaitPreMatchGameEvent(String description, String actingPlayer, String recipient, String challenger, String waitCode) {
        super(description);
        this.actingPlayer = actingPlayer;
        this.recipient = recipient;
        this.challenger = challenger;
        this.waitCode = waitCode;
    }

    @Override
    public String getEventDecription() {
        return super.getEventDecription();
    }

    public String getActingPlayer() {
        return actingPlayer;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getChallenger() {
        return challenger;
    }

    public String getWaitCode() {
        return waitCode;
    }

    @Override
    public void notifyHandler(ViewListener listener) {
        listener.handleEvent(this);
    }
}
