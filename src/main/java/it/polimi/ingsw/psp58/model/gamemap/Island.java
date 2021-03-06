package it.polimi.ingsw.psp58.model.gamemap;


import it.polimi.ingsw.psp58.auxiliary.Range;
import it.polimi.ingsw.psp58.exceptions.InvalidBuildException;
import it.polimi.ingsw.psp58.exceptions.InvalidMovementException;
import it.polimi.ingsw.psp58.auxiliary.CellClusterData;
import it.polimi.ingsw.psp58.auxiliary.IslandData;

/**
 * This class represents the board game with the matrix of CellCluster
 */
public class Island {
    private final Range indexRange;

    private final CellCluster[][] grid;

    private int numberOfCompleteTowers;

    /**
     * Constructor, initialize all the island's cells using a for loop
     */
    public Island() {
        grid = new CellCluster[5][5];

        //initialize *all cells* with a double for
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                grid[i][j] = new CellCluster();
            }
        }

        indexRange = new Range(0, 4);
        numberOfCompleteTowers = 0;

        System.out.println("DEBUG: Island has been initialized");

    }

    /**
     * @param x index of the cell (x)
     * @param y index of the cell (x)
     * @return true if the index are correct, otherwise false.
     */
    private boolean inRange(int x, int y) {
        if (indexRange.contains(x) && indexRange.contains(y)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * it places the worker on the board only if it's not already placed.
     *
     * @param w Worker that has to be placed
     * @param x index of the cell (x) where the worker will be placed
     * @param y index of the cell (y) where the worker will be placed
     * @throws InvalidMovementException when you try to place a worker in a cell the has already a worker on top
     */
    public void placeWorker(Worker w, int x, int y) throws InvalidMovementException {

        if (!inRange(x, y)) {
            throw new IndexOutOfBoundsException();
        }
        if (isWorkerAlreadyPlaced(w)) {
            return;
        }

        grid[x][y].addWorker(w);
        w.setPlacedOnIsland(true);
        w.setPosition(x, y);

        System.out.println("DEBUG: Island: worker placed at index " + x + "," + y);
    }

    /**
     * @param w Worker that has to be placed
     * @param x index of the cell (x) where the worker will be placed
     * @param y index of the cell (y) where the worker will be placed
     * @throws InvalidMovementException when you try to move a worker in a cell the has already a worker on top
     */
    public void moveWorker(Worker w, int x, int y) throws InvalidMovementException {


        if (!inRange(x, y)) {
            throw new IndexOutOfBoundsException();
        }
        int oldX = w.getPosition()[0];
        int oldY = w.getPosition()[1];
        grid[oldX][oldY].removeWorker(); //TODO gestire (forse conviene internamente) il caso dove non posso rimuovere nessuno
        grid[x][y].addWorker(w);
        w.setPosition(x, y); //RIPETERE CODICE


        System.out.println("DEBUG: Island: worker successfully moved from position " + oldX + "," + oldY + "to position " +
                +x + "," + y);

    }

    /**
     * @param b the block you want to build over the cell
     * @param x index of the cell (x)
     * @param y index of the cell (y)
     * @throws InvalidBuildException when you try to build in the wrong order or if the cell is full
     */
    public void buildBlock(BlockTypeEnum b, int x, int y) throws InvalidBuildException {
        if (!inRange(x, y)) {
            throw new IndexOutOfBoundsException("Index out of island bounds");
        }

        CellCluster xy = grid[x][y];

        if (xy.checkBuildingBlockOrder(b)) { //Checks if this block is inserted the order is legal
            xy.build(b);
        } else {
            throw new InvalidBuildException("By inserting this block the costruction order is invalid.");
        }
    }

    /**
     * finds the worker and removes it from the board
     *
     * @param w worker that will be removed
     */
    public void removeWorker(Worker w) {
        int[] pos = w.getPosition();
        CellCluster hasWorkerIn = grid[pos[0]][pos[1]];
        if (hasWorkerIn.hasWorkerOnTop()) {
            hasWorkerIn.removeWorker();
            w.setPlacedOnIsland(false);
        }
    }

    /**
     * @param x index of the cell (x)
     * @param y index of the cell (y)
     * @return a copy of the CellCluster at position (x,y)
     */
    public CellCluster getCellCluster(int x, int y) {

        if (!inRange(x, y)) {
            throw new IndexOutOfBoundsException("Index out of island bounds");
        }

        CellCluster real = grid[x][y];
        CellCluster copy = null;
        try {
            copy = (CellCluster) real.clone();
            return copy;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isWorkerAlreadyPlaced(Worker w) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (grid[i][j].hasWorkerOnTop() && grid[i][j].getWorkerID().equals(w.getWorkerID())) {
                    if (grid[i][j].getWorkerOwnerUsername().equals(w.getPlayerUsername())) {
                        return true;
                    }

                }

            }
        }
        return false;
    }

    private CellClusterData[][] getGridData() {
        CellClusterData[][] newGrid = new CellClusterData[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                newGrid[i][j] = new CellClusterData(getCellCluster(i, j));
            }
        }
        return newGrid;
    }

    public IslandData getIslandDataCopy() {
        IslandData copy = new IslandData();
        copy.fillIsland(getGridData());
        return copy;
    }

    public void incrementNumberOfCompleteTowers() {
        numberOfCompleteTowers++;
    }

    public int getNumberOfCompleteTowers() {
        return numberOfCompleteTowers;
    }
}
