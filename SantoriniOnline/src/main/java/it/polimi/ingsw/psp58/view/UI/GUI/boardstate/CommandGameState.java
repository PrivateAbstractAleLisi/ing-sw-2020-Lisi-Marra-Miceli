package it.polimi.ingsw.psp58.view.UI.GUI.boardstate;

import it.polimi.ingsw.psp58.event.gameEvents.ControllerGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.CV_CommandRequestEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.VC_PlayerCommandGameEvent;
import it.polimi.ingsw.psp58.model.TurnAction;
import it.polimi.ingsw.psp58.model.gamemap.Worker;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.BoardSceneController;


public class CommandGameState extends GameStateAbs {
    private CV_CommandRequestEvent eventArrived;
    private BoardSceneController boardSceneController;

    public CommandGameState(CV_CommandRequestEvent eventArrived) {
        this.eventArrived = eventArrived;
    }

    @Override
    public void setState(BoardSceneController boardController) {

        boardSceneController = boardController;

        boardController.enableActionButtons(eventArrived.getAvailableActions());

        if(!boardController.hasAlreadyMadeAMove()){
            boardController.setWorkerOnAction(null);
        }
    }

    @Override
    public ControllerGameEvent handleClick(String username, int x, int y, Worker.IDs workerID, GameState state) {
        switch (state) {
            case SELECT_WORKER:
//                boardSceneController.showPossibleBlockAction(workerID);
                break;
            case MOVE:
                return new VC_PlayerCommandGameEvent("", TurnAction.MOVE , username, new int[]{x, y}, workerID, null  );
            case BUILD:
                return new VC_PlayerCommandGameEvent("", TurnAction.BUILD , username, new int[]{x, y}, workerID, null  );
        }
        return null;
}

    @Override
    public ViewGameEvent getEvent() {
        return eventArrived;
    }
}
