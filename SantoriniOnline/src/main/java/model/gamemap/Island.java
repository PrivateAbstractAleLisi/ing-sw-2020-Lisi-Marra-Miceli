package model.gamemap;

public class Island {

    private CellCluster[][] grid;
    /*Game Manager TODO*/ private Object match;

    /**
     * Costruttore, inizializza tutte le celle dell'isola
     */
    public Island() {
        grid = new CellCluster[5][5];
        //inizializzo tutte le celle con un doppio foreach
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
