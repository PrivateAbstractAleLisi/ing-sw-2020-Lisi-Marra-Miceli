package it.polimi.ingsw.psp58.event.gameEvents.prematch;

import it.polimi.ingsw.psp58.event.core.EventListener;
import it.polimi.ingsw.psp58.event.gameEvents.GameEvent;

/**
 * sent by the PreGame when another player is choosing a card (challenger or not)
 * the it.polimi.ingsw.sp58.view receiving this it.polimi.ingsw.sp58.event must notify the user that is in waiting status
 * description contains info about who's chosing the card
 * choosingPlayer the player that is choosing the card
 *
 * NB: /TODO esplicitare perchè stai aspettando e chi stai aspettando (scelta carta o scelta CARTE del challenger)
 * tutto in description (ale) è l'unica cosa che stampo quando mi arriva questo evento.
 */
public class CV_WaitPreMatchGameEvent extends GameEvent {

    private final String actingPlayer;
    private final String recipient;

    public CV_WaitPreMatchGameEvent(String description, String actingPlayer, String recipient) {
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
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }
}
