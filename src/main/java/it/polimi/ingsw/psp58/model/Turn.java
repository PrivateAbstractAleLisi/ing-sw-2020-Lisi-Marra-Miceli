package it.polimi.ingsw.psp58.model;

import it.polimi.ingsw.psp58.exceptions.DefeatException;
import it.polimi.ingsw.psp58.model.gamemap.Worker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class contains all the info of the specific turn of the player
 */
public class Turn {
    private final Player currentPlayer;
    private Worker.IDs workerID;
    private int numberOfMove;
    private int numberOfBuild;
    private int[] startingPosition;
    private final BoardManager boardManager;
    private boolean hasBuiltBeforeMove;

    public Turn(Player currentPlayer, BoardManager boardManager) {
        this.currentPlayer = currentPlayer;
        this.numberOfBuild = 0;
        this.numberOfMove = 0;
        this.boardManager = boardManager;
        this.hasBuiltBeforeMove=false;
    }

    /**
     * The player must perform all the actions of the turn with the same player so he has to choose one
     *
     * @param workerID the id of the worker chosen by the player at the start of the turn
     */
    public void chooseWorker(Worker.IDs workerID) {
        this.workerID = workerID;
        startingPosition = new int[2];
        startingPosition = currentPlayer.getWorker(workerID).getPosition();
    }

    /**
     * @return the current player is playing the turn
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * @return the worker ID used by the currentPlayer in this turn
     */
    public Worker.IDs getWorkerID() {
        return workerID;
    }

    /**
     * @return the starting position of the worker
     */
    public int[] getStartingPosition() {
        return startingPosition;
    }

    /**
     * Check if the worker position is the same as the start of the turn
     *
     * @throws DefeatException if the player as not moved in the turn
     */
    public void checkMoveDuringTurn() throws DefeatException {
        if (Arrays.equals(currentPlayer.getWorker(workerID).getPosition(), startingPosition))
            throw new DefeatException(currentPlayer.getUsername());
    }

    /**
     * Calculate all the possible destination of an action of a specific worker passed both as parameters
     *
     * @param workerID the worker that perform the action
     * @param action   the action to perform
     * @return a list of all the possible move or build destination
     */
    public List<int[]> validActions(Worker.IDs workerID, TurnAction action) {
        ArrayList<int[]> validMoves = new ArrayList<>();
        int[] position;
        Worker worker = currentPlayer.getWorker(workerID);
        int[] currentPosition = worker.getPosition();
        for (int i = currentPosition[0] - 1; i <= currentPosition[0] + 1; i++) {
            for (int j = currentPosition[1] - 1; j <= currentPosition[1] + 1; j++) {
                if (action == TurnAction.MOVE) {
                    if (currentPlayer.getCard().checkCellMovementAvailability(currentPosition[0], currentPosition[1], i, j, boardManager.getIsland())) {
                        position = new int[2];
                        position[0] = i;
                        position[1] = j;
                        validMoves.add(position);
                    }
                } else if (action == TurnAction.BUILD &&
                        currentPlayer.getCard().checkCellConstructionAvailability(currentPosition[0], currentPosition[1], i, j, boardManager.getIsland())) {
                        position = new int[2];
                        position[0] = i;
                        position[1] = j;
                        validMoves.add(position);

                }
            }
        }
        return validMoves;
    }

    /**
     * @return the number of {@code MOVE} the player has performed during this specific turn
     */
    public int getNumberOfMove() {
        return numberOfMove;
    }

    /**
     * Increments the {@code numberOfMove} integer of the class
     */
    public void incrementNumberOfMove() {
        this.numberOfMove++;
    }

    /**
     * @return the number of {@code BUILD} the player has performed during this specific turn
     */
    public int getNumberOfBuild() {
        return numberOfBuild;
    }

    /**
     * Increments the {@code numberOfBuild} integer of the class
     */
    public void incrementNumberOfBuild() {
        this.numberOfBuild++;
    }

    /**
     * @return true if the player has built before moving, false otherwise
     */
    public boolean getHasBuiltBeforeMove() {
        return hasBuiltBeforeMove;
    }

    /**
     * @param hasBuiltBeforeMove the boolean that says if the player has built before the move
     */
    public void setHasBuiltBeforeMove(boolean hasBuiltBeforeMove) {
        this.hasBuiltBeforeMove = hasBuiltBeforeMove;
    }
}
