package it.polimi.ingsw.psp58.event.gameEvents.prematch;

import it.polimi.ingsw.psp58.event.core.ControllerListener;
import it.polimi.ingsw.psp58.event.gameEvents.ControllerGameEvent;

public class VC_ChallengerChosenFirstPlayerEvent extends ControllerGameEvent {

    public VC_ChallengerChosenFirstPlayerEvent(String username) {
        super(username);
    }

    @Override
    public void notifyHandler(ControllerListener listener) {
        listener.handleEvent(this);
    }

    public String getUsername(){
        return super.getEventDescription();
    }
 //USERNAME IS INTO THE DESCRIPTION FIELD
}
