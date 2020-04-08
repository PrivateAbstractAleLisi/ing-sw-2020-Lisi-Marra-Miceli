package model;

import model.exception.DefeatException;
import model.gamemap.Worker;

import java.util.ArrayList;
import java.util.Arrays;

public class Turn {
    private Player currentPlayer;
    private Worker.IDs workerID;
    private boolean hasAlreadyMoved;
    private boolean hasAlreadyBuild;
    private int[] startingPosition;
    private BoardManager boardManager;

    public Turn(Player currentPlayer, BoardManager boardManager) {
        this.currentPlayer = currentPlayer;
        this.hasAlreadyBuild=false;
        this.hasAlreadyMoved=false;
        this.boardManager= boardManager;
    }

    /**
     * The player must perform all the actions of the turn with the same player so he has to choose one
     * @param workerID the id of the worker chosen by the player at the start of the turn
     */
    public void chooseWorker(Worker.IDs workerID) {
        this.workerID = workerID;
        startingPosition= new int[2];
        startingPosition= currentPlayer.getWorker(workerID).getPosition();
    }

    /**
     *
     * @return the current player is playing the turn
     */
    public Player getCurrentPlayer(){
        return currentPlayer;
    }

    /**
     *
     * @return the worker ID used by the currentPlayer in this turn
     */
    public Worker.IDs getWorkerID() {
        return workerID;
    }

    /**
     *
     * @return the starting position of the worker
     */
    public int[] getStartingPosition() {
        return startingPosition;
    }

    /**
     * Check if the worker position is the same as the start of the turn
     * @throws DefeatException if the player as not moved in the turn
     */
    public void checkMoveDuringTurn() throws DefeatException{
        if(Arrays.equals(currentPlayer.getWorker(workerID).getPosition(), startingPosition))
            throw new DefeatException (currentPlayer.getUsername());
    }

    /**
     * Calculate all the possible destination of an action of a specific worker passed both as parameters
     * @param workerID the worker that perform the action
     * @param action the action to perform
     * @return a list of all the possible move or build destination
     * @throws DefeatException if the player as no move and build actions possible
     */
    public ArrayList<int[]> validActions(Worker.IDs workerID, turnAction action) throws DefeatException {
        ArrayList<int[]> validMoves = new ArrayList<int[]>();
        int[] position = null;
        Worker worker = currentPlayer.getWorker(workerID);
        for (int i= worker.getPosition()[0]-1; i<=1; i++) {
            for (int j = worker.getPosition()[1] - 1; j <= 1; j++) {
                if (action==turnAction.MOVE) {
                    if (currentPlayer.getCard().checkCellMovementAvailability(worker.getPosition()[0], worker.getPosition()[1], i, j, boardManager.getIsland()))
                        position = new int[2];
                        position[0] = i;
                        position[1] = j;
                        validMoves.add(position);
                } else if (action==turnAction.BUILD) {
                    if (currentPlayer.getCard().checkCellBuildAvailability(worker.getPosition()[0], worker.getPosition()[1], i, j, boardManager.getIsland())) {
                        position = new int[2];
                        position[0] = i;
                        position[1] = j;
                        validMoves.add(position);
                    }
                }
            }
        }
        if (validMoves.size() == 0) throw new DefeatException(currentPlayer.getUsername());
        return validMoves;
    }

}
