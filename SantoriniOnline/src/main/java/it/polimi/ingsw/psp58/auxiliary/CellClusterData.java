package it.polimi.ingsw.psp58.auxiliary;

import it.polimi.ingsw.psp58.model.WorkerColors;
import it.polimi.ingsw.psp58.model.gamemap.CellCluster;
import it.polimi.ingsw.psp58.model.gamemap.Worker;

import java.io.Serializable;

public class CellClusterData implements Serializable {

    private final int[] blocks;
    private final boolean domeOnTop;
    private final Worker.IDs workerOnTop;
    private final String usernamePlayer;
    private final WorkerColors workerColor;

    public CellClusterData(CellCluster cellCluster) {
        blocks = cellCluster.toIntArray();
        this.domeOnTop = cellCluster.isComplete();
        this.workerOnTop = cellCluster.getWorkerID();
        this.usernamePlayer = cellCluster.getWorkerOwnerUsername();
        this.workerColor = cellCluster.getWorkerColor();
    }

    public WorkerColors getWorkerColor() {
        if(getWorkerOnTop()!=null) {
            return workerColor;
        }
        return null;
    }

    public Worker.IDs getWorkerOnTop() {
        if(workerOnTop!=null){
            return workerOnTop;
        }
        return null;
    }

    public int[] getBlocks() {
        return blocks;
    }

    public String getUsernamePlayer() {
        if(workerOnTop!=null){
            return usernamePlayer;
        }
        return null;
    }

    public boolean isDomeOnTop() {
        return domeOnTop;
    }

    public boolean isFree() {
        return !domeOnTop && usernamePlayer == null && blocks.length == 0;
    }
}
