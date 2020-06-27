package it.polimi.ingsw.psp58.event.gameEvents.match;

import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;

import java.util.List;

/**
 * Event sent by the controller to the client when a new turn begins.
 * Contains the turn rotation and the username of the player that has to perform an action.
 */
public class CV_NewTurnEvent extends ViewGameEvent {
    private final String currentPlayer;
    private final List<String> turnRotation;

    public CV_NewTurnEvent(String description, String currentPlayerUsername, List<String> turnRotation) {
        super(description);
        this.currentPlayer = currentPlayerUsername;
        this.turnRotation = turnRotation;
    }

    public String getCurrentPlayerUsername() {
        return currentPlayer;
    }

    public List<String> getTurnRotation() {
        return turnRotation;
    }

    @Override
    public void notifyHandler(ViewListener listener) {
        listener.handleEvent(this);
    }
}
