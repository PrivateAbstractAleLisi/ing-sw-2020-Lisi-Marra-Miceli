package it.polimi.ingsw.psp58.view.UI.GUI.boardstate;

import com.google.gson.Gson;
import it.polimi.ingsw.psp58.auxiliary.IslandData;
import it.polimi.ingsw.psp58.event.gameEvents.ControllerGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.CV_PlayerPlaceWorkerRequestEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.VC_PlayerPlacedWorkerEvent;
import it.polimi.ingsw.psp58.model.gamemap.Worker;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.BoardSceneController;

public class PlaceWorkerGameState extends GameStateAbs {
    private  CV_PlayerPlaceWorkerRequestEvent eventArrived;

    private final GameState state = GameState.PLACE_WORKER;
    public PlaceWorkerGameState(CV_PlayerPlaceWorkerRequestEvent eventArrived) {
        this.eventArrived = eventArrived;
    }

    @Override
    public void setState(BoardSceneController b) {
        b.setLastIslandUpdate(islandDataFromJson(eventArrived.getIsland()));
        b.updateIsland(islandDataFromJson(eventArrived.getIsland()));
        b.handleWorkerPlacement(eventArrived.getWorkerToPlace());

    }

    @Override
    public ControllerGameEvent handleClick(String username, int x, int y, Worker.IDs workerID, GameState state) {
        return new VC_PlayerPlacedWorkerEvent("", username, x, y, workerID);
    }

    @Override
    public ViewGameEvent getEvent() {
        return eventArrived;
    }

    @Override
    public GameState getState() {
        return state;
    }

    private IslandData islandDataFromJson(String islaJson) {
        //display island
        Gson gson = new Gson();
        return gson.fromJson(islaJson, IslandData.class);
    }

}
