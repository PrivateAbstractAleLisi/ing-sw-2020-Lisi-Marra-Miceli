package it.polimi.ingsw.psp58.view.UI.CLI.utility;

import it.polimi.ingsw.psp58.model.gamemap.CellCluster;
import org.junit.Before;
import org.junit.Test;
import it.polimi.ingsw.psp58.auxiliary.CellClusterData;
import it.polimi.ingsw.psp58.auxiliary.IslandData;

public class IslandUtilityTest {

    IslandData isla;


    @Before
    public void setUp() throws Exception {
        isla = new IslandData();
        CellClusterData [][]d = new CellClusterData[5][5];
        for(int i = 0; i<5; i++) {
            for (int j = 0; j < 5; j++) {
                d[i][j] = new CellClusterData(new CellCluster());
            }
        }
        isla.fillIsland(d);

    }

    @Test
    public void displayIsland() {
        new IslandUtility(isla).displayIsland();
        return;
    }
}