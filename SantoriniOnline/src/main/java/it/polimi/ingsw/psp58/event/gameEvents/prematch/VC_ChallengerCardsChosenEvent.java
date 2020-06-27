package it.polimi.ingsw.psp58.event.gameEvents.prematch;

import it.polimi.ingsw.psp58.event.core.ControllerListener;
import it.polimi.ingsw.psp58.event.gameEvents.ControllerGameEvent;
import it.polimi.ingsw.psp58.model.CardEnum;

import java.util.List;

/**
 * Event sent by the client to the server when the challenger has chosen the cards.
 * Contains the list of the cards chosen by the challenger.
 */
public class VC_ChallengerCardsChosenEvent extends ControllerGameEvent {
    private List<CardEnum> cardsChosen;

    public VC_ChallengerCardsChosenEvent(String description, List<CardEnum> cardsChosen) {
        super(description);
        this.cardsChosen = cardsChosen;
    }

    public List<CardEnum> getCardsChosen() {
        return cardsChosen;
    }


    @Override
    public void notifyHandler(ControllerListener listener) {
        listener.handleEvent(this);
    }
}
