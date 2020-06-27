package it.polimi.ingsw.psp58.event.gameEvents.prematch;

import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;
import it.polimi.ingsw.psp58.model.CardEnum;

import java.util.List;

/**
 * Event sent by the controller to the client when the player has to choose a card.
 * Contains the list of the card the player can choose from and the already picked ones.
 */
public class CV_CardChoiceRequestGameEvent extends ViewGameEvent {

    private List <CardEnum> availableCards;
    private List <CardEnum> usedCards;
    private String username;

    public CV_CardChoiceRequestGameEvent(String description, List<CardEnum> availableCards, String username) {
        super(description);
        this.availableCards = availableCards;
        this.username=username;
        this.usedCards=null;
    }
    public CV_CardChoiceRequestGameEvent(String description, List<CardEnum> availableCards, List<CardEnum> usedCards, String username) {
        super(description);
        this.availableCards = availableCards;
        this.username=username;
        this.usedCards=usedCards;
    }

    public List<CardEnum> getAvailableCards() {
        return availableCards;
    }

    public String getUsername() {
        return username;
    }

    public List<CardEnum> getUsedCards() {
        return usedCards;
    }

    @Override
    public void notifyHandler(ViewListener listener) {
        listener.handleEvent(this);
    }
}
