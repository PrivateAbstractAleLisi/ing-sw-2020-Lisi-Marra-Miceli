package model;

import java.util.List;

public class Player {
    private String username;
    private List<Worker> workersList = new List<Worker>;
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
