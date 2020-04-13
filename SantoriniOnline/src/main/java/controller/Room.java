package controller;

import event.core.EventSource;
import event.core.ListenerType;
import event.events.RoomUpdateGameEvent;
import model.BoardManager;
import model.Player;
import model.exception.AlreadyExistingPlayerException;

import javax.naming.LimitExceededException;
import java.util.Map;

public class Room extends EventSource {

    private final int SIZE;
    private int lastOccupiedPosition;
    private String[] activeUsers;
    private BoardManager boardManager;
    private TurnController turnController;
    private PreGameController preGame;


    public Room(int size) {
        this.SIZE = size;
        activeUsers = new String[SIZE];
        lastOccupiedPosition = 0;
        boardManager = new BoardManager();

//        DEBUG
        System.out.println("DEBUG: ROOM: Stanza creata");
    }

    public void addUser(String username) {
        try {
            boardManager.addPlayer(username);
            this.activeUsers[lastOccupiedPosition] = username;
            //        DEBUG
            System.out.println("DEBUG: ROOM: username aggiunto");
            this.lastOccupiedPosition++;
            RoomUpdateGameEvent updateEvent = new RoomUpdateGameEvent("Added a new Player", getActiveUsersCopy(), SIZE);
            notifyAllObserverByType(ListenerType.VIEW, updateEvent);
        } catch (LimitExceededException e) {
            e.printStackTrace();
        } catch (AlreadyExistingPlayerException e) {
            e.printStackTrace();
        }

    }

    public void logAllUsers() {
        System.out.println("IN THIS ROOM: ");
        for (int i = 0; i < activeUsers.length; i++) {
            System.out.println(i + " | " + activeUsers[i]);
        }
    }

    public void beginPreGame() {
        preGame = new PreGameController(boardManager, this);
        preGame.start();
    }

    public void beginGame(Map<Integer, Player> turnSequence) {
        turnController = new TurnController(turnSequence, SIZE);
    }

    public int getSIZE() {
        return SIZE;
    }

    public String[] getActiveUsers() {
        return activeUsers;
    }

    public String[] getActiveUsersCopy() {
        return activeUsers.clone();
    }

    public int getLastOccupiedPosition() {
        return lastOccupiedPosition;
    }
}
