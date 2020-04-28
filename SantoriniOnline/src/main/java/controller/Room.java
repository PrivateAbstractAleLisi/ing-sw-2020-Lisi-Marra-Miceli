package controller;

import event.PlayerDisconnectedGameEvent;
import event.core.EventSource;
import event.core.ListenerType;
import event.gameEvents.lobby.CV_RoomUpdateGameEvent;
import event.gameEvents.match.CV_GameStartedGameEvent;
import model.BoardManager;
import model.Player;
import exceptions.AlreadyExistingPlayerException;
import view.VirtualView;

import javax.naming.LimitExceededException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Room extends EventSource {

    private final int SIZE;
    private int lastOccupiedPosition;
    private List<String> activeUsers;
    private Map<String, VirtualView> virtualViewMap;
    private BoardManager boardManager;
    private TurnController turnController;
    private Map<Integer, Player> turnSequence;
    private boolean gameCanStart;
    private String roomID;

    //    todo AFTER DEBUG: make private
    private PreGameController preGame;
//    private PreGameController preGame;

    public Room(int size, String roomID) {
        this.SIZE = size;
        activeUsers = new ArrayList<String>();
        lastOccupiedPosition = activeUsers.size();
        boardManager = new BoardManager();
        virtualViewMap = new HashMap<>(SIZE);
        gameCanStart = false;
        this.turnSequence = new HashMap<Integer, Player>();
        this.roomID = roomID;

        printLogMessage("Room created");
    }

    public void disconnectAllUsers(String causedByUsername) {
        PlayerDisconnectedGameEvent disconnectedGameEvent = new PlayerDisconnectedGameEvent("an user has disconnected", causedByUsername,
                causedByUsername + " has lost connection to the server ");

        notifyAllObserverByType(ListenerType.VIEW, disconnectedGameEvent);

        activeUsers = null;
        virtualViewMap = null;
    }

    public void addUser(String username, VirtualView virtualView) {
        try {

            boardManager.addPlayer(username);

            this.activeUsers.add(username);
            this.virtualViewMap.put(username, virtualView);
            attachListenerByType(ListenerType.VIEW, virtualView);
            this.lastOccupiedPosition = activeUsers.size();

            //        DEBUG
            printLogMessage("New player " + username.toUpperCase() + " added in the room");

            CV_RoomUpdateGameEvent updateEvent = new CV_RoomUpdateGameEvent("Added a new Player", getActiveUsersCopy(), SIZE);
            notifyAllObserverByType(ListenerType.VIEW, updateEvent);

            /*if (lastOccupiedPosition == SIZE) {  //when room is filled.
                //beginPreGame();
                throw new Exception("ROOMREADY");
            } */
        } catch (LimitExceededException e) {
            e.printStackTrace();
        } catch (AlreadyExistingPlayerException e) {
            printLogMessage("ERROR: The username is already in BoardManager");
            e.printStackTrace();
        }
    }

    public void logAllUsers() {
        System.out.println("IN THIS ROOM: ");
        for (int i = 0; i < activeUsers.size(); i++) {
            System.out.println(i + " | " + activeUsers.get(i));
        }
    }

    public boolean isFull() {
        return (lastOccupiedPosition == SIZE);
    }

    public void beginPreGame() {

        //1! controller for this pre match/game
        preGame = new PreGameController(boardManager, this);

        //virtualView added to listener
        for (int i = 0; i < SIZE; i++) {
            String tempUser = activeUsers.get(i);
            VirtualView tempVirtualView = virtualViewMap.get(tempUser);
            tempVirtualView.attachListenerByType(ListenerType.VIEW, preGame);
            preGame.attachListenerByType(ListenerType.VIEW, tempVirtualView);
        }
        startPreGame();
    }

    private void startPreGame() {
        preGame.start();
    }

    //    public void beginGame(Map<Integer, Player> turnSequence) {
//        turnController = new TurnController (boardManager, turnSequence, SIZE);
//    }
    public void beginGame() {
        turnController = new TurnController(boardManager, this.turnSequence, SIZE, this);

        for (int i = 0; i < SIZE; i++) {
            String tempUser = activeUsers.get(i);
            VirtualView tempVirtualView = virtualViewMap.get(tempUser);
            tempVirtualView.attachListenerByType(ListenerType.VIEW, turnController);
            turnController.attachListenerByType(ListenerType.VIEW, tempVirtualView);
        }
        CV_GameStartedGameEvent event = new CV_GameStartedGameEvent("", turnSequence.get(0).getUsername());
        notifyAllObserverByType(ListenerType.VIEW, event);
        turnController.firstTurn();

    }

    public int getSIZE() {
        return SIZE;
    }

    public List<String> getActiveUsers() {
        return activeUsers;
    }


    public String[] getActiveUsersCopy() {
        String[] result = new String[activeUsers.size()];
        for (int i = 0; i < activeUsers.size(); i++) {
            result[i] = activeUsers.get(i);
        }
        return result;
    }

    public int getLastOccupiedPosition() {
        return lastOccupiedPosition;
    }

    public boolean isGameCanStart() {
        return gameCanStart;
    }

    public void setGameCanStart(boolean gameCanStart) {
        this.gameCanStart = gameCanStart;
    }

    public void setTurnSequence(Map<Integer, Player> turnSequence) {
        this.turnSequence = turnSequence;
    }

    public String getRoomID() {
        return roomID;
    }

    /**
     * Print in the Server console a Log from the current Class
     *
     * @param messageToPrint a {@link String} with the message to print
     */
    private void printLogMessage(String messageToPrint) {
        System.out.println("\t \tROOM(" + this.roomID + "): " + messageToPrint);
    }

    /**
     * Print in the Server console Error Stream an Errror Log from the current Class
     *
     * @param messageToPrint a {@link String} with the message to print
     */
    private void printErrorLogMessage(String messageToPrint) {
        System.err.println("\t \tROOM(" + roomID + "): " + messageToPrint);
    }
}
