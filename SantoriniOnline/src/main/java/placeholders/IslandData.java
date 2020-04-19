package placeholders;

import model.gamemap.CellCluster;

public class IslandData {

    private CellClusterData[][] grid;

    public IslandData() {

        grid = new CellClusterData[5][5];

    }

    /**
     * fills the island with a copy of a cell cluster for each cell
     * @param islandData a 5x5 matrix of cell cluster immutable copies
     */
    public void fillIsland(CellClusterData[][] islandData) {
        grid = islandData;
    }

    public CellClusterData getCellCluster(int x, int y) {
        return grid[x][y];
    }

}
