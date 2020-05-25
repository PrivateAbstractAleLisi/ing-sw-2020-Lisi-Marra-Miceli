package it.polimi.ingsw.psp58.view.UI.GUI.boardstate;

import it.polimi.ingsw.psp58.event.gameEvents.ControllerGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.CV_WaitMatchGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.CV_WaitPreMatchGameEvent;
import it.polimi.ingsw.psp58.model.gamemap.Worker;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.BoardSceneController;

public class WaitGameState extends GameStateAbstract {

    private final GameStateEnum state = GameStateEnum.NOT_YOUR_TURN;

    public WaitGameState(CV_WaitPreMatchGameEvent event) {
    }

    public WaitGameState() {
    }

    @Override
    public void setState(BoardSceneController boardController) {
        boardController.setWaitingView();
    }

    @Override
    public ControllerGameEvent handleClick(String username, int x, int y, Worker.IDs workerID, GameStateEnum state) {
        return null;
    }

    @Override
    public ViewGameEvent getEvent() {
        return null;
    }

    @Override
    public GameStateEnum getState() {
        return state;
    }

    //@Overload
    public void setState(BoardSceneController boardController, CV_WaitMatchGameEvent event) {
        boardController.setWaitingView();
        updateTurnSequence();
    }

    private void updateTurnSequence() {
        //aggiorna la sequenza su con updateTurnSequence(evento)
        //todo
    }


}
