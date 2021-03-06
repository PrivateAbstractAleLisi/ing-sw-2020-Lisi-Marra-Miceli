package it.polimi.ingsw.psp58.model.gamemap;

import it.polimi.ingsw.psp58.exceptions.InvalidBuildException;
import it.polimi.ingsw.psp58.exceptions.InvalidMovementException;
import it.polimi.ingsw.psp58.model.WorkerColors;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum.*; //Controllare che cosa significa, generato automaticamente

/**
 * This class represents the single cell of the board game
 */
public class CellCluster implements Cloneable {
    private final List<BlockTypeEnum> construction;
    private boolean isComplete, isFree;
    private Worker worker;

    public CellCluster() {
        construction = new ArrayList<>();
        isComplete = false;
        isFree = true;
    }

    /**
     * Checks if the construction inside this cell has a valid CostructionBlock order
     *
     * @param toBeAdded block to be added
     * @return true if the order is valid, false otherwise
     */
    public boolean checkBuildingBlockOrder(BlockTypeEnum toBeAdded) {
        List<BlockTypeEnum> constructionAfter = new ArrayList<>(construction);
        constructionAfter.add(toBeAdded);

        int[] array = toIntArray(constructionAfter);

        for (int i = 0; i < array.length - 1; i++) { //checks if it's ascending order
            if (array[i] >= array[i + 1]) {
                return false;
            }
        }

        return true;
    }

    private int[] toIntArray(List<BlockTypeEnum> construction) {
        int[] array = new int[construction.size()]; //Auxiliary array
        for (int i = 0; i < array.length; i++) { //Converts the construction into an integer array

            switch (construction.get(i)) {
                case LEVEL1:
                    array[i] = 1;
                    break;
                case LEVEL2:
                    array[i] = 2;
                    break;
                case LEVEL3:
                    array[i] = 3;
                    break;
                case DOME:
                    array[i] = 4;
                    break;
            }
        }

        return array;
    }

    public int[] toIntArray() {
        int[] array = new int[construction.size()]; //Auxiliary array
        for (int i = 0; i < array.length; i++) { //Converts the construction into an integer array

            switch (construction.get(i)) {
                case LEVEL1:
                    array[i] = 1;
                    break;
                case LEVEL2:
                    array[i] = 2;
                    break;
                case LEVEL3:
                    array[i] = 3;
                    break;
                case DOME:
                    array[i] = 4;
                    break;
            }
        }

        return array;
    }

    public int[] toIntArrayWithHypo(BlockTypeEnum toBeAdded) {
        List<BlockTypeEnum> constructionAfter = new ArrayList<>(construction);
        constructionAfter.add(toBeAdded);

        return toIntArray(constructionAfter);
    }

    public void build(BlockTypeEnum block) throws InvalidBuildException {
        isFree = false;
        //Build:
        if (!isComplete) {

            construction.add(block);

            if (construction.contains(DOME)) {
                isComplete = true;
            }
        } else {
            throw new InvalidBuildException("Beware, you cannot build here, this cell is full.");
        }
    }

    //GETTERS

    /**
     * @return the actual height of construction, 0 if the cell is free
     */
    public int getCostructionHeight() {
        if (isFree && construction.isEmpty()) {
            return 0;
        } else {
            return construction.size();
        }

    }

    /**
     * @return true if the cell has a complete construction (with the dome)
     */
    public boolean isComplete() {
        return isComplete;
    }

    /**
     * @return true if the cell is free (no player, no constructions)
     */
    public boolean isFree() {
        return isFree;
    }

    public void addWorker(Worker worker) throws InvalidMovementException {
        if (this.worker != null) {
            throw new InvalidMovementException("WorkerAlreadyOnTop");
        } else if (isComplete) {
            throw new InvalidMovementException("DomeOnTop");
        } else {
            this.worker = worker;
        }

        isFree = false;
    }


    public void removeWorker() {
        worker = null;
    }


    public boolean hasWorkerOnTop() {
        if (worker != null) {
            return true;
        } else {
            return false;
        }
    }

    public String getWorkerOwnerUsername() {
        if (hasWorkerOnTop()) {
            return worker.getPlayerUsername();
        }
        return null;
    }

    public Worker.IDs getWorkerID() {
        if (hasWorkerOnTop()) {
            return worker.getWorkerID();
        }
        return null;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public WorkerColors getWorkerColor() {
        if (hasWorkerOnTop()) {
            return worker.getColor();
        }
        return null;
    }

    public BlockTypeEnum nextBlockToBuild() throws InvalidBuildException {
        if (getCostructionHeight() == 0) {
            return LEVEL1;
        } else {
            BlockTypeEnum lastBlockBuilt = construction.get(getCostructionHeight() - 1);
            switch (lastBlockBuilt) {
                case LEVEL1:
                    return LEVEL2;
                case LEVEL2:
                    return LEVEL3;
                case LEVEL3:
                    return DOME;
                case DOME:
                    throw new InvalidBuildException("BLOCK_NOT_AVAILABLE");
            }
        }
        return null;
    }
}
