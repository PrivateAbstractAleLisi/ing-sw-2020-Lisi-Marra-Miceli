package view.CLI.utility;

import model.WorkerColors;
import model.gamemap.CellCluster;
import model.gamemap.Island;
import model.gamemap.Worker;
import placeholders.CellClusterData;
import placeholders.IslandData;

import static auxiliary.ANSIColors.*;

public class IslandUtility {

    private IslandData data;
    public IslandUtility(IslandData data) {
        this.data= data;
    }

    private void printCellCluster (int x, int y)  {
        if(data.getCellCluster(x,y).isFree()) {
            System.out.print("| " + " " + " ");
        }
        else if (data.getCellCluster(x,y).isDomeOnTop()) {
            System.out.print("| " + "O" + " ");
        }
        else{
            CellClusterData xy = data.getCellCluster(x, y);
            int height = xy.getBlocks().length;

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

            if (xy.getWorkerOnTop() != null) {
                WorkerColors color = xy.getWorkerColor();

                switch (color) {
                    case BEIGE:
                        System.out.print("| " + ANSI_YELLOW + "♦︎" + ANSI_BLACK + " ");
                        break;
                    case BLUE:
                        System.out.print("| " + CYAN_BRIGHT + "♦︎" + ANSI_BLACK + " ");
                        break;
                    case WHITE:
                        System.out.print("| " + ANSI_WHITE + "♦︎" + ANSI_BLACK + " ");
                        break;
                }


            }
        }
    }
    public void displayIsland() {
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