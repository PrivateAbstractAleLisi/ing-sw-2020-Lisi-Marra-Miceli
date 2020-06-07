package it.polimi.ingsw.psp58.view.UI.GUI.boardstate;

import it.polimi.ingsw.psp58.event.gameEvents.ControllerGameEvent;
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

public class PlaceWorkerGameState extends GameStateAbstract {
    private CV_PlayerPlaceWorkerRequestEvent eventArrived;
    private final GUI gui;
    private final BoardSceneController boardSceneController;

    private final GameStateEnum state = GameStateEnum.PLACE_WORKER;

    public PlaceWorkerGameState(CV_PlayerPlaceWorkerRequestEvent eventArrived, GUI gui, BoardSceneController boardController) {
        this.eventArrived = eventArrived;
        this.gui = gui;
        this.boardSceneController = boardController;
    }

    @Override
    public ControllerGameEvent handleClick(String username, int x, int y, Worker.IDs workerID, GameStateEnum state) {
        return new VC_PlayerPlacedWorkerEvent("", username, x, y, workerID);
    }

    @Override
    public void handleClickOnButton(int x, int y) {
        System.out.println("Place Worker State click on " + x + " " + y);
        //place the worker
        gui.sendEvent(new VC_PlayerPlacedWorkerEvent("", gui.getUsername(), x, y, boardSceneController.getWorkerStatus().getSelectedWorker()));
    }

    @Override
    public void handleClickOnButton(TurnAction buttonPressed) {
        System.out.println("Place Worker State Handle Click On Button - ERROR");
        //PRINT ERROR
    }

    @Override
    public void handleClickOnButton(BlockTypeEnum blockClicked) {
        System.out.println("Place Worker State Handle Click On Block - ERROR");
        //PRINT ERROR
    }

    @Override
    public void updateFromServer(CV_CommandExecutedGameEvent event) {
        System.out.println("Place Worker State Handle CV_CommandExecutedGameEvent - ERROR");
        //PRINT ERROR
    }

    @Override
    public void updateFromServer(CV_CommandRequestEvent event) {
        System.out.println("Place Worker State Handle CV_CommandRequestEvent - ERROR");
        //PRINT ERROR
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
