package model;

import java.util.ArrayList;
import model.gamemap.Worker;
import model.Card;

public class Player {
    private String username;
    private ArrayList<Worker> workersList = new ArrayList<Worker>();
    private WorkerColors color;
    private Card card;
    protected GameManager match;
    protected BehaviourManager behaviour;

    /**
     * Constructor of the player class
     * @param username the name of the player
     */
    public Player(String username) {
        username= username;
    }

    /**
     *
     * @return the username of the player
     */
    public String getUsername(){
        return username;
    }

    /**
     *
     * @param card the card chosen by the player
     */
    public void setCard(Card card) {
        card = card;
    }

    /**
     *
     * @return the card the player is using
     */
    public Card getCard(){

    }

    /**
     *
     * @param worker the worker to be added in the workersList
     */
    public void setWorker(Worker worker) {
        workersList.add(worker);
    }

    /**
     *
     * @param id the id of the worker to return
     * @return the worker of the id passed by parameter
     */
    public Worker getWorker(Worker.IDs id){
        Worker w;
        for (Worker x : workersList){
            //needed method getWorkerID in class Worker
            if (x.getWorkerID() == id){
                w = x;
            }
        }
        return w;
    }

    /**
     * Set the color of the player
     * @param color the color representative of the player
     */
    public void setColor (WorkerColors color){
        color= color;
    }

    /**
     *
     * @return the color of the workers of the player
     */
    public WorkerColors getColor() {
        WorkerColors colorCopy= color;
        return colorCopy;
    }
}
