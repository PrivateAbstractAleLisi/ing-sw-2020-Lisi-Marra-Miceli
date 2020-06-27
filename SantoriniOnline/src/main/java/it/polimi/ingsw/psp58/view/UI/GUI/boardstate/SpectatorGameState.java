package it.polimi.ingsw.psp58.view.UI.GUI.boardstate;

import it.polimi.ingsw.psp58.event.gameEvents.match.CV_CommandExecutedGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.CV_CommandRequestEvent;
import it.polimi.ingsw.psp58.model.TurnAction;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.BoardSceneController;

/**
 * Spectator State class implementation.
 */
public class SpectatorGameState implements GameStateAbstract {
    /**
     * The {@link BoardSceneController} that created this Object.
     */
    private final BoardSceneController boardSceneController;

    /**
     * Unique constructor.
     *
     * @param boardSceneController The {@link BoardSceneController} that created this Object.
     */
    public SpectatorGameState(BoardSceneController boardSceneController) {
        this.boardSceneController = boardSceneController;
    }

    /**
     * Display error because it's Spectator State.
     *
     * @param x X Coordinate of the click.
     * @param y Y Coordinate of the click.
     */
    @Override
    public void handleClick(int x, int y) {
        displaySpectatorError();
    }

    /**
     * Display error because it's Spectator State.
     *
     * @param buttonPressed {@link TurnAction} of the Button pressed.
     */
    @Override
    public void handleClick(TurnAction buttonPressed) {
        displaySpectatorError();
    }

    /**
     * Display error because it's Spectator State.
     *
     * @param blockClicked {@link BlockTypeEnum} of the Button pressed.
     */
    @Override
    public void handleClick(BlockTypeEnum blockClicked) {
        displaySpectatorError();
    }

    /**
     * Display error because it's Spectator State.
     *
     * @param event The {@link CV_CommandExecutedGameEvent} received from the server.
     */
    @Override
    public void updateFromServer(CV_CommandExecutedGameEvent event) {
        displaySpectatorError();
    }

    /**
     * Display error because it's Spectator State.
     *
     * @param event The {@link CV_CommandRequestEvent} received from the server.
     */
    @Override
    public void updateFromServer(CV_CommandRequestEvent event) {
        displaySpectatorError();
    }

    /**
     * Display a Spectator State Error with a Popup
     */
    private void displaySpectatorError() {
        boardSceneController.displayPopupMessage("YOU LOST THE GAME - WAIT OR EXIT");
    }
}
