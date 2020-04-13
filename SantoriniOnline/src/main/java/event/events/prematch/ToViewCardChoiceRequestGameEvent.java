package event.events.prematch;

import event.core.EventListener;
import event.events.GameEvent;
import model.CardEnum;

import java.util.List;

/**
 * sent by the PreGame when a player must choose a card to play the game with
 * the view receiving this event must notify the user and handle a card choice FROM the remaining cards
 * choosingPlayer the player that is choosing the card
 * available cards: the list of cards from which the user must choose his god.

 */
public class ToViewCardChoiceRequestGameEvent extends GameEvent {

    private List <CardEnum> availableCards;

    public ToViewCardChoiceRequestGameEvent(String description, List<CardEnum> availableCards) {
        super(description);
        this.availableCards = availableCards;
    }

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }
}
