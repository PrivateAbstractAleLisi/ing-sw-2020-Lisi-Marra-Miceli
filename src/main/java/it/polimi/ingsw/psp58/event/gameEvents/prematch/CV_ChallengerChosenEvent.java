package it.polimi.ingsw.psp58.event.gameEvents.prematch;

import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;

/**
 * Event sent by the controller to the client when the challenger has to choose the cards.
 * Contains the room size that will be the number of the cards that the challenger has to choose.
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
