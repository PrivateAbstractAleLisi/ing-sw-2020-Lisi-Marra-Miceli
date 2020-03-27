package model.gamemap;

import model.exception.*;

import java.util.ArrayList;
import java.util.List;

public class CellCluster {
    private List<BlockTypeEnum> costruction;
    private boolean isComplete, isFree;
    private Worker worker;
//    private final int x,y;

    public CellCluster() {
        costruction = new ArrayList<BlockTypeEnum>();
        isComplete = false;
        isFree = true;
        isComplete = false;
    }


    public void build(BlockTypeEnum block) throws InvalidBuildException {
        isFree = false;
        //Build:
        if (!isComplete) {

            costruction.add(block);

            if (costruction.contains(BlockTypeEnum.DOME)) {
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
        if (isFree && costruction.isEmpty()) {
            return 0;
        } else {
            return costruction.size();
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
    /*
    public Worker removeWorker () {
        Worker r = this.worker;
        worker = null;
        return this.worker;
    } */

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
}
