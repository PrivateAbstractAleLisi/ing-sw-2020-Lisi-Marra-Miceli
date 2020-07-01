package it.polimi.ingsw.psp58.model.gods;

import it.polimi.ingsw.psp58.model.BehaviourManager;
import it.polimi.ingsw.psp58.model.Card;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.model.Player;
import it.polimi.ingsw.psp58.exceptions.InvalidBuildException;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;
import it.polimi.ingsw.psp58.model.gamemap.Island;
import it.polimi.ingsw.psp58.model.gamemap.Worker;

/**
 * Hephaestus Card implementation.
 */
public class Hephaestus extends Card {
    private int[] lastBuiltPosition;

    public Hephaestus(Player player) {
        super(player);
        name = CardEnum.HEPHAESTUS;
        lastBuiltPosition = new int[]{-1, -1};
    }

    /**
     * It doesn't update the lastBuiltPosition, it just checks if you have already built here or not in the turn
     *
     * @param desiredX where you want to build
     * @param desiredY where you want to build
     * @return false never built in this turn, true he built here
     */
    private boolean hasAlreadyBuiltHereInThisTurn(int desiredX, int desiredY) {
        if (lastBuiltPosition[1] == -1 && lastBuiltPosition[0] == -1) { //he never built in this turn
            return false;
        } else { //he build at least one block
            return lastBuiltPosition[0] == desiredX && lastBuiltPosition[1] == desiredY;
        }
    }

    /**
     * Check if the player has already built during this turn.
     * @return true if the player already build during this turn, false otherwise
     */
    private boolean hasAlreadyBuiltInThisTurn() {
        return !(lastBuiltPosition[1] == -1 && lastBuiltPosition[0] == -1);
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
    public void build(Worker worker, BlockTypeEnum block, int desiredX, int desiredY, Island island) throws InvalidBuildException, CloneNotSupportedException {
        super.build(worker, block, desiredX, desiredY, island);
        lastBuiltPosition[0] = desiredX;
        lastBuiltPosition[1] = desiredY;
    }

    /**
     * Check if the construction can be done from the actual position of the {@link Worker}.
     * If the player try to build twice but not in the same cell, return false.
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
        if (hasAlreadyBuiltInThisTurn()) {
            if (hasAlreadyBuiltHereInThisTurn(desiredX, desiredY)) {
                if (block == BlockTypeEnum.DOME) return false;
                else return super.isValidConstruction(block, actualX, actualY, desiredX, desiredY, island);
            } else return false;
        }
        return super.isValidConstruction(block, actualX, actualY, desiredX, desiredY, island);
    }

    /**
     * Reset the {@code behaviour} of the {@link Player} to the default value of the card and set 2 the block placement left.
     */
    @Override
    public void resetBehaviour() {
        BehaviourManager behaviour = playedBy.getBehaviour();
        super.resetBehaviour();
        behaviour.setBlockPlacementLeft(2); //the worker may build one additional time

        lastBuiltPosition = new int[]{-1, -1};
    }
}
