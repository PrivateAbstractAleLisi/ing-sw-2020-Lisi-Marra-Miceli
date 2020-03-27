package model.gamemap;

import model.exception.InvalidBuildException;
import model.exception.InvalidMovementException;

public class Island {
//todo BLOCCARE COSTRURIONE E MOVIMENTO QUANDO LA CELLA Eà COMPLETED (DOME ON TOP), BLOCCARE QUANDO PROVI A COSTRUIRE SOPRA LA TESTA DI QUALCUNO
//
//

    private CellCluster[][] grid;
    /*Game Manager TODO*/ private Object match;

    /**
     * Constructor, initialize all the island's cells
     */

    public Island() {
        grid = new CellCluster[5][5];

        //initialize all cells with a double for
        for (int i=0; i<5; i++) {
            for (int j=0; j<5; j++) {
                grid [i][j] = new CellCluster();
            }
        }
        System.out.println("DEBUG: Island has been initialized");
    }

    /**
     *
     * @param i an integer that represents an offset (x or y axis) in the island grid
     * @return true if the offset is inside the island, false if it's outside (Invalid).
     */
    private boolean inRange(int i) {
        if (i>= 0 && i<5) {
            return true;
        }
        else {
            return false;
        }
    }

    public void placeWorker(Worker w, int x, int y) {
        try {
            grid[x][y].addWorker(w);
            w.setPosition(x, y); //RIPETERE CODICE§!§!§!
        } catch (InvalidMovementException e) {
            System.err.println(e.toString());
        }

        System.out.println("DEBUG: Island: worker placed at index " + x +","+ y );
    }


    public void moveWorker(Worker w, int x, int y) {

        int oldX = w.getPosition()[0];
        int oldY = w.getPosition()[1];
        grid[oldX][oldY].removeWorker(); //TODO gestire (forse conviene internamente) il caso dove non posso rimuovere nessuno
        try {
            grid[x][y].addWorker(w);
            w.setPosition(x, y); //RIPETERE CODICE
        } catch (InvalidMovementException e) {
            System.err.println(e.toString());
        }

        System.out.println("DEBUG: Island: worker successfully moved from position " + oldX +","+ oldY +  "to position " +
                 + x + "," + y );

    }

    public void buildBlock(BlockTypeEnum b, int x, int y) {

        try {
            grid[x][y].build(b);
        } catch (InvalidBuildException e) {
            System.err.println("TEST FALLITO");
        }

        System.out.println("DEBUG: Island: block build on position" + x + "," + y + " block type: " + b.toString());
    }

    public CellCluster getClusterAtPosition(int x, int y) {
        return grid[x][y];
    }

}
