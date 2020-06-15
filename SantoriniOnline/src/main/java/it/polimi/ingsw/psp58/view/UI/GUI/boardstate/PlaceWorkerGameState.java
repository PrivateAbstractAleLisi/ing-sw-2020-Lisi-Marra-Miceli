package it.polimi.ingsw.psp58.view.UI.GUI.boardstate;

import it.polimi.ingsw.psp58.event.gameEvents.match.CV_CommandExecutedGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.CV_CommandRequestEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.VC_PlayerPlacedWorkerEvent;
import it.polimi.ingsw.psp58.model.TurnAction;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;
import it.polimi.ingsw.psp58.model.gamemap.Worker;
import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.BoardSceneController;

/**
 * Worker Placement state class implementation that handle the placement of the workers.
 */
public class PlaceWorkerGameState implements GameStateAbstract {
    /**
     * The {@link BoardSceneController} that created this Object.
     */
    private final BoardSceneController boardSceneController;
    /**
     * Main {@link GUI} class.
     */
    private final GUI gui;

    /**
     * Unique Constructor of this Class.
     *
     * @param gui             Main {@link GUI} class.
     * @param boardController The {@link BoardSceneController} that created this Object.
     */
    public PlaceWorkerGameState(GUI gui, BoardSceneController boardController) {
        this.gui = gui;
        this.boardSceneController = boardController;
    }

    /**
     * Place the worker at given coordinates and send event through the {@code GUI}.
     *
     * @param x X Coordinate of the click.
     * @param y Y Coordinate of the click.
     */
    @Override
    public void handleClick(int x, int y) {
        //place the worker
        Worker.IDs workerID = boardSceneController.getWorkerStatus().getSelectedWorker();
        gui.sendEvent(new VC_PlayerPlacedWorkerEvent("", gui.getUsername(), x, y, workerID));
    }

    /**
     * Display error because it's Placement State.
     *
     * @param buttonPressed {@link TurnAction} of the Button pressed.
     */
    @Override
    public void handleClick(TurnAction buttonPressed) {
        displayPlacementStateError();
    }

    /**
     * Display error because it's Placement State.
     *
     * @param blockClicked {@link BlockTypeEnum} of the Button pressed.
     */
    @Override
    public void handleClick(BlockTypeEnum blockClicked) {
        displayPlacementStateError();
    }

    /**
     * Display error because it's Placement State.
     *
     * @param event The {@link CV_CommandExecutedGameEvent} received from the server.
     */
    @Override
    public void updateFromServer(CV_CommandExecutedGameEvent event) {
        displayPlacementStateError();
    }

    /**
     * Display error because it's Placement State.
     *
     * @param event The {@link CV_CommandRequestEvent} received from the server.
     */
    @Override
    public void updateFromServer(CV_CommandRequestEvent event) {
        displayPlacementStateError();
    }

    /**
     * Display a Placement State Error with a Popup
     */
    private void displayPlacementStateError() {
        boardSceneController.displayPopupMessage("INVALID ACTION, PLEASE PLACE YOUR WORKER");
    }
}
