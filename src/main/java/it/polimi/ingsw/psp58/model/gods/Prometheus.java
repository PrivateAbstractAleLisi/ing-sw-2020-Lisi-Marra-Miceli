package it.polimi.ingsw.psp58.model.gods;

import it.polimi.ingsw.psp58.model.BehaviourManager;
import it.polimi.ingsw.psp58.model.Card;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.model.Player;
import it.polimi.ingsw.psp58.exceptions.InvalidBuildException;
import it.polimi.ingsw.psp58.exceptions.InvalidMovementException;
import it.polimi.ingsw.psp58.exceptions.WinningException;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;
import it.polimi.ingsw.psp58.model.gamemap.Island;
import it.polimi.ingsw.psp58.model.gamemap.Worker;

/**
 * Prometheus Card implementation, part of the implementation is located in the controller.
 */
public class Prometheus extends Card {
    private boolean hasAlreadyBuilt;

    public Prometheus(Player player) {
        super(player);
        name = CardEnum.PROMETHEUS;

        hasAlreadyBuilt = false;
    }

    /**
     * Move a {@link Worker} from his actual position to the desired coordinates and if the player has already built set to false the canClimb behaviour.
     *
     * @param worker   A worker of the actual player
     * @param desiredX X Position where the player wants to move the worker
     * @param desiredY Y Position where the player wants to move the worker
     * @param island   The current board of game
     * @throws InvalidMovementException If the movement is not valid throw a new exception.
     * @throws WinningException         If the player won, throw a WinningException
     */
    @Override
    public void move(Worker worker, int desiredX, int desiredY, Island island) throws InvalidMovementException, WinningException {
        if (hasAlreadyBuilt) {
            BehaviourManager behaviour = playedBy.getBehaviour();
            behaviour.setCanClimb(false);
            behaviour.setBlockPlacementLeft(1);
        }
        super.move(worker, desiredX, desiredY, island);
    }

    /**
     * Build a block, near a {@link Worker}, in the the desired coordinates and set to true the hasAlreadyBuilt attribute.
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
        hasAlreadyBuilt = true;
    }

    /**
     * Reset the {@code behaviour} of the {@link Player} to the default value of the card.
     */
    @Override
    public void resetBehaviour() {
        super.resetBehaviour();
        hasAlreadyBuilt = false;
    }
}
