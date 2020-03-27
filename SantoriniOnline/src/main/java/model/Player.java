package model;

import java.util.ArrayList;
import java.util.List;
import model.gamemap.Worker;

public class Player {
    private String username;
    private ArrayList<Worker> workersList = new ArrayList<Worker>();
    private WorkerColors color;
    private Object card;

    public Player(String username) {
        this.username=username;
    }

    public void setCard(Object card) {
        this.card = card;
    }

    public void setWorker(Worker w) {
        workersList.add(w);
    }

    public void setColor (WorkerColors color){
        this.color=color;
    }
}
