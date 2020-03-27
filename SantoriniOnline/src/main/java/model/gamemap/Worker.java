package model.gamemap;


public class Worker {


    public enum IDs {
        A, B;
    }

    private IDs workerID;
    private String playerUsername; //

    //in cluster cosa sarebbe??!

    private int[] position;

    /**
     *  Costruttore per la classe worker
     * @param workerID id del worker: ogni player ne ha due A e B.
     * @param playerUsername l'username (stringa) del giocatore che possiede questo worker
     */
    public Worker(IDs workerID, String playerUsername) {
        this.workerID = workerID;
        this.playerUsername = playerUsername;
        position = new int [2];
    }
//Changed: vettore posizione unico, no getter x, getter y

    //2D
    public void setPosition (int posX, int posY) {
        position[0] = posX;
        position[1] = posY;
    }

    public int[] getPosition () {
        int [] vector2 = new int[2];
        vector2[0] =  position[0];
        vector2[1] =  position[1];

        return vector2;
    }
}
