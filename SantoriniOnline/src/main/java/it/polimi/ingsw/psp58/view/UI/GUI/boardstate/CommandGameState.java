package it.polimi.ingsw.psp58.view.UI.GUI.boardstate;

import it.polimi.ingsw.psp58.event.gameEvents.ControllerGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.CV_CommandRequestEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.VC_PlayerCommandGameEvent;
import it.polimi.ingsw.psp58.model.TurnAction;
import it.polimi.ingsw.psp58.model.gamemap.Worker;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.BoardSceneController;


public class CommandGameState extends GameStateAbstract {
    private CV_CommandRequestEvent eventArrived;
    private BoardSceneController boardSceneController;

    private final GameStateEnum state = GameStateEnum.COMMAND;

    public CommandGameState(CV_CommandRequestEvent eventArrived) {
        this.eventArrived = eventArrived;
    }

    @Override
    public void setState(BoardSceneController boardController) {


        boardSceneController = boardController;
        boardSceneController.hideWorkerPlacementBox();
        boardController.enableActionButtons(eventArrived.getAvailableActions());
        boardController.initCommandRequest();


        /*
        if(!boardController.hasAlreadyMadeAMove()){
            boardController.setWorkerOnAction(null);
        }



        if(!boardSceneController.hasAlreadyMadeAMove()){
            boardController.displayMessage("please select a worker");
            boardSceneController.setCurrentState(GameState.SELECT_WORKER);
        }
        */

    }

    @Override
    public GameStateEnum getState() {
        return state;
    }

    @Override
    public ControllerGameEvent handleClick(String username, int x, int y, Worker.IDs workerID, GameStateEnum state) {
        switch (state) {
            case SELECT_WORKER:
//                boardSceneController.showPossibleBlockAction(workerID);
                break;
            case MOVE:
                return new VC_PlayerCommandGameEvent("", TurnAction.MOVE , username, new int[]{x, y}, workerID, null  );
            case BUILD:
                return new VC_PlayerCommandGameEvent("", TurnAction.BUILD , username, new int[]{x, y}, workerID, null  );
            case WAIT_COMMAND_BUTTON_:
                boardSceneController.displayMessage("please select a command first");
        }
        return null;
}

    public void workerClick(Worker.IDs workerID, BoardSceneController board) {

    }

    @Override
    public ViewGameEvent getEvent() {
        return eventArrived;
    }
}
