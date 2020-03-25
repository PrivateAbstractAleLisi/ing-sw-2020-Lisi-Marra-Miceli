package model;

import java.util.Arrays;

public class Worker {
    private int workerID;
    private boolean inCluster;
    private int[] position;

    public Worker(int workerID) {
        workerID= workerID;
        inCluster=false;
        position= new int[2];
    }

    public int[] getPosition() {
        int[] positionCopy = new int[2];
        Arrays.copyOf(position, positionCopy);
        return positionCopy;
    }

    public void updatePosition(int x, int y) {
        position[0]=x;
        position[1]=y;
        inCluster=true;
    }
}
