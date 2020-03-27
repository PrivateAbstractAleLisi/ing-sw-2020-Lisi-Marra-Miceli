package model.gamemap;

import model.exception.InvalidBuildException;

public class Island {

    private CellCluster[][] grid;
    /*Game Manager TODO*/ private Object match;

    /**
     * Constructor, initialize all the island's cells
     */

    public Island() {
        grid = new CellCluster[5][5];

        //initialize all cells wit a double foreach
        for (CellCluster[] cellClusters : grid) {
            for (CellCluster cellCluster : cellClusters) {
                cellCluster = new CellCluster();
            }
        }
        System.out.println("DEBUG: Island has been initialized");
    }

    /**
     *
     * @param i an integer that represents an offset (x or y axis) in the island grid
     * @return true if the offset is inside the island, false if it's outside (Invalid).
     */
    private boolean inRange(int i) {
        if (i>= 0 && i<5) {
            return true;
        }
        else {
            return false;
        }
    }

    public void placeWorker(Worker w, int x, int y) {
        grid[x][y].addWorker(w); //implementare in add worker l'update della posizione per non
        w.setPosition(x, y); //RIPETERE CODICE
        System.out.println("DEBUG: Island: worker placed at index " + x +","+ y );
    }


    public void moveWorker(Worker w, int x, int y) {

        int oldX = w.getPosition()[0];
        int oldY = w.getPosition()[1];
        grid[oldX][oldY].removeWorker();
        grid[x][y].addWorker(w);
        w.setPosition(x, y); //RIPETERE CODICE
        System.out.println("DEBUG: Island: worker successfully moved from position " + oldX +","+ oldY +  "to position " +
                 + x + "," + y );

    }

    public void buildBlock(BlockTypeEnum b, int x, int y) {

        try {
            grid[x][y].build(b);
        } catch (InvalidBuildException e) {
            System.err.println("TEST FALLITO");
        }

        System.out.println("DEBUG: Island: block build on position" + x + "," + y + " block type: " + b.toString());
    }

    public CellCluster getClusterAtPosition(int x, int y) {
        return grid[x][y];
    }

}
