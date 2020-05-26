package it.polimi.ingsw.psp58.view.UI.GUI.boardstate;

import com.google.gson.Gson;
import it.polimi.ingsw.psp58.auxiliary.IslandData;
import it.polimi.ingsw.psp58.event.gameEvents.ControllerGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.CV_PlayerPlaceWorkerRequestEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.VC_PlayerPlacedWorkerEvent;
import it.polimi.ingsw.psp58.model.gamemap.Worker;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.BoardSceneController;

public class PlaceWorkerGameState extends GameStateAbstract {
    private CV_PlayerPlaceWorkerRequestEvent eventArrived;

    private final GameStateEnum state = GameStateEnum.PLACE_WORKER;

    public PlaceWorkerGameState(CV_PlayerPlaceWorkerRequestEvent eventArrived) {
        this.eventArrived = eventArrived;
    }

    @Override
    public void setState(BoardSceneController boardController) {
        boardController.setLastIslandUpdate(eventArrived.getIsland());
        boardController.updateIsland(eventArrived.getIsland());
        boardController.handleWorkerPlacement(eventArrived.getWorkerToPlace());
    }

    @Override
    public ControllerGameEvent handleClick(String username, int x, int y, Worker.IDs workerID, GameStateEnum state) {
        return new VC_PlayerPlacedWorkerEvent("", username, x, y, workerID);
    }

    @Override
    public ViewGameEvent getEvent() {
        return eventArrived;
    }

    @Override
    public GameStateEnum getState() {
        return state;
    }



}
