package event.gameEvents.prematch;

import event.core.EventListener;
import event.gameEvents.GameEvent;

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
