package event.events;

import event.core.EventListener;
import model.CardEnum;

public class PlayerCardChosenEvent extends GameEvent{
    private CardEnum card;

    public PlayerCardChosenEvent(String username, CardEnum card) {
        super(username);
        this.card = card;
    }

    public CardEnum getCard() {
        return card;
    }

    public String getPlayer() {
        return super.getEventDescription();
    }


    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }
}
