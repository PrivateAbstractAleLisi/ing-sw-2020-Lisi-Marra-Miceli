package it.polimi.ingsw.psp58.view.UI.GUI.boardstate;

import it.polimi.ingsw.psp58.event.gameEvents.match.CV_WaitMatchGameEvent;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.BoardSceneController;

public class WaitGameState extends GameStateAbs {
    @Override
    public void setState(BoardSceneController boardController) {
        boardController.setWaitingView();
    }
    //@Overload
    public void setState(BoardSceneController boardController, CV_WaitMatchGameEvent event) {
        boardController.setWaitingView();
        updateTurnSequence();
    }
    private void updateTurnSequence() {
        //aggiorna la sequenza su con updateTurnSequence(evento)
    }
}
