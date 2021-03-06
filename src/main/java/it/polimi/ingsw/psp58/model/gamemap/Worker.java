package it.polimi.ingsw.psp58.model.gamemap;


import it.polimi.ingsw.psp58.model.WorkerColors;

/**
 * Worker class with info about the owner
 */
public class Worker {

    /**
     * Worker ID (A or B)
     */
    public enum IDs {
        A, B;
    }

    private final IDs workerID;
    private final String playerUsername;
    private boolean isPlacedOnIsland;
    private WorkerColors color;

    private int[] position;

    /**
     *  Worker constructor
     * @param workerID id del worker: ogni player ne ha due A e B.
     * @param playerUsername l'username (stringa) del giocatore che possiede questo worker
     */
    public Worker(IDs workerID, String playerUsername) {
        this.workerID = workerID;
        this.playerUsername = playerUsername;
        position = null;
        isPlacedOnIsland = false;

    }

    /**
     * sets worker color
     * @param color the color of the worker
     */
    public void setColor(WorkerColors color) {
        this.color = color;
    }

    public WorkerColors getColor() {
        return color;
    }

    //Changed: vettore posizione unico, no getter x, getter y

    //2D

    /**
     * @param posX x position
     * @param posY y position
     */
    public void setPosition (int posX, int posY) {
        position = new int[2];
        position[0] = posX;
        position[1] = posY;
    }
    /**
     * @return a 2D vector [X component, Y component], null if worker isn't  placed already
     */
    public int[] getPosition () {

        if (position == null) {
            return null;
        }
        else {
            int[] vector2 = new int[2];
            vector2[0] = position[0];
            vector2[1] = position[1];
            return vector2;
        }


    }

    /**
     *
     * @return returns worker ID
     */
    public IDs getWorkerID(){
        return this.workerID;
    }

    public String getPlayerUsername() {
        return playerUsername;
    }

    public boolean isPlacedOnIsland() {
        return isPlacedOnIsland;
    }

    public void setPlacedOnIsland(boolean placedOnIsland) {
        isPlacedOnIsland = placedOnIsland;
    }
}
