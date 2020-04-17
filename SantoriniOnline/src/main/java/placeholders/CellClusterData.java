package placeholders;

import model.gamemap.CellCluster;
import model.gamemap.Worker;

public class CellClusterData {
    private final int[] blocks;
    private final boolean domeOnTop;
    private final Worker.IDs workerOnTop;
    private final String usernamePlayer;

    public CellClusterData(CellCluster cellCluster) {
        blocks = cellCluster.toIntArray();
        this.domeOnTop = cellCluster.isComplete();
        this.workerOnTop = cellCluster.getWorkerID();
        this.usernamePlayer = cellCluster.getWorkerOwnerUsername();
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
}
