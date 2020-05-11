package it.polimi.ingsw.psp58.event.gameEvents.prematch;

import it.polimi.ingsw.psp58.event.core.ControllerListener;
import it.polimi.ingsw.psp58.event.gameEvents.ControllerGameEvent;
import it.polimi.ingsw.psp58.model.CardEnum;

import java.util.List;

/**
 * sent by the challenger client it.polimi.ingsw.sp58.view to the Server when a choice of 2 or 3 cards has been made
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
