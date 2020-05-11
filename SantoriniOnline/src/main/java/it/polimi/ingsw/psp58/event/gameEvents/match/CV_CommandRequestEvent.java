package it.polimi.ingsw.psp58.event.gameEvents.match;

import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;
import it.polimi.ingsw.psp58.model.TurnAction;

import java.util.List;

public class CV_CommandRequestEvent extends ViewGameEvent {
    private final List<TurnAction> availableActions;

    private final List<int[]> availableBuildBlocksA;
    private final List<int[]> availableMovementBlocksA;
    private final List<int[]> availableBuildBlocksB;
    private final List<int[]> availableMovementBlocksB;

    private final String actingPlayer;

    public CV_CommandRequestEvent(String description, List<TurnAction> availableActions, List<int[]> availableBuildA, List<int[]> availableMovesA, List<int[]> availableBuildB, List<int[]> availableMovesB, String actingPlayer) {
        super(description);
        this.availableActions = availableActions;
        this.availableBuildBlocksA = availableBuildA;
        this.availableMovementBlocksA = availableMovesA;
        this.availableBuildBlocksB = availableBuildA;
        this.availableMovementBlocksB = availableMovesA;
        this.actingPlayer = actingPlayer;
    }

    public List<TurnAction> getAvailableActions() {
        return availableActions;
    }

    public String getActingPlayer() {
        return actingPlayer;
    }

    public List<int[]> getAvailableBuildBlocksA() {
        return availableBuildBlocksA;
    }

    public List<int[]> getAvailableMovementBlocksA() {
        return availableMovementBlocksA;
    }

    public List<int[]> getAvailableBuildBlocksB() {
        return availableBuildBlocksB;
    }

    public List<int[]> getAvailableMovementBlocksB() {
        return availableMovementBlocksB;
    }

    @Override
    public void notifyHandler(ViewListener listener) {
        listener.handleEvent(this);
    }
}
