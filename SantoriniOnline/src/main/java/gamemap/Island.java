package gamemap;

public class Island {
    private CellCluster[][] grid;
    /*Game Manager TODO*/ private Object match;
    public Island() {
        grid = new CellCluster[5][5];
        //inizializzo tutte le celle con un doppio foreach
        for (CellCluster[] cellClusters : grid) {
            for (CellCluster cellCluster : cellClusters) {
                cellCluster = new CellCluster();
            }
        }
    }
}
