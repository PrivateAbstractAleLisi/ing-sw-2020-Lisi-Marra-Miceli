package controller;

import model.BoardManager;
import model.Player;
import model.exception.AlreadyExistingPlayerException;

import javax.naming.LimitExceededException;
import java.util.Map;

public class Room {

    private final int SIZE;
    private static int lastOccupiedPosition;
    private static String[] activeUsers;
    private BoardManager boardManager;
    private TurnController turnController;
    private PreGame preGame;


    public Room(int size) {
        this.SIZE = size;
        activeUsers = new String[SIZE];
        lastOccupiedPosition = 0;
        boardManager = new BoardManager();
    }

    public void addUser(String username) {
        try {
            boardManager.addPlayer(username);
            Room.activeUsers[lastOccupiedPosition] = username;
            Room.lastOccupiedPosition++;
        } catch (LimitExceededException e) {
            //todo aggiungere generazione eventi di errore
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
        preGame = new PreGame();
        preGame.start();
    }

    public void beginGame(Map<Integer, Player> turnSequence) {
        turnController = new TurnController(turnSequence, SIZE);
    }

    public int getSIZE() {
        return SIZE;
    }

    public static String[] getActiveUsers() {
        return activeUsers;
    }
}
