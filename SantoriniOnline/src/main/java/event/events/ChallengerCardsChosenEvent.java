package event.events;

import event.core.EventListener;
import model.CardEnum;

import java.util.List;

/**
 * sent by the challenger client view a correct choice of 2 or 3 cards has been made
 */
public class ChallengerCardsChosenEvent extends GameEvent {
    private List<CardEnum> cardsChosen;

    public ChallengerCardsChosenEvent(String description, List<CardEnum> cardsChosen) {
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
