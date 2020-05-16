package it.polimi.ingsw.psp58.event.gamephase;

import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.GameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.model.WorkerColors;

import java.util.*;

public class CV_WorkerPlacementGameEvent extends ViewGameEvent {

    Map<String, CardEnum> turnSequence;
    Map<String, WorkerColors> playerWorkerColors;

    public CV_WorkerPlacementGameEvent(String description, Map<String, CardEnum> turnSequence, Map<String, WorkerColors> playerWorkerColors) {
        super(description);
        this.turnSequence = new HashMap<>(turnSequence);
        this.playerWorkerColors = playerWorkerColors;
    }

    public Map<String, CardEnum> getTurnSequence() {
        return turnSequence;
    }

    public Map<String, WorkerColors> getPlayerWorkerColors() {
        return playerWorkerColors;
    }

    @Override
    public void notifyHandler(ViewListener listener) {
        listener.handleEvent(this);
    }

}
