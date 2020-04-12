package event.events;

import event.core.EventListener;
import model.CardEnum;

import java.util.List;

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
