package model;

import java.util.ArrayList;
import java.util.List;

import model.exception.InvalidWorkerException;
import model.gamemap.Worker;

public class Player {

    private String username;

    private ArrayList<Worker> workers = new ArrayList<Worker>();
    private WorkerColors color;

    private BoardManager boardManager;
    private Card card;
    private BehaviourManager behaviour;

    /**
     * Constructor of the player class
     *
     * @param username the name of the player
     */
    public Player(String username, BoardManager boardManager) {
        this.username = username;
        this.boardManager = boardManager;
        behaviour = new BehaviourManager();
    }

    public Player(String username) {
        this.username = username;
        behaviour = new BehaviourManager();
    }

    /**
     * @return the username of the player
     */
    public String getUsername() {
        return username;
    }

    /**
     * Creates the card from the input string and link it to the player
     *
     * @param cardToSet the card chosen by the player
     */
    public void setCard(String cardToSet) {
        try {
            Class<? extends Card> card = Class.forName("model.cards." + cardToSet).asSubclass(Card.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.card = card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    /**
     * @return the card the player is using
     */
    public Card getCard() {
        return card;
    }

    /**
     * @return the behaviour of the player for the current turn
     */
    public BehaviourManager getBehaviour() {
        return behaviour;
    }

    /**
     * @param worker the worker to be added in the workers
     * @throws InvalidWorkerException if there are already two workers or there is already one with the same ID
     */
    public void setWorker(Worker worker) throws InvalidWorkerException {
        if (workers.size() == 2)
            throw new InvalidWorkerException("Already two workers for this player");
        for (Worker w : workers) {
            if (w.getWorkerID() == worker.getWorkerID())
                throw new InvalidWorkerException("Already a worker with the same ID");
        }
        workers.add(worker);
    }

    /**
     * @param id the id of the worker to return
     * @return the worker of the id passed by parameter
     */
    public Worker getWorker(Worker.IDs id) {
        Worker w = null;
        for (Worker x : workers) {
            if (x.getWorkerID() == id) {
                w = x;
            }
        }
        return w;
    }

    /**
     * Set the color of the player
     *
     * @param color the color representative of the player
     */
    public void setColor(WorkerColors color) {
        this.color = color;
    }

    /**
     * @return the color of the workers of the player
     */
    public WorkerColors getColor() {
        WorkerColors colorCopy = color;
        return colorCopy;
    }

    /**
     * @return the list of the players
     */
    public List<Player> getPlayers() {
        return boardManager.getPlayers();
    }
}
