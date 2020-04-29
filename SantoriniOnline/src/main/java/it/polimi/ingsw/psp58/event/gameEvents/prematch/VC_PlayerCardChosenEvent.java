package it.polimi.ingsw.psp58.event.gameEvents.prematch;

import it.polimi.ingsw.psp58.event.core.EventListener;
import it.polimi.ingsw.psp58.event.gameEvents.GameEvent;
import it.polimi.ingsw.psp58.model.CardEnum;

/**
 * By View to it.polimi.ingsw.sp58.controller, sent when the current player has chosen the card to play with
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
