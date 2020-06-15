package it.polimi.ingsw.psp58.view.UI.GUI.boardstate;

import it.polimi.ingsw.psp58.event.gameEvents.match.CV_CommandExecutedGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.CV_CommandRequestEvent;
import it.polimi.ingsw.psp58.model.TurnAction;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;

/**
 * Interface that implements pattern State with all methods to handle the interaction by the user and updates from the server.
 */
public interface GameStateAbstract {
    /**
     * Called if the user clicked on the board.
     *
     * @param x X Coordinate of the click.
     * @param y Y Coordinate of the click.
     */
    void handleClick(int x, int y);

    /**
     * Called if the user clicked one of the Action Button.
     *
     * @param buttonPressed {@link TurnAction} of the Button pressed.
     */
    void handleClick(TurnAction buttonPressed);

    /**
     * Called if the user clicked one of the Block Type Button.
     *
     * @param blockClicked {@link BlockTypeEnum} of the Button pressed.
     */
    void handleClick(BlockTypeEnum blockClicked);

    /**
     * Handle the {@link CV_CommandExecutedGameEvent} to handle the next user input.
     *
     * @param event The {@link CV_CommandExecutedGameEvent} received from the server.
     */
    void updateFromServer(CV_CommandExecutedGameEvent event);

    /**
     * Handle the {@link CV_CommandRequestEvent} to handle the next user input.
     *
     * @param event The {@link CV_CommandRequestEvent} received from the server.
     */
    void updateFromServer(CV_CommandRequestEvent event);
}
