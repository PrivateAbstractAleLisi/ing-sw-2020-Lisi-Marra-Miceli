package event.gameEvents.prematch;

import event.core.EventListener;
import event.gameEvents.GameEvent;
import model.CardEnum;

import java.util.List;

/**
 * sent by the challenger client view to the Server when a choice of 2 or 3 cards has been made
 */
public class VC_ChallengerCardsChosenEvent extends GameEvent {
    private List<CardEnum> cardsChosen;

    public VC_ChallengerCardsChosenEvent(String description, List<CardEnum> cardsChosen) {
        super(description);
        this.cardsChosen = cardsChosen;
    }

    public List<CardEnum> getCardsChosen() {
        return cardsChosen;
    }

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }
}
