package event.events;

import event.core.EventListener;

/**
 * notifies to a client that he's the challenger -> it triggers a method to pick 3 cards
 */
public class ChallengerChosenEvent extends GameEvent {

    private final int roomSize;
    public ChallengerChosenEvent(String username, int roomSize) {
        super(username);
        this.roomSize = roomSize;
    }

    public String getUsername(){
        return super.getEventDescription();
    }

    public int getRoomSize() {
        return roomSize;
    }


    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }
}
