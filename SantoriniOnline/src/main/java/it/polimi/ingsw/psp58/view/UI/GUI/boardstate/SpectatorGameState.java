package it.polimi.ingsw.psp58.view.UI.GUI.boardstate;

import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.CV_CommandExecutedGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.CV_CommandRequestEvent;
import it.polimi.ingsw.psp58.model.TurnAction;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.BoardSceneController;

public class SpectatorGameState implements GameStateAbstract {
    private final BoardSceneController boardSceneController;

    public SpectatorGameState(BoardSceneController boardSceneController) {
        this.boardSceneController = boardSceneController;
    }

    @Override
    public void handleClickOnButton(int x, int y) {
        displaySpectatorError();
    }

    @Override
    public void handleClickOnButton(TurnAction buttonPressed) {
        displaySpectatorError();
    }

    @Override
    public void handleClickOnButton(BlockTypeEnum blockClicked) {
        displaySpectatorError();
    }

    @Override
    public void updateFromServer(CV_CommandExecutedGameEvent event) {
        displaySpectatorError();
    }

    @Override
    public void updateFromServer(CV_CommandRequestEvent event) {
        displaySpectatorError();
    }

    @Override
    public ViewGameEvent getEvent() {
        return null;
    }

    private void displaySpectatorError() {
        boardSceneController.displayPopupMessage("YOU LOST THE GAME - WAIT OR EXIT");
    }
}
