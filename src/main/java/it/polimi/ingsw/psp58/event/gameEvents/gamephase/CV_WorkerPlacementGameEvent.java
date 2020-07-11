package it.polimi.ingsw.psp58.event.gameEvents.gamephase;

import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.model.WorkerColors;

import java.util.*;

/**
 * Event sent when the worker placement phase starts. Contains the main information of the players:
 * the correspondence between players' names and cards and between the players' names and colors.
 */
public class CV_WorkerPlacementGameEvent extends ViewGameEvent {

    Map<String, CardEnum> playersCardsCorrespondence;
    Map<String, WorkerColors> playerWorkerColors;

    public CV_WorkerPlacementGameEvent(String description, Map<String, CardEnum> playersCardsCorrespondence, Map<String, WorkerColors> playerWorkerColors) {
        super(description);
        this.playersCardsCorrespondence = new HashMap<>(playersCardsCorrespondence);
        this.playerWorkerColors = playerWorkerColors;
    }

    public Map<String, CardEnum> getPlayersCardsCorrespondence() {
        return playersCardsCorrespondence;
    }

    public Map<String, WorkerColors> getPlayerWorkerColors() {
        return playerWorkerColors;
    }

    @Override
    public void notifyHandler(ViewListener listener) {
        listener.handleEvent(this);
    }

}
