package it.polimi.ingsw.psp58.view.UI.GUI.boardstate;

import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.CV_CommandExecutedGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.CV_CommandRequestEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.CV_PlayerPlaceWorkerRequestEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.VC_PlayerPlacedWorkerEvent;
import it.polimi.ingsw.psp58.model.TurnAction;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;
import it.polimi.ingsw.psp58.model.gamemap.Worker;
import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.BoardSceneController;

public class PlaceWorkerGameState implements GameStateAbstract {
    private CV_PlayerPlaceWorkerRequestEvent eventArrived;
    private final GUI gui;
    private final BoardSceneController boardSceneController;

    public PlaceWorkerGameState(CV_PlayerPlaceWorkerRequestEvent eventArrived, GUI gui, BoardSceneController boardController) {
        this.eventArrived = eventArrived;
        this.gui = gui;
        this.boardSceneController = boardController;
    }

    @Override
    public void handleClickOnButton(int x, int y) {
        //place the worker
        Worker.IDs workerID = boardSceneController.getWorkerStatus().getSelectedWorker();
        gui.sendEvent(new VC_PlayerPlacedWorkerEvent("", gui.getUsername(), x, y, workerID));
    }

    @Override
    public void handleClickOnButton(TurnAction buttonPressed) {
        displayPlacementStateError();
    }

    @Override
    public void handleClickOnButton(BlockTypeEnum blockClicked) {
        displayPlacementStateError();
    }

    @Override
    public void updateFromServer(CV_CommandExecutedGameEvent event) {
        displayPlacementStateError();
    }

    @Override
    public void updateFromServer(CV_CommandRequestEvent event) {
        displayPlacementStateError();
    }

    @Override
    public ViewGameEvent getEvent() {
        return eventArrived;
    }

    private void displayPlacementStateError() {
        boardSceneController.displayPopupMessage("INVALID ACTION, PLEASE PLACE YOUR WORKER");
    }
}
