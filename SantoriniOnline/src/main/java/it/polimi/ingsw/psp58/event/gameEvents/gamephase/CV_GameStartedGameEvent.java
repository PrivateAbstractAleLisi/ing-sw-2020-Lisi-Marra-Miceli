package it.polimi.ingsw.psp58.event.gameEvents.gamephase;

import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;

/**
 * Event to notify the client that the game is started. Contains information about the first player.
 */
public class CV_GameStartedGameEvent extends ViewGameEvent {
    String firstPlayer;

    public CV_GameStartedGameEvent(String description, String firstPlayer) {
        super(description);
        this.firstPlayer = firstPlayer;
    }

    @Override
    public void notifyHandler(ViewListener listener) {
        listener.handleEvent(this);
    }
}