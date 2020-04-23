package model;

import model.exception.DefeatException;
import model.gamemap.Worker;

import java.util.ArrayList;
import java.util.Arrays;

public class Turn {
    private Player currentPlayer;
    private Worker.IDs workerID;
    private int numberOfMove;
    private int numberOfBuild;
    private int[] startingPosition;
    private BoardManager boardManager;

    public Turn(Player currentPlayer, BoardManager boardManager) {
        this.currentPlayer = currentPlayer;
        this.numberOfBuild = 0;
        this.numberOfMove = 0;
        this.boardManager = boardManager;
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
    public ArrayList<int[]> validActions(Worker.IDs workerID, TurnAction action) {
        ArrayList<int[]> validMoves = new ArrayList<int[]>();
        int[] position = null;
        Worker worker = currentPlayer.getWorker(workerID);
        int[] startingPosition = worker.getPosition();
        for (int i = startingPosition[0] - 1; i <= startingPosition[0] + 1; i++) {
            for (int j = startingPosition[1] - 1; j <= startingPosition[0] + 1; j++) {
                if (action == TurnAction.MOVE) {
                    if (currentPlayer.getCard().checkCellMovementAvailability(startingPosition[0], startingPosition[1], i, j, boardManager.getIsland())) {
                        position = new int[2];
                        position[0] = i;
                        position[1] = j;
                        validMoves.add(position);
                    }
                } else if (action == TurnAction.BUILD) {
                    if (currentPlayer.getCard().checkCellCostructionAvailability(startingPosition[0], startingPosition[1], i, j, boardManager.getIsland())) {
                        position = new int[2];
                        position[0] = i;
                        position[1] = j;
                        validMoves.add(position);
                    }
                }
            }
        }
        return validMoves;
    }

    public int getNumberOfMove() {
        return numberOfMove;
    }

    public void setNumberOfMove(int numberOfMove) {
        this.numberOfMove = numberOfMove;
    }

    public int getNumberOfBuild() {
        return numberOfBuild;
    }

    public void setNumberOfBuild(int numberOfBuild) {
        this.numberOfBuild = numberOfBuild;
    }
}
