package it.polimi.ingsw.psp58.event.gameEvents.prematch;

import it.polimi.ingsw.psp58.event.core.EventListener;
import it.polimi.ingsw.psp58.event.gameEvents.GameEvent;

public class VC_ChallengerChosenFirstPlayerEvent extends GameEvent {

    public VC_ChallengerChosenFirstPlayerEvent(String username) {
        super(username);
    }

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }

    public String getUsername(){
        return super.getEventDescription();
    }
 //USERNAME IS INTO THE DESCRIPTION FIELD
}
