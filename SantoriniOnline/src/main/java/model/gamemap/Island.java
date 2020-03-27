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
    }

    public void placeWorker(Worker w, int x, int y) {
        grid[x][y].addWorker(w); //implementare in add worker l'update della posizione per non
        w.setPosition(x, y); //RIPETERE CODICE
    }



    public void moveWorker(Worker w, int x, int y) {

        int oldX = w.getPosition()[0];
        int oldY = w.getPosition()[1];
        grid[oldX][oldY].removeWorker();
        grid[x][y].addWorker(w);
        w.setPosition(x, y); //RIPETERE CODICE

    }

    public void buildBlock(BlockTypeEnum b, int x, int y) {
        try {
            grid[x][y].build(b);
        } catch (InvalidBuildException e) {
            System.err.println("TEST FALLITO");
        }
    }

    public CellCluster getClusterAtPosition(int x, int y) {
        return grid[x][y];
    }

}
