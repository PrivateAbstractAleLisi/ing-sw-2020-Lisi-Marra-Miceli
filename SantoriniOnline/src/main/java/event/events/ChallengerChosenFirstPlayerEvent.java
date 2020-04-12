package event.events;

import event.core.EventListener;

public class ChallengerChosenFirstPlayerEvent extends GameEvent {

    public ChallengerChosenFirstPlayerEvent(String username) {
        super(username);
    }

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }

    public String getUsername(){
        return super.getEventDescription();
    }

}
