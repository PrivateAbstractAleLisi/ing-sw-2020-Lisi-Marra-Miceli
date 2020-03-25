package model.gamemap;


public class Worker {


    enum IDs {
        A, B;
    }

    private IDs workerID;
    private String playerUsername; //

    //in cluster cosa sarebbe??!

    private int posX, posY;

    /**
     *  Costruttore per la classe worker
     * @param workerID id del worker: ogni player ne ha due A e B.
     * @param playerUsername l'username (stringa) del giocatore che possiede questo worker
     */
    public Worker(IDs workerID, String playerUsername) {
        this.workerID = workerID;
        this.playerUsername = playerUsername;
    }

    //1D

    public int getPositionX() {
        return posX;
    }

    public void setPositionX(int posX) {
        this.posX = posX;
    }

    public int getPositionY() {
        return posY;
    }

    public void setPositionY(int posY) {
        this.posY = posY;
    }

    //2D
    public void setPosition (int posX, int posY) {
        setPositionX(posX);
        setPositionY(posY);
    }

    public int[] getPosition () {
        int [] vector2 = new int[2];
        vector2[0] =  posX;
        vector2[1] =  posY;

        return vector2;
    }
}
