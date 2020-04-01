package placeholders;

import model.gamemap.CellCluster;
import model.gamemap.Island;

public class IslandPrinter {
    public static final int BOARD_SIZE = 8;
    private Island islandDecorated;
    public IslandPrinter(Island i) {
        islandDecorated = i;
    }

    private void printCellCluster (int x, int y) throws CloneNotSupportedException {
        if(islandDecorated.getCellCluster(x, y).isFree()) {
            System.out.print("| " + " " + " ");
        }
        else if (islandDecorated.getCellCluster(x,y).isComplete()) {
            System.out.print("| " + "O" + " ");
        }
        else{
            CellCluster xy = islandDecorated.getCellCluster(x,y);
            int height = xy.getCostructionHeight();
            switch (height) {
                case 1:
                    System.out.print("| " + "1" + " ");
                    break;
                case 2:
                    System.out.print("| " + "2" + " ");
                    break;
                case 3:
                    System.out.print("| " + "3" + " ");
                    break;

            }
            if (xy.hasWorkerOnTop()) {
                //TODO STAMPA IL PLAYER come ♦︎ del relativo colore (usare costanti ASCII)
            }
        }
    }
    public void displayIsland() throws CloneNotSupportedException {
        System.out.println("ISLAND: ");
        for (int row = 0; row < 5; row++)
        {
            System.out.println("");
            System.out.println("---------------------");

            for (int column = 0; column < 5; column++)
            {
                printCellCluster(row, column);
            }
            System.out.print("|");
        }
        System.out.println("");
        System.out.println("---------------------");
    }
}