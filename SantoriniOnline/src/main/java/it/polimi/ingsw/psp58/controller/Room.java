package it.polimi.ingsw.psp58.controller;

import it.polimi.ingsw.psp58.event.core.EventSource;
import it.polimi.ingsw.psp58.event.gameEvents.connection.PlayerDisconnectedViewEvent;
import it.polimi.ingsw.psp58.event.gameEvents.lobby.CV_RoomUpdateGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.gamephase.CV_GameStartedGameEvent;
import it.polimi.ingsw.psp58.exceptions.AlreadyExistingPlayerException;
import it.polimi.ingsw.psp58.model.BoardManager;
import it.polimi.ingsw.psp58.model.Player;
import it.polimi.ingsw.psp58.view.VirtualView;

import javax.naming.LimitExceededException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.psp58.event.core.ListenerType.CONTROLLER;
import static it.polimi.ingsw.psp58.event.core.ListenerType.VIEW;

/**
 * This class contains the Player and handle the game during the change of the phases.
 */
public class Room extends EventSource {

    /**
     * Size of the room. Defined during the creation.
     */
    private final int SIZE;
    /**
     * Number of current players
     */
    private int lastOccupiedPosition;
    /**
     * List of the username of the current players in the room.
     */
    private List<String> activeUsers;
    /**
     * Map to connect the username to the VirtualView of each user.
     */
    private Map<String, VirtualView> virtualViewMap;
    /**
     * BoardManager of this match.
     */
    private final BoardManager boardManager;
    /**
     * PreGameController of this match.
     */
    private PreGameController preGame;
    /**
     * Turn controller of this match.
     */
    private TurnController turnController;
    /**
     * The turn sequence used in the match. It contains the players in the right order.
     */
    private Map<Integer, Player> turnSequence;
    /**
     * Boolean value, become true when the workers have been placed and the Game can start.
     */
    private boolean gameCanStart;
    /**
     * Name or ID of this room.
     */
    private final String roomID;
    /**
     * Name of the player that has lost the game.
     */
    private String spectator;
    /**
     * Boolean value, become true after the end of the match and the room must be cleaned.
     */
    private boolean roomMustBeCleaned;

    /**
     * Create a new Room with a specific Size and an ID.
     * @param size Size of the room: only
     * @param roomID The ID or the name of the room.
     */
    public Room(int size, String roomID) {
        if (size!=2 && size != 3){
            throw new IllegalArgumentException();
        }
        this.SIZE = size;
        activeUsers = new ArrayList<>(SIZE);
        lastOccupiedPosition = activeUsers.size();
        boardManager = new BoardManager();
        virtualViewMap = new HashMap<>(SIZE);
        gameCanStart = false;
        this.turnSequence = new HashMap<>();
        this.roomID = roomID;
        this.roomMustBeCleaned = false;

        printLogMessage("Room created");
    }

    /* ----------------------------------------------------------------------------------------------
                                         USERS METHODS
       ----------------------------------------------------------------------------------------------*/

    /**
     * Add an user to the room and to the BoardManager.
     * @param username Name of the user to add.
     * @param virtualView Virtual view of the user.
     */
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
            attachListenerByType(VIEW, virtualView);
            this.lastOccupiedPosition = activeUsers.size();

            //        DEBUG
            printLogMessage("New player " + username.toUpperCase() + " added in the room");

            CV_RoomUpdateGameEvent updateEvent = new CV_RoomUpdateGameEvent("Added a new Player", getActiveUsersCopy(), SIZE, roomID);
            notifyAllObserverByType(VIEW, updateEvent);


        } catch (LimitExceededException e) {
            e.printStackTrace();
        } catch (AlreadyExistingPlayerException e) {
            printLogMessage("ERROR: The username is already in BoardManager");
            e.printStackTrace();
        }
    }

    /**
     * Print the name of the all Users in this room.
     */
    public void logAllUsers() {
        System.out.println("IN THIS ROOM: ");
        for (int i = 0; i < activeUsers.size(); i++) {
            System.out.println(i + " | " + activeUsers.get(i));
        }
    }

    /**
     * Check if the number of the users in the room is the maximum for this room.
     * @return True if the room is Full, false if not.
     */
    public boolean isFull() {
        return (lastOccupiedPosition == SIZE);
    }

    /**
     * Set the username of the spectator: someone that has lost the game and can just wait another match.
     * @param spectator Name of the loser.
     */
    protected void setSpectator(String spectator) {
        this.spectator = spectator;
    }

    /**
     * Get the name of the loser.
     * @return String with the name of the spectator.
     */
    public String getSpectator() {
        return spectator;
    }

    /* ----------------------------------------------------------------------------------------------
                                         ROOM CLEAN METHODS
       ----------------------------------------------------------------------------------------------*/

    /**
     * Delete everything in this room and delete the connections with the virtual views.
     */
    public void cleanRoom() {

        List<VirtualView> virtualViewList = getVirtualViewList();

        for (VirtualView virtualView : virtualViewList) {
            turnController.detachListenerByType(VIEW, virtualView);
            virtualView.detachListenerByType(CONTROLLER, turnController);
            this.detachListenerByType(VIEW, virtualView);
        }

        Lobby.instance().removeRoom(this);
        Lobby.instance().updateRoomCounter();
    }

    /**
     * Set to TRUE the boolean value for the cleaning of the room after the end of the match
     */
    protected void setRoomMustBeCleanedTrue() {
        this.roomMustBeCleaned = true;
    }

    /**
     * Check if the room has to be cleaned.
     * @return True if the match is ended and this room must be delete.
     */
    public boolean roomMustBeCleaned() {
        return roomMustBeCleaned;
    }

    /* ----------------------------------------------------------------------------------------------
                                         PRE GAME START METHODS
       ----------------------------------------------------------------------------------------------*/

    /**
     * Create the Pregame .
     */
    public void beginPreGame() {

        //1! controller for this pre match/game
        preGame = new PreGameController(boardManager, this);

        //virtualView added to listener
        for (int i = 0; i < SIZE; i++) {
            String tempUser = activeUsers.get(i);
            VirtualView tempVirtualView = virtualViewMap.get(tempUser);
            tempVirtualView.attachListenerByType(CONTROLLER, preGame);
            preGame.attachListenerByType(VIEW, tempVirtualView);
        }
        startPreGame();
    }

    /**
     * Start the PreGame selecting the Challenger
     */
    private void startPreGame() {
        preGame.start();
    }

     /* ----------------------------------------------------------------------------------------------
                                         GAME START METHODS
       ----------------------------------------------------------------------------------------------*/

    /**
     * Start the Game with the first player.
     */
    public void beginGame() {

        List<VirtualView> virtualViewList = getVirtualViewList();
        for (VirtualView virtualView : virtualViewList) {
            preGame.detachListenerByType(VIEW, virtualView);
            virtualView.detachListenerByType(CONTROLLER, preGame);
        }
        preGame = null;

        turnController = new TurnController(boardManager, this.turnSequence, SIZE, this);

        for (int i = 0; i < SIZE; i++) {
            String tempUser = activeUsers.get(i);
            VirtualView tempVirtualView = virtualViewMap.get(tempUser);
            tempVirtualView.attachListenerByType(CONTROLLER, turnController);
            turnController.attachListenerByType(VIEW, tempVirtualView);
        }
        CV_GameStartedGameEvent event = new CV_GameStartedGameEvent("", turnSequence.get(0).getUsername());
        notifyAllObserverByType(VIEW, event);
        turnController.firstTurn();
    }

    /**
     * Check if the game can start or not.
     * @return True if the came can start.
     */
    public boolean canGameStart() {
        return gameCanStart;
    }

    /**
     * Set to TRUE the boolean value for begin of the match
     */
    protected void setGameCanStartTrue() {
        this.gameCanStart = true;
    }

    /**
     * Set the TurnSequence for the TurnController and all the game.
     * @param turnSequence TurnSequence to set.
     */
    public void setTurnSequence(Map<Integer, Player> turnSequence) {
        this.turnSequence = turnSequence;
    }

    /* ----------------------------------------------------------------------------------------------
                                         DISCONNECTION METHODS
       ----------------------------------------------------------------------------------------------*/

    /**
     * Disconnect all the user in the room and delete everything in this room.
     * @param causedByUsername Name of the player that caused the disconnection.
     */
    protected void disconnectAllUsers(String causedByUsername) {
        PlayerDisconnectedViewEvent disconnectedGameEvent = new PlayerDisconnectedViewEvent("an user has disconnected", causedByUsername,
                causedByUsername + " has lost connection to the server ");

        preGame = null;
        turnController = null;

        notifyAllObserverByType(VIEW, disconnectedGameEvent);

        activeUsers = null;
        virtualViewMap = null;
    }

    /**
     * Disconnect a single user (usually the spectator) and clean his virtual view connection.
     * @param username Username of the player to remove.
     */
    protected void disconnectUser(String username) {
        activeUsers.remove(username);

        if (spectator.equals(username)) {
            spectator = null;
        }
        VirtualView virtualView = virtualViewMap.get(username);
        virtualView.detachListenerByType(CONTROLLER, turnController);
        turnController.detachListenerByType(VIEW, virtualView);

        virtualViewMap.remove(username);
    }

    /* ----------------------------------------------------------------------------------------------
                                         UTILITY METHODS
       ----------------------------------------------------------------------------------------------*/

    /**
     * Get size of the room
     * @return The Max size of the room.
     */
    public int getSIZE() {
        return SIZE;
    }

    /**
     * Get the List of the active users.
     * @return List of active users
     */
    public List<String> getActiveUsers() {
        return activeUsers;
    }

    /**
     * Get the String Array copy of the active users.
     * @return String Array copy of active users
     */
    public String[] getActiveUsersCopy() {
        String[] result = new String[activeUsers.size()];
        for (int i = 0; i < activeUsers.size(); i++) {
            result[i] = activeUsers.get(i);
        }
        return result;
    }

    /**
     * Get the index of the last occupied position
     * @return index of the last occupied position
     */
    public int getLastOccupiedPosition() {
        return lastOccupiedPosition;
    }

    /**
     * Get the List of the virtual view of the players.
     * @return List of player's virtual views
     */
    public List<VirtualView> getVirtualViewList() {
        List<VirtualView> virtualViewList = new ArrayList<>();
        for (String player : activeUsers) {
            virtualViewList.add(virtualViewMap.get(player));
        }
        return virtualViewList;
    }

    /**
     * Get the room id
     * @return Name or ID of the room
     */
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

}
