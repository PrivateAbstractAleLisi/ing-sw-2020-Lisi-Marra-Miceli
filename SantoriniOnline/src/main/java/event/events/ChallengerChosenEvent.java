package event.events;

import event.core.EventListener;

public class ChallengerChosenEvent extends GameEvent {

    public ChallengerChosenEvent(String username) {
        super(username);
    }

    public String getUsername(){
        return super.getEventDescription();
    }

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }
}
