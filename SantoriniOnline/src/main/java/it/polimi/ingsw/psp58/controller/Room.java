package it.polimi.ingsw.psp58.controller;

import it.polimi.ingsw.psp58.event.PlayerDisconnectedGameEvent;
import it.polimi.ingsw.psp58.event.core.EventSource;
import it.polimi.ingsw.psp58.event.core.ListenerType;
import it.polimi.ingsw.psp58.event.gameEvents.lobby.CV_RoomUpdateGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.CV_GameStartedGameEvent;
import it.polimi.ingsw.psp58.model.BoardManager;
import it.polimi.ingsw.psp58.model.Player;
import it.polimi.ingsw.psp58.exceptions.AlreadyExistingPlayerException;
import it.polimi.ingsw.psp58.view.VirtualView;

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
    private String spectator;
    private boolean roomMustBeCleaned;

    private PreGameController preGame;

    public Room(int size, String roomID) {
        this.SIZE = size;
        activeUsers = new ArrayList<String>(SIZE);
        lastOccupiedPosition = activeUsers.size();
        boardManager = new BoardManager();
        virtualViewMap = new HashMap<>(SIZE);
        gameCanStart = false;
        this.turnSequence = new HashMap<Integer, Player>();
        this.roomID = roomID;
        this.roomMustBeCleaned=false;

        printLogMessage("Room created");
    }

    public void disconnectAllUsers(String causedByUsername) {
        PlayerDisconnectedGameEvent disconnectedGameEvent = new PlayerDisconnectedGameEvent("an user has disconnected", causedByUsername,
                causedByUsername + " has lost connection to the server ");

        preGame = null;
        turnController = null;

        notifyAllObserverByType(ListenerType.VIEW, disconnectedGameEvent);

        activeUsers = null;
        virtualViewMap = null;
    }

    public void disconnectUser(String username) {
        activeUsers.remove(username);

        if (spectator.equals(username)) {
            spectator = null;
        }
        VirtualView virtualView = virtualViewMap.get(username);
        virtualView.detachListenerByType(ListenerType.VIEW, turnController);
        turnController.detachListenerByType(ListenerType.VIEW, virtualView);

        virtualViewMap.remove(username);
    }

    public void addUser(String username, VirtualView virtualView) {
        try {
            boolean canAdd = !(boardManager.getPlayers().size() + 1 > SIZE) &&
                    activeUsers.size() < SIZE && activeUsers.size() + 1 <= SIZE;
            if (!canAdd) {
                throw new LimitExceededException();
            }

            boardManager.addPlayer(username);

            this.activeUsers.add(username);
            this.virtualViewMap.put(username, virtualView);
            attachListenerByType(ListenerType.VIEW, virtualView);
            this.lastOccupiedPosition = activeUsers.size();

            //        DEBUG
            printLogMessage("New player " + username.toUpperCase() + " added in the room");

            CV_RoomUpdateGameEvent updateEvent = new CV_RoomUpdateGameEvent("Added a new Player", getActiveUsersCopy(), SIZE);
            notifyAllObserverByType(ListenerType.VIEW, updateEvent);


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

        //1! it.polimi.ingsw.sp58.controller for this pre match/game
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

    public void beginGame() {

        List<VirtualView> virtualViewList = getVirtualViewList();
        for (VirtualView virtualView : virtualViewList) {
            preGame.detachListenerByType(ListenerType.VIEW, virtualView);
            virtualView.detachListenerByType(ListenerType.VIEW, preGame);
        }
        preGame = null;

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

    public void setSpectator(String spectator) {
        this.spectator = spectator;
    }

    public String getSpectator() {
        return spectator;
    }

    public List<VirtualView> getVirtualViewList() {
        List<VirtualView> virtualViewList = new ArrayList<>();
        for (String player : activeUsers) {
            virtualViewList.add(virtualViewMap.get(player));
        }
        return virtualViewList;
    }

    public void cleanRoom() {

        List<VirtualView> virtualViewList = getVirtualViewList();

        for (VirtualView virtualView : virtualViewList) {
            turnController.detachListenerByType(ListenerType.VIEW, virtualView);
            virtualView.detachListenerByType(ListenerType.VIEW, turnController);
            this.detachListenerByType(ListenerType.VIEW,virtualView);
        }

        Lobby.instance().removeRoom(this);
        Lobby.instance().updateRoomCounter();
    }

    public void setRoomMustBeCleaned(boolean roomMustBeCleaned) {
        this.roomMustBeCleaned = roomMustBeCleaned;
    }

    public boolean roomMustBeCleaned() {
        return roomMustBeCleaned;
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
