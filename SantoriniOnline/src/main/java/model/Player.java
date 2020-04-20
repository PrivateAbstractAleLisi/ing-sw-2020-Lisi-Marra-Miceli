package model;

import model.Gods.*;
import model.gamemap.Worker;

import java.util.ArrayList;
import java.util.List;

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
        Worker worker1 = new Worker(Worker.IDs.A, username);
        workers.add(worker1);
        Worker worker2 = new Worker(Worker.IDs.B, username);
        workers.add(worker2);
    }

    public Player(String username) {
        this.username = username;
        behaviour = new BehaviourManager();
    }

    /**
     * @param boardManager the boardManager the player is connected to
     */
    public void setBoardManager(BoardManager boardManager) {
        this.boardManager = boardManager;
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
    public void setCard(CardEnum cardToSet) {
        switch (cardToSet) {
            case APOLLO:
                this.card = new Apollo(this);
                break;
            case ARTEMIS:
                this.card = new Artemis(this);
                break;
            case ATHENA:
                this.card = new Athena(this);
                break;
            case ATLAS:
                this.card = new Atlas(this);
                break;
            case DEMETER:
                this.card = new Demeter(this);
                break;
            case HEPHAESTUS:
                this.card = new Hephaestus(this);
                break;
            case MINOTAUR:
                this.card = new Minotaur(this);
                break;
            case PAN:
                this.card = new Pan(this);
                break;
            case PROMETHEUS:
                this.card = new Prometheus(this);
                break;
            default:
                throw new IllegalArgumentException("String doesn't match with any God");
        }
        this.card.resetBehaviour();
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

    /*
    /**
     * @param worker the worker to be added in the workers
     * @throws InvalidWorkerException if there are already two workers or there is already one with the same ID
     */
    /*public void setWorker(Worker worker) throws InvalidWorkerException {
        if (workers.size() == 2)
            throw new InvalidWorkerException("Already two workers for this player");
        for (Worker w : workers) {
            if (w.getWorkerID() == worker.getWorkerID())
                throw new InvalidWorkerException("Already a worker with the same ID");
        }
        workers.add(worker);
    }

     */


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
     * @param id the worker id of the worker to delete from the worker list of this player
     */
    public void removeWorker(Worker.IDs id) {
        Worker worker = null;
        for (Worker w : workers) {
            if (w.getWorkerID() == id) {
                worker = w;
            }
        }
        workers.remove(worker);
    }

    /**
     * Set the color of the player
     *
     * @param color the color representative of the player
     */
    public void setColor(WorkerColors color) {
        this.color = color;
        for (Worker worker : workers) {
            worker.setColor(this.color);
        }
    }

    /**
     * @return the color of the workers of the player
     */
    public WorkerColors getColor() {
        return color;
    }

    /**
     * @return the list of the players
     */
    public List<Player> getPlayers() {
        return boardManager.getPlayers();
    }


    public BoardManager getBoardManager() {
        return boardManager;
    }
}
