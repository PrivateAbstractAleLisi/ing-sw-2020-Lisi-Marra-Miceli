package it.polimi.ingsw.psp58.model.gods;

import it.polimi.ingsw.psp58.model.BehaviourManager;
import it.polimi.ingsw.psp58.model.Card;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.model.Player;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;
import it.polimi.ingsw.psp58.model.gamemap.CellCluster;
import it.polimi.ingsw.psp58.model.gamemap.Island;
import it.polimi.ingsw.psp58.model.gamemap.Worker;

import static it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum.DOME;

/**
 * Zeus Card implementation.
 */
public class Zeus extends Card {

    public Zeus(Player p) {
        super(p);
        name = CardEnum.ZEUS;
    }

    /**
     * Check if the construction can be done from the actual position of the {@link Worker}.
     *
     * @param block    level of construction {@link Player} wants to build
     * @param actualX  Actual X Position of the worker
     * @param actualY  Actual Y Position of the worker
     * @param desiredX X Position where the player wants to place the worker
     * @param desiredY Y Position where the player wants to place the worker
     * @param island   The current board of game
     * @return true when the construction can be done from the actual position, false otherwise
     */
    @Override
    protected boolean isValidConstruction(BlockTypeEnum block, int actualX, int actualY, int desiredX, int desiredY, Island island) throws IndexOutOfBoundsException {
        CellCluster desiredCellCluster = island.getCellCluster(desiredX, desiredY);
        boolean samePosition = actualX == desiredX && actualY == desiredY;
        BehaviourManager behaviour = playedBy.getBehaviour();

        //check if the behavior allow to build
        if (behaviour.getBlockPlacementLeft() <= 0) {
            return false;
        }

        if (desiredCellCluster.hasWorkerOnTop() && !samePosition) {
            return false;
        }
        if (desiredCellCluster.isComplete()) {
            return false;
        }
        if(samePosition && block==DOME){
            return false;
        }
        //calculate the euclidean distance and check that distance < 2 (return false otherwise)
        if (distance(actualX, actualY, desiredX, desiredY) >= 2) {
            return false;
        }

        //generate an array with the cellCluster structure (plus the new block) and analyze that with the next function
        int[] desiredConstruction = desiredCellCluster.toIntArrayWithHypo(block);
        return isValidBlockPlacement(block, desiredConstruction, behaviour);
    }
}
