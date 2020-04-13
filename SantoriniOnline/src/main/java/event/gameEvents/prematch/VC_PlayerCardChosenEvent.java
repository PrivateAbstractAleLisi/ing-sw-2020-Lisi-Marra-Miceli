package event.gameEvents.prematch;

import event.core.EventListener;
import event.gameEvents.GameEvent;
import model.CardEnum;

/**
 * By View to controller, sent when the current player has chosen the card to play with
 */
public class VC_PlayerCardChosenEvent extends GameEvent {
    private CardEnum card;

    public VC_PlayerCardChosenEvent(String username, CardEnum card) {
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
