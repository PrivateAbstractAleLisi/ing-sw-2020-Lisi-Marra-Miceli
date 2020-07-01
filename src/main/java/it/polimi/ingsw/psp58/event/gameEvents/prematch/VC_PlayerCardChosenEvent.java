package it.polimi.ingsw.psp58.event.gameEvents.prematch;

import it.polimi.ingsw.psp58.event.core.ControllerListener;
import it.polimi.ingsw.psp58.event.gameEvents.ControllerGameEvent;
import it.polimi.ingsw.psp58.model.CardEnum;

/**
 * Event sent by the client to the server responding to a {@link CV_CardChoiceRequestGameEvent} when the player has to choose his card.
 * Contains the card chosen by the player.
 */
public class VC_PlayerCardChosenEvent extends ControllerGameEvent {
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
    public void notifyHandler(ControllerListener listener) {
        listener.handleEvent(this);
    }
}
