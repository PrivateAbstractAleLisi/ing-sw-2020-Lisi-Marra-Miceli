package it.polimi.ingsw.psp58.view.UI.GUI.boardstate;

import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.CV_CommandExecutedGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.CV_CommandRequestEvent;
import it.polimi.ingsw.psp58.model.TurnAction;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.BoardSceneController;

public class WaitGameState implements GameStateAbstract {
    private final BoardSceneController boardSceneController;

    public WaitGameState(BoardSceneController boardSceneController) {
        this.boardSceneController = boardSceneController;
    }

    @Override
    public void handleClickOnButton(int x, int y) {
        displayWaitError();
    }

    @Override
    public void handleClickOnButton(TurnAction buttonPressed) {
        displayWaitError();
    }

    @Override
    public void handleClickOnButton(BlockTypeEnum blockClicked) {
        displayWaitError();
    }

    @Override
    public void updateFromServer(CV_CommandExecutedGameEvent event) {
        displayWaitError();
    }

    @Override
    public void updateFromServer(CV_CommandRequestEvent event) {
       displayWaitError();
    }

    @Override
    public ViewGameEvent getEvent() {
        return null;
    }

    private void displayWaitError() {
        boardSceneController.displayPopupMessage("IT'S NOT YOUR TURN, PLEASE WAIT");
    }
}
