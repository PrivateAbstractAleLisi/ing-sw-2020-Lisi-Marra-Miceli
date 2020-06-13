package it.polimi.ingsw.psp58.event.gameEvents.prematch;

import it.polimi.ingsw.psp58.auxiliary.IslandData;
import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;
import it.polimi.ingsw.psp58.model.gamemap.Worker;

public class CV_PlayerPlaceWorkerRequestEvent extends ViewGameEvent {
    private String actingPlayer;
    private IslandData islandData;
    private Worker.IDs workerToPlace;

    public CV_PlayerPlaceWorkerRequestEvent(String description, String actingPlayer, IslandData islandData, Worker.IDs workerToPlace) {
        super(description);
        this.actingPlayer = actingPlayer;
        this.workerToPlace = workerToPlace;
        this.islandData = islandData;
    }

    public String getActingPlayer() {
        return actingPlayer;
    }

    @Override
    public void notifyHandler(ViewListener listener) {
        listener.handleEvent(this);
    }

    public Worker.IDs getWorkerToPlace() {
        return workerToPlace;
    }

    public IslandData getIsland() {
        return islandData;
    }
}
