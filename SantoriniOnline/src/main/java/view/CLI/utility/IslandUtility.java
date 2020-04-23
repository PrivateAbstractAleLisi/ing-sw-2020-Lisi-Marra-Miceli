package view.CLI.utility;

import model.WorkerColors;
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
        String cellClusterContent = "| ";
        if(data.getCellCluster(x,y).isFree()) {
            //System.out.print("| " + " " + " ");
            cellClusterContent = cellClusterContent + "     "; //EMPTY : + 5 CHARS
        }
        else if (data.getCellCluster(x,y).isDomeOnTop()) {
            //System.out.print("| " + "O" + " ");
            cellClusterContent = cellClusterContent + "  ◉  "; //DOME   +5 CHARS (4 EMPTY)
        }
        else{
            CellClusterData xy = data.getCellCluster(x, y);
            int height = xy.getBlocks().length;


            switch (height) {
                case 1:
                    //System.out.print("| " + "1" + " ");
                    cellClusterContent += " 1 ";
                    break;
                case 2:
                    //System.out.print("| " + "2" + " ");
                    cellClusterContent += " 2 ";
                    break;
                case 3:
                    //System.out.print("| " + "3" + " ");
                    cellClusterContent += " 3 ";
                    break;
                default:
                    cellClusterContent += "   ";
                    break;
            }

            if (xy.getWorkerOnTop() != null) {

                WorkerColors color = xy.getWorkerColor();
                Character workerLetter = xy.getWorkerOnTop().equals(Worker.IDs.A) ? 'A' : 'B';
                switch (color) {
                    case BEIGE:
                        cellClusterContent += ANSI_YELLOW + workerLetter + " " + ANSI_RESET;
                        break;
                    case BLUE:
                        cellClusterContent += CYAN_BRIGHT + workerLetter + " " + ANSI_RESET;
                        break;
                    case WHITE:
                        cellClusterContent += ANSI_WHITE + workerLetter + " " + ANSI_RESET;
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


        System.out.print("COL →  ");


        for (int c = 0; c<5; c++) {
            System.out.print("   " + (c+1) + "   ");
        }

        for (int row = 0; row < 5; row++)
        {
            System.out.println("");

            if (row != 0) {
                System.out.println("        -----------------------------------");
            }
            else{
                System.out.println("ROW ↓   -----------------------------------");
            }

            System.out.print("    " + (row+1) +  "  ");


            for (int column = 0; column < 5; column++)
            {
                printCellCluster(row, column);
            }
            System.out.print("|");

        }
        System.out.println("");
        System.out.println("        -----------------------------------");
    }
}
