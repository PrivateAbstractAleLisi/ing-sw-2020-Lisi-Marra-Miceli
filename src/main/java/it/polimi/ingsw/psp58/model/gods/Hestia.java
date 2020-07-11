package it.polimi.ingsw.psp58.model.gods;

import it.polimi.ingsw.psp58.exceptions.InvalidBuildException;
import it.polimi.ingsw.psp58.model.Card;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.model.Player;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;
import it.polimi.ingsw.psp58.model.gamemap.Island;
import it.polimi.ingsw.psp58.model.gamemap.Worker;

/**
 * Hestia Card implementation.
 */
public class Hestia extends Card {
    private boolean hasAlreadyBuilt;

    public Hestia(Player p) {
        super(p);
        name = CardEnum.HESTIA;
        hasAlreadyBuilt = false;
    }

    /**
     * Build a block, near a {@link Worker}, in the the desired coordinates and save that's the player already build..
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
        super.build(worker, block, desiredX, desiredY, island);
        if (!hasAlreadyBuilt) hasAlreadyBuilt = true;
    }

    /**
     * Check if the construction can be done from the actual position of the {@link Worker}.
     * If the player try to build twice on a perimeter cell return false.
     *
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
        //if has already build she cannot build in a perimeter space
        if (hasAlreadyBuilt && (desiredX == 0 || desiredX == 4 || desiredY == 0 || desiredY == 4)) { //it's a perimeter cell
            return false;
        }
        return super.isValidConstruction(block, actualX, actualY, desiredX, desiredY, island);
    }

    /**
     * Reset the {@code behaviour} of the {@link Player} to the default value of the card and set 2 the block placement left.
     */
    @Override
    public void resetBehaviour() {
        super.resetBehaviour();
        playedBy.getBehaviour().setBlockPlacementLeft(2);
        hasAlreadyBuilt = false;
    }
}
