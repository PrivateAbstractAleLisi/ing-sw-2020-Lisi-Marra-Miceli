package event.gameEvents;

import event.core.EventListener;

/**
 * sent by the PreGame when another player is choosing a card (challenger or not)
 * the view receiving this event must notify the user that is in waiting status
 * description contains info about who's chosing the card
 * choosingPlayer the player that is choosing the card
 *
 * NB: /TODO esplicitare perchè stai aspettando e chi stai aspettando (scelta carta o scelta CARTE del challenger)
 * tutto in description (ale) è l'unica cosa che stampo quando mi arriva questo evento.
 */
public class CV_WaitGameEvent extends GameEvent {

    private final String actingPlayer;
    private final String recipient;

    public CV_WaitGameEvent(String description, String actingPlayer, String recipient) {
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
