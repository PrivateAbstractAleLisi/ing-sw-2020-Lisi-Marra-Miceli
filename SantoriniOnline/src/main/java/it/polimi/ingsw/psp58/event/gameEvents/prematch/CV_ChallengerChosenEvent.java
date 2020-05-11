package it.polimi.ingsw.psp58.event.gameEvents.prematch;

import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;

/**
 * notifies to a client that he's the challenger -> it triggers a method to pick 3 cards
 */
public class CV_ChallengerChosenEvent extends ViewGameEvent {

    private final int roomSize;
    public CV_ChallengerChosenEvent(String username, int roomSize) {
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
    public void notifyHandler(ViewListener listener) {
        listener.handleEvent(this);
    }
}
