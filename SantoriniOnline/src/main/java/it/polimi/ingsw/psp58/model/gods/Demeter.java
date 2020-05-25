package it.polimi.ingsw.psp58.model.gods;

import it.polimi.ingsw.psp58.exceptions.InvalidBuildException;
import it.polimi.ingsw.psp58.model.BehaviourManager;
import it.polimi.ingsw.psp58.model.Card;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.model.Player;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;
import it.polimi.ingsw.psp58.model.gamemap.CellCluster;
import it.polimi.ingsw.psp58.model.gamemap.Island;
import it.polimi.ingsw.psp58.model.gamemap.Worker;

/**
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

    @Override
    protected boolean isValidConstruction(BlockTypeEnum block, int actualX, int actualY, int desiredX, int desiredY, Island island) throws IndexOutOfBoundsException {
        if (hasAlreadyBuiltHereInThisTurn(desiredX, desiredY)) {
            return false;
        }
        return super.isValidConstruction(block, actualX, actualY, desiredX, desiredY, island);
    }

    @Override
    public void build(Worker worker, BlockTypeEnum block, int desiredX, int desiredY, Island island) throws InvalidBuildException, CloneNotSupportedException {
        int actualX = worker.getPosition()[0];
        int actualY = worker.getPosition()[1];
        int[] oldCellCluster = null;

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
     * it doesn't update the lastBuiltPosition, it just checks if you have already built here or not in the turn
     *
     * @param desidedX where you want to build
     * @param desiredY where you want to build
     * @return false never built in this turn, true he built here
     */
    private boolean hasAlreadyBuiltHereInThisTurn(int desidedX, int desiredY) {
        if (lastBuiltPosition[0] == -1 && lastBuiltPosition[1] == -1) { //he never built in this turn
            return false;
        } else { //he build at least one block
            if (lastBuiltPosition[0] == desidedX && lastBuiltPosition[1] == desiredY) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void resetBehaviour() {

        BehaviourManager behaviour = playedBy.getBehaviour();
        behaviour.setBlockPlacementLeft(2); //the worker may build one additional time
        behaviour.setMovementsRemaining(1);
        behaviour.setCanClimb(true);
        behaviour.setCanBuildDomeEverywhere(false);
        lastBuiltPosition = new int[]{-1, -1};
    }
}
