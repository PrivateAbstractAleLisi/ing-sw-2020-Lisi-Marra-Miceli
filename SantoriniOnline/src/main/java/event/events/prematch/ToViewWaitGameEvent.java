package event.events.prematch;

import event.core.EventListener;
import event.events.GameEvent;

/**
 * sent by the PreGame when another player is choosing a card (challenger or not)
 * the view receiving this event must notify the user that is in waiting status
 * description contains info about who's chosing the card
 * choosingPlayer the player that is choosing the card
 *
 * NB: /TODO esplicitare perchè stai aspettando e chi stai aspettando (scelta carta o scelta CARTE del challenger)
 * tutto in description (ale) è l'unica cosa che stampo quando mi arriva questo evento.
 */
public class ToViewWaitGameEvent extends GameEvent {

    private final String choosingPlayer;

    public ToViewWaitGameEvent(String description, String choosingPlayer) {
        super(description);
        this.choosingPlayer = choosingPlayer;
    }

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }
}
