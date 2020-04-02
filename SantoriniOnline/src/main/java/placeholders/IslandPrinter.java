package placeholders;

import model.gamemap.CellCluster;
import model.gamemap.Island;

public class IslandPrinter {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

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

                System.out.print("| " + ANSI_BLUE + "♦︎" + ANSI_RESET + " ");
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
