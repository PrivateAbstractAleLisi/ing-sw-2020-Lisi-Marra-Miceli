package it.polimi.ingsw.psp58.model.gods;

import it.polimi.ingsw.psp58.exceptions.InvalidMovementException;
import it.polimi.ingsw.psp58.exceptions.WinningException;
import it.polimi.ingsw.psp58.model.Card;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.model.Player;
import it.polimi.ingsw.psp58.model.gamemap.Island;
import it.polimi.ingsw.psp58.model.gamemap.Worker;

/**
 * Artemis Card implementation.
 * @author alelisi
 */
public class Artemis extends Card {
    /**
     * Position at the beginning of the turn, at the beginning of the turn the values are set to -1.
     */
    private int[] startingPosition;
    /**
     * Worker chosen during the first move.
     */
    private Worker.IDs workerChosen;

    /**
     * Constructor, same as card but it fills name and descriptions
     *
     * @param p the player that plays with the card
     */
    public Artemis(Player p) {
        super(p);
        name = CardEnum.ARTEMIS;
        startingPosition =  new int[]{-1, -1};
    }

    /**
     * Artemis overrides the move method
     * SPECIAL EFFECT: Artemis can move a second time without going back to starting position
     *
     * @param worker   A worker of the actual player
     * @param desiredX X Position where the player wants to move the worker
     * @param desiredY Y Position where the player wants to move the worker
     * @param island   The current board of game
     * @throws InvalidMovementException If the movement is not valid throw a new exception.
     * @throws WinningException If the player won, throw a WinningException
     */
    @Override
    public void move(Worker worker, int desiredX, int desiredY, Island island) throws InvalidMovementException, WinningException {
        //where my worker starts in this move
        int actualX = worker.getPosition()[0];
        int actualY = worker.getPosition()[1];

        //Checks if movement is available and if I can move there
        if (!isValidDestination(actualX, actualY, desiredX, desiredY, island)) {
            throw new InvalidMovementException("Invalid move for this worker");
        }

        //if it's my first move during the turn, save my starting position
        if (startingPosition[0] == -1 && startingPosition[1] == -1) {
            startingPosition = new int[2];
            workerChosen = worker.getWorkerID();
            startingPosition[0] = actualX;
            startingPosition[1] = actualY;
        }
        //if it's the second move, check if the destination is different from my starting position during the turn
        else {
            if (desiredX == startingPosition[0] && desiredY == startingPosition[1]) {
                throw new IllegalArgumentException("ARTEMIS: on the second movement you can't come back to starting position");
            }
            if (worker.getWorkerID() != workerChosen) {
                throw new IllegalArgumentException("ARTEMIS: on the second movement you must use the same worker ");
            }
        }

        island.moveWorker(worker, desiredX, desiredY);

        //decrease the number of required movements
        playedBy.getBehaviour().setMovementsRemaining(playedBy.getBehaviour().getMovementsRemaining() - 1);

        if (!checkWorkerPosition(island, worker, desiredX, desiredY)) {
            throw new InvalidMovementException("The move is valid but there was an error applying desired changes");
        } else {
            //memorize the high of worker and the check if the worker has won
            int oldAltitudeOfPlayer = island.getCellCluster(actualX, actualY).getCostructionHeight();
            checkWin(island, desiredX, desiredY, oldAltitudeOfPlayer);
        }

    }

    /**
     * Reset the {@code behaviour} of the {@link Player} to the default value of the card and reset the starting position.
     */
    //ARTEMIS can move two times in normal conditions
    @Override
    public void resetBehaviour() {
        super.resetBehaviour();
        playedBy.getBehaviour().setMovementsRemaining(2);

        //decided during develop
        startingPosition[0] = -1;
        startingPosition[1] = -1;
    }
}
