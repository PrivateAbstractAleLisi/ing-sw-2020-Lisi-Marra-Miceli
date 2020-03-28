package placeholders;

import model.gamemap.*;

public class Main {
    public static void main(String[] args) {

        Island island = new Island();
        IslandPrinter ip = new IslandPrinter(island);
        ip.displayIsland();
        island.buildBlock(BlockTypeEnum.LEVEL1, 3, 4);
        island.buildBlock(BlockTypeEnum.LEVEL2, 3, 4);
        island.buildBlock(BlockTypeEnum.LEVEL3, 1, 2);
        ip.displayIsland();

    }
}
