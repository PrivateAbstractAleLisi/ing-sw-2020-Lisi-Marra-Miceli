package it.polimi.ingsw.psp58.model.gods;

import it.polimi.ingsw.psp58.exceptions.InvalidBuildException;
import it.polimi.ingsw.psp58.model.Card;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.model.Player;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;
import it.polimi.ingsw.psp58.model.gamemap.CellCluster;
import it.polimi.ingsw.psp58.model.gamemap.Island;
import it.polimi.ingsw.psp58.model.gamemap.Worker;

/**
 * Demeter Card implementation.
 * @author Ale Lisi
 */
public class Demeter extends Card {
    int[] lastBuiltPosition;
    private Worker.IDs workerChoosen;

    public Demeter(Player p) {
        super(p);
        name = CardEnum.DEMETER;
        lastBuiltPosition = new int[]{-1, -1};
    }

    /**
     * Check if the construction can be done from the actual position of the {@link Worker}.
     * If the player try to build on the same cell twice return false.
     * @param block    level of construction {@link Player} wants to build
     * @param actualX  Actual X Position of the worker
     * @param actualY  Actual Y Position of the worker
     * @param desiredX X Position where the player wants to place the worker
     * @param desiredY Y Position where the player wants to place the worker
     * @param island   The current board of game
     * @return true when the construction can be done from the actual position, false otherwise
     * @throws IndexOutOfBoundsException if the indexes of the desired position aren't valid.
     */
    @Override
    protected boolean isValidConstruction(BlockTypeEnum block, int actualX, int actualY, int desiredX, int desiredY, Island island) throws IndexOutOfBoundsException {
        if (hasAlreadyBuiltHereInThisTurn(desiredX, desiredY)) {
            return false;
        }
        return super.isValidConstruction(block, actualX, actualY, desiredX, desiredY, island);
    }


    /**
     * Build a block, near a {@link Worker}, in the the desired coordinates.
     *
     * @param worker   A worker of the actual player
     * @param block    level of block desired to build
     * @param desiredX X Position where the player wants to build the block
     * @param desiredY Y Position where the player wants to build the block
     * @param island   The current board of game
     * @throws InvalidBuildException Exception thrown when the coordinates are not valid, or the behaviour of the player block this action
     */
    @Override
    public void build(Worker worker, BlockTypeEnum block, int desiredX, int desiredY, Island island) throws InvalidBuildException {
        int actualX = worker.getPosition()[0];
        int actualY = worker.getPosition()[1];
        int[] oldCellCluster;

        CellCluster old = island.getCellCluster(desiredX, desiredY);
        if (old.getCostructionHeight() != 0) {
            oldCellCluster = old.toIntArray();
        } else {
            oldCellCluster = new int[1];
        }

        if (!isValidConstruction(block, actualX, actualY, desiredX, desiredY, island)) {
            throw new InvalidBuildException("Invalid build for this worker");
        }

        //check se utilizzo lo stesso worker
        if (lastBuiltPosition[0] == -1 && lastBuiltPosition[1] == -1) {
            workerChoosen = worker.getWorkerID();
        }else {
            if (worker.getWorkerID() != workerChoosen) {
                throw new IllegalArgumentException("DEMETER: on the second building you must use the same worker");
            }
        }

        island.buildBlock(block, desiredX, desiredY);
        if (!checkBlockPosition(island, block, desiredX, desiredY, oldCellCluster)) {
            throw new InvalidBuildException("The build is valid but there was an error applying desired changes");
        }

        if(oldCellCluster.length == 3 && block== BlockTypeEnum.DOME){
            island.incrementNumberOfCompleteTowers();
        }

        lastBuiltPosition = new int[]{desiredX, desiredY};
        //decrementa il numero di blocchi da costruire rimasti e ritorno true
        playedBy.getBehaviour().setBlockPlacementLeft(playedBy.getBehaviour().getBlockPlacementLeft() - 1);
    }

    /**
     * it doesn't update the lastBuiltPosition, it just checks if you have already built HERE or not in the turn
     *
     * @param desidedX where you want to build
     * @param desiredY where you want to build
     * @return false never built in this turn, true he built here
     */
    private boolean hasAlreadyBuiltHereInThisTurn(int desidedX, int desiredY) {
        if (lastBuiltPosition[0] == -1 && lastBuiltPosition[1] == -1) { //he never built in this turn
            return false;
        } else { //he build at least one block
            return lastBuiltPosition[0] == desidedX && lastBuiltPosition[1] == desiredY;
        }
    }

    /**
     * Reset the {@code behaviour} of the {@link Player} to the default value of the card and set 2 the block placement left.
     */
    @Override
    public void resetBehaviour() {

        super.resetBehaviour();
        playedBy.getBehaviour().setBlockPlacementLeft(2); //the worker may build one additional time
        lastBuiltPosition = new int[]{-1, -1};
    }
}
