package view.CLI.utility;

import model.WorkerColors;
import placeholders.CellClusterData;
import placeholders.IslandData;

import static auxiliary.ANSIColors.*;

public class IslandUtility {

    private IslandData data;
    public IslandUtility(IslandData data) {
        this.data= data;
    }



    private void printCellCluster (int x, int y)  {
        String cellClusterContent = "| ";
        if(data.getCellCluster(x,y).isFree()) {
            //System.out.print("| " + " " + " ");
            cellClusterContent = cellClusterContent + "   ";
        }
        else if (data.getCellCluster(x,y).isDomeOnTop()) {
            //System.out.print("| " + "O" + " ");
            cellClusterContent = cellClusterContent + "O  ";
        }
        else{
            CellClusterData xy = data.getCellCluster(x, y);
            int height = xy.getBlocks().length;

            switch (height) {
                case 1:
                    //System.out.print("| " + "1" + " ");
                    cellClusterContent += "1";
                    break;
                case 2:
                    //System.out.print("| " + "2" + " ");
                    cellClusterContent += "2";
                    break;
                case 3:
                    //System.out.print("| " + "3" + " ");
                    cellClusterContent += "3";
                    break;

            }

            if (xy.getWorkerOnTop() != null) {

                WorkerColors color = xy.getWorkerColor();

                switch (color) {
                    case BEIGE:
                        //System.out.print("| " + ANSI_YELLOW + "♦︎" + ANSI_BLACK + " ");
                        cellClusterContent += ANSI_YELLOW + "♦︎" + ANSI_RESET;
                        break;
                    case BLUE:
                        //System.out.print("| " + CYAN_BRIGHT + "♦︎" + ANSI_BLACK + " ");
                        cellClusterContent += CYAN_BRIGHT + "♦︎" + ANSI_RESET;
                        break;
                    case WHITE:
                        //System.out.print("| " + ANSI_WHITE + "♦︎" + ANSI_BLACK + " ");
                        cellClusterContent += ANSI_WHITE + "♦︎" + ANSI_RESET;
                        break;
                }


            }
            else {
                cellClusterContent += "  ";
            }

        }

        System.out.print(cellClusterContent);
    }
    public void displayIsland() {
        for (int row = 0; row < 5; row++)
        {
            System.out.println("");
            System.out.println("---------------------------------");

            for (int column = 0; column < 5; column++)
            {
                printCellCluster(row, column);
            }
            System.out.print("|");
        }
        System.out.println("");
        System.out.println("---------------------------------");
    }
}
