package model.gamemap;

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

    public void placeWorker (Worker w, int x, int y) {
        grid[x][y].
    }
    public void moveWorker (Worker w, int x, int y) {

    }
    public void buildBlock (BuildingBlock b, int x, int y) {

    }
    public CellCluster getClusterAtPosition(int x, int y) {

    }

}
