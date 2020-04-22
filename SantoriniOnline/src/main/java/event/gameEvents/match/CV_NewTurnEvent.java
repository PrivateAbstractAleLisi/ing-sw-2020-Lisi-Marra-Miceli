package event.gameEvents.match;

import event.core.EventListener;
import event.gameEvents.GameEvent;

import java.util.List;

public class CV_NewTurnEvent extends GameEvent {
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
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }
}
