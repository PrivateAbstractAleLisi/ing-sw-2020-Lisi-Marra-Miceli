package it.polimi.ingsw.psp58.controller;

import it.polimi.ingsw.psp58.event.PlayerDisconnectedGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.lobby.CV_NewGameRequestEvent;
import it.polimi.ingsw.psp58.event.gameEvents.lobby.VC_NewGameResponseEvent;
import it.polimi.ingsw.psp58.exceptions.NotFreeRoomAvailableException;
import it.polimi.ingsw.psp58.event.core.EventListener;
import it.polimi.ingsw.psp58.event.core.EventSource;
import it.polimi.ingsw.psp58.event.core.ListenerType;
import it.polimi.ingsw.psp58.event.gameEvents.CV_GameErrorGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.CV_WaitPreMatchGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.GameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.lobby.*;
import it.polimi.ingsw.psp58.event.gameEvents.match.*;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.*;
import it.polimi.ingsw.psp58.exceptions.RoomNotFoundException;
import it.polimi.ingsw.psp58.exceptions.UserNotFoundException;
import it.polimi.ingsw.psp58.view.VirtualView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Handle the connections to the game, create the Rooms and start PreGame and Game, implements the Singleton Pattern.
 */
public class Lobby extends EventSource implements EventListener {

    /* ----------------------------------------------------------------------------------------------
                                         SINGLETON IMPLEMENTATION
       ----------------------------------------------------------------------------------------------*/

    /**
     * Single instance of {@code Lobby}
     */
    private static Lobby instance = null;

    /**
     * Private constructor: only called by {@code createInstance method}
     */
    private Lobby() {
        canCreateNewRoom = new AtomicBoolean();
        canCreateNewRoom.set(true);
        activeRooms = new ArrayList<Room>();
        activeUsersList = new ArrayList<String>();
        roomCounter = 0;
    }

    /**
     * Private method: it creates the object if it doesn't exist.
     *
     * @return Return the Instance of Lobby
     */
    private synchronized static Lobby createInstance() {
        instance = new Lobby();
        return instance;
    }

    /**
     * Public method that return the unique instance of Lobby.
     * It calls the synchronized and private method only if the object doesn't exist.
     * If the object is already created, the method only return the instance of the object.
     *
     * @return Return the Instance of Lobby
     */
    public static Lobby instance() {
        if (instance == null) createInstance();
        return instance;
    }


    /* ----------------------------------------------------------------------------------------------
                                         FIELDS DECLARATIONS
       ----------------------------------------------------------------------------------------------*/

    /**
     * List of the currently active Rooms in this Server
     */
    private List<Room> activeRooms;

    /**
     * List of the currently active users in this Server, stored by their Username ({@link String})
     */
    private List<String> activeUsersList;

    /**
     * {@link AtomicBoolean} variable that help to synchronize the creation of a Room.
     */
    private AtomicBoolean canCreateNewRoom;

    /**
     * Username of the player that is creating the Room, stored until the {@link VC_RoomSizeResponseGameEvent} event arrives
     */
    private String pendingUsername;

    /**
     * {@link VirtualView} of the player that is creating the Room, stored until the {@link VC_RoomSizeResponseGameEvent} event arrives
     */
    private VirtualView pendingVirtualView;

    /**
     * Number of active Rooms, for LogMessage purpose
     */
    private int roomCounter;

    /**
     * Lock the creation of the Room and the access to {@code canCreateNewRoom}.
     */
    private ReentrantLock creatingRoomLock = new ReentrantLock();

    /**
     * Number of maximum Rooms in this server
     */
    private static final int MAX_ROOMS = 16;


    /* ----------------------------------------------------------------------------------------------
                                  SYNCHRONIZED AND PUBLIC METHODS IMPLEMENTATION
       ----------------------------------------------------------------------------------------------*/

    /**
     * This method attach an {@link EventListener} Object to this Lobby.
     *
     * @param type     the category in which the Listener will be registered.
     * @param listener the listener that will be registered.
     */
    @Override
    public synchronized void attachListenerByType(ListenerType type, EventListener listener) {
        super.attachListenerByType(type, listener);
    }

    /**
     * This method detach an {@link EventListener} Object to this Lobby.
     *
     * @param type     the category from which the listener will be detached.
     * @param listener the listener that will be detached.
     */
    @Override
    public synchronized void detachListenerByType(ListenerType type, EventListener listener) {
        super.detachListenerByType(type, listener);
    }

    /**
     * This method disconnect the given Username from the server and, if the player was in a Room, clean the Room too.
     * This method is called only if the server saved the username in the username List.
     *
     * @param username Username of the player that lost the connection.
     */
    public synchronized void handleClientDisconnected(String username) {
        try {
            if (isPlayerInLobby(username)) {
                boolean userAlreadyInRoom = isUserInARoom(username);

                if (!userAlreadyInRoom && pendingUsername != null && pendingUsername.toLowerCase().equals(username.toLowerCase())) { //Waiting user to type room size

                    printLogMessage("Disconnecting " + username.toUpperCase() + " that was creating a room");
                    if (activeUsersList != null) {
                        activeUsersList.remove(username.toLowerCase());
                    }
                    creatingRoomLock.lock();
                    try {
                        canCreateNewRoom.set(true);
                        pendingUsername = null;
                        pendingVirtualView = null;
                    } finally {
                        creatingRoomLock.unlock();
                    }
                } else if (!userAlreadyInRoom) {
                    //the user ended a game and now disconnected
                    printLogMessage("Disconnecting " + username + " after the end of the game.");
                    activeUsersList.remove(username);
                } else if (userAlreadyInRoom) {  //at least one room has been created, the room can be 1..2/3 full
                    Room roomWithUser = getRoomWithUser(username.toLowerCase());
                    if (roomWithUser.getSIZE() == 3 && roomWithUser.getSpectator() != null
                            && roomWithUser.getSpectator().equals(username.toLowerCase())) {
                        // the user lost the game and after closed the match
                        printLogMessage("Disconnecting Spectator in" + roomWithUser.getRoomID() + ". The match is still working.");

                        roomWithUser.disconnectUser(username);
                        activeUsersList.remove(username);
                    } else {
                        printLogMessage("Disconnecting  all users already in " + roomWithUser.getRoomID() + " :" + roomWithUser.getActiveUsers().toString());

                        List<String> usersInRoom = new ArrayList<>(roomWithUser.getActiveUsers());
                        roomWithUser.disconnectAllUsers(username.toLowerCase());
                        activeRooms.remove(roomWithUser);
                        updateRoomCounter();
                        for (String user : usersInRoom) {
                            activeUsersList.remove(user);
                        }
                    }
                } else {
                    printErrorLogMessage("error in client " + username + " disconnection... case not implemented");
                }
            }
        } catch (UserNotFoundException e) {
            printErrorLogMessage("Exception: " + e.getMessage());
        } catch (RoomNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method handle a {@link CC_ConnectionRequestGameEvent} and admit the client to the Lobby.
     * If the username is already taken the method send a {@link CV_ConnectionRejectedErrorGameEvent} to the client with {@code errorCode: "USER_TAKEN"}.
     * If the server is full (no rooms available) the method send a {@link CV_ConnectionRejectedErrorGameEvent} to the client with {@code errorCode: "ROOM_FULL"}.
     * If another player is creating a Room the method send a {@link CV_ConnectionRejectedErrorGameEvent} to the client with {@code errorCode: "WAIT_FOR_CREATION"}.
     * If every room is full but the server can manage a new room, the method send a {@link CV_RoomSizeRequestGameEvent} to the client asking the desired size of the room.
     * If there's an available Room, the method add the client to the Room.
     *
     * @param event A {@link CC_ConnectionRequestGameEvent} with the info about the client.
     */
    @Override
    public synchronized void handleEvent(CC_ConnectionRequestGameEvent event) {

        printLogMessage("Connection request to lobby from: " + event.getUserIP().toString().substring(1) +
                "@" + event.getUserPort() + " with proposed username: " + event.getUsername().toUpperCase());

        if (activeUsersList.contains(event.getUsername())) {
            //If the username is already taken the method send a CV_ConnectionRejectedErrorGameEvent to the client with errorCode: "USER_TAKEN"
            sendConnectionRefusedEvent(event, "USER_TAKEN", "The choosen username is already used in this Server");
            printErrorLogMessage("Connection rejected because the username " + event.getUsername().toUpperCase() + " is already used in this Server");
        } else {
            if (!allRoomsAreFull()) {
                //If there's an available Room, the method add the client to the Room.
                try {
                    addUserToRoom(event.getUsername(), event.getVirtualView());
                } catch (NotFreeRoomAvailableException e) {
                    e.printStackTrace();
                    printErrorLogMessage("Not available rooms but entered in this section");
                }
            } else if (canCreateNewRoom.get() && !creatingRoomLock.isLocked() && MAX_ROOMS > activeRooms.size()) {
                // If every room is full but the server can manage a new room, the method send a CV_RoomSizeRequestGameEvent to the client asking the desired size of the room

                createNewRoom(event.getUsername(), event.getVirtualView());
            } else if (MAX_ROOMS == activeRooms.size()) {
                //If the server is full (no rooms available) the method send a CV_ConnectionRejectedErrorGameEvent to the client with  errorCode: "ROOM_FULL"

                sendConnectionRefusedEvent(event, "SERVER_FULL", "The room is actually full, please retry later.");

                printErrorLogMessage("Connection rejected because the Server is actually full");
            } else if (!canCreateNewRoom.get()) {
                // If another player is creating a Room the method send a {@link CV_ConnectionRejectedErrorGameEvent} to the client with errorCode: "WAIT_FOR_CREATION".

                sendConnectionRefusedEvent(event, "WAIT_FOR_CREATION", "A room is being created by another user, please wait few seconds.");

                printErrorLogMessage("Connection rejected because a room is being created by " + event.getUsername().toUpperCase());
            }
        }

    }

    /**
     * This method create a new {@link Room} with the desired size and add the user to the new Room.
     *
     * @param event A {@link VC_RoomSizeResponseGameEvent} with the desired size of the event.
     */
    @Override
    public synchronized void handleEvent(VC_RoomSizeResponseGameEvent event) {

        String roomID = "Room_#" + roomCounter;
        Room newRoom = new Room(event.getSize(), roomID);
        activeRooms.add(newRoom);
        updateRoomCounter();

        newRoom.addUser(pendingUsername, pendingVirtualView);

        canCreateNewRoom.set(true);

        printLogMessage("Successfully created new room (" + roomID + "). Size:" + event.getSize());

        pendingUsername = null;
        pendingVirtualView = null;
    }

    @Override
    public void handleEvent(CC_NewGameResponseEvent event) {
        printLogMessage("Reconnection request to lobby from: " + event.getUsername());


        //todo check che c'è in activeuserlist
        if (!allRoomsAreFull()) {
            //If there's an available Room, the method add the client to the Room.
            try {
                addUserToRoom(event.getUsername(), event.getVirtualView());
            } catch (NotFreeRoomAvailableException e) {
                e.printStackTrace();
                printErrorLogMessage("Not available rooms but entered in this section");
            }
        } else if (canCreateNewRoom.get() && !creatingRoomLock.isLocked() && MAX_ROOMS > activeRooms.size()) {
            // If every room is full but the server can manage a new room, the method send a CV_RoomSizeRequestGameEvent to the client asking the desired size of the room

            createNewRoom(event.getUsername(), event.getVirtualView());
        } else if (MAX_ROOMS == activeRooms.size()) {
            //If the server is full (no rooms available) the method send a CV_ConnectionRejectedErrorGameEvent to the client with  errorCode: "ROOM_FULL"

            sendConnectionRefusedEvent(event, "SERVER_FULL", "The room is actually full, please retry later.");

            printErrorLogMessage("Connection rejected because the Server is actually full");
        } else if (!canCreateNewRoom.get()) {
            // If another player is creating a Room the method send a {@link CV_ConnectionRejectedErrorGameEvent} to the client with errorCode: "WAIT_FOR_CREATION".

            sendConnectionRefusedEvent(event, "WAIT_FOR_CREATION", "A room is being created by another user, please wait few seconds.");

            printErrorLogMessage("Connection rejected because a room is being created by " + event.getUsername().toUpperCase());
        }
    }

    private void addUserToRoom(String username, VirtualView virtualView) throws NotFreeRoomAvailableException {
        if (!allRoomsAreFull()) {
            //If there's an available Room, the method add the client to the Room.
            activeUsersList.add(username);
            Room room = null;
            try {
                room = firstFreeRoom();
                if (!room.isFull()) {
                    room.addUser(username, virtualView);
                }
            } catch (NotFreeRoomAvailableException e) {
                e.printStackTrace();
            }
        } else {
            throw new NotFreeRoomAvailableException("Room not available");
        }
    }

    private void createNewRoom(String username, VirtualView virtualView) {
        creatingRoomLock.lock();
        if (!activeUsersList.contains(username)) {
            activeUsersList.add(username);
        }
        try {
            pendingUsername = username;
            pendingVirtualView = virtualView;

            canCreateNewRoom.set(false);
            //todo usare userIP e userPort??
            CV_RoomSizeRequestGameEvent request = new CV_RoomSizeRequestGameEvent("Insert the desired size of the room: ", username);
            notifyAllObserverByType(ListenerType.VIEW, request);
        } finally {
            creatingRoomLock.unlock();
        }
    }

    private void sendConnectionRefusedEvent(CC_ConnectionRequestGameEvent event, String errCode, String errorMessage) {
        CV_ConnectionRejectedErrorGameEvent msgError = new CV_ConnectionRejectedErrorGameEvent("", errCode, errorMessage, event.getUserIP(), event.getUserPort(), event.getUsername());
        notifyAllObserverByType(ListenerType.VIEW, msgError);
    }

    private void sendConnectionRefusedEvent(CC_NewGameResponseEvent event, String errCode, String errorMessage) {
        CV_ReconnectionRejectedErrorGameEvent msgError = new CV_ReconnectionRejectedErrorGameEvent("", errCode, errorMessage, event.getUsername());
        notifyAllObserverByType(ListenerType.VIEW, msgError);
    }


    /* ----------------------------------------------------------------------------------------------
                                         LOBBY UTILITY
       ----------------------------------------------------------------------------------------------*/

    /**
     * Update the roomCounter for Log purpose.
     */
    public void updateRoomCounter() {
        roomCounter = activeRooms.size();
    }

    /**
     * Check if the player is in this Lobby.
     *
     * @param username Username of the player whose presence you want to check.
     * @return {@code true} if the player is in this Lobby, {@code false} otherwise.
     * @throws UserNotFoundException when the user is not is the Lobby list of user.
     */
    private boolean isPlayerInLobby(String username) throws UserNotFoundException {
        if (activeUsersList.contains(username.toLowerCase())) {
            return true;
        }
        throw new UserNotFoundException("The user is not in the Lobby");
    }

    /**
     * Check if the player is in this Lobby AND in a Room.
     *
     * @param username Username of the player whose presence you want to check.
     * @return {@code true} if the player is in this Lobby AND in a Room, {@code false} otherwise.
     */
    private boolean isUserInARoom(String username) {
        try {
            getRoomWithUser(username);
            return true;
        } catch (UserNotFoundException | RoomNotFoundException e) {
            return false;
        }
    }

    /**
     * This method find the Room containing the give User.
     *
     * @param username Username of the player.
     * @return The {@link Room} containing the user.
     * @throws RoomNotFoundException if there aren't Rooms that contain the given username.
     * @throws UserNotFoundException when the user is not is the Lobby list of user.
     */
    private Room getRoomWithUser(String username) throws RoomNotFoundException, UserNotFoundException {
        if (isPlayerInLobby(username)) {
            for (Room room : activeRooms) {
                if (room.getActiveUsers().contains(username.toLowerCase())) {
                    return room;
                }
            }
            throw new RoomNotFoundException("Room not found");
        }
        throw new UserNotFoundException("The user is not in the Lobby");
    }

    /**
     * This method check if the given {@link Room} is full or not
     *
     * @param roomToCheck The {@link Room} to check if is full or free
     * @return {@code true} if the room is full, {@code false} otherwise
     */
    private boolean isRoomFull(Room roomToCheck) {
        return roomToCheck.isFull();
    }

    /**
     * This method check if all rooms are full
     *
     * @return {@code true} if all rooms are full, {@code false} if at least one is free
     */
    private boolean allRoomsAreFull() {
        for (Room actualRoom : activeRooms) {
            if (!isRoomFull(actualRoom)) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method check if there is a free room. If yes, it return the room, else should throw an exception
     *
     * @return The first {@link Room} available
     * @throws NotFreeRoomAvailableException if there aren't rooms available
     */
    private Room firstFreeRoom() throws NotFreeRoomAvailableException {
        if (allRoomsAreFull()) {
            throw new NotFreeRoomAvailableException("All room are full");
        }
        for (Room actualRoom : activeRooms) {
            if (!isRoomFull(actualRoom)) {
                return actualRoom;
            }
        }
        return null;
    }

    public void removeRoom(Room room) {
        activeRooms.remove(room);
    }


    /* ----------------------------------------------------------------------------------------------
                                         START GAME AND PRE-GAME
       ----------------------------------------------------------------------------------------------*/

    /**
     * Check if the PreGame can start or not in the room containing the given player.
     *
     * @param username Username of a player
     * @return {@code true} if the room containing the player is full and the PreGame can start, {@code false} otherwise
     */
    public boolean canStartPreGameForThisUser(String username) {
        Room userRoom = null;
        try {
            userRoom = getRoomWithUser(username);
            if (userRoom.isFull()) {
                return true;
            }
        } catch (RoomNotFoundException | UserNotFoundException e) {
            printErrorLogMessage("Exception: " + e.getMessage());

            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * Start the PreGame in the room containing the given player.
     *
     * @param username Username of a player
     */
    public synchronized void startPreGameForThisUser(String username) {
        if (canStartPreGameForThisUser(username)) {
            try {
                getRoomWithUser(username).beginPreGame();
            } catch (UserNotFoundException | RoomNotFoundException e) {
                printErrorLogMessage("Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Check if the PreGame can start or not in the room containing the given player.
     *
     * @param username Username of a player
     * @return {@code true} if each player has placed his workers and the Game can start, {@code false} otherwise
     */
    public boolean canStartGameForThisUser(String username) {
        try {
            return getRoomWithUser(username).isGameCanStart();
        } catch (RoomNotFoundException | UserNotFoundException e) {
            printErrorLogMessage("Exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Start the Game in the room containing the given player.
     *
     * @param username Username of a player
     */
    public synchronized void startGameForThisUser(String username) {
        if (canStartGameForThisUser(username)) {
            try {
                getRoomWithUser(username).beginGame();
            } catch (RoomNotFoundException | UserNotFoundException e) {
                printErrorLogMessage("Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public boolean canCleanRoomForThisUser(String username) {
        try {
            return getRoomWithUser(username).roomMustBeCleaned();
        } catch (RoomNotFoundException | UserNotFoundException e) {
            printErrorLogMessage("Exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public synchronized void cleanRoomForThisUser(String username) {
        if (canCleanRoomForThisUser(username)) {
            try {
                getRoomWithUser(username).cleanRoom();
            } catch (RoomNotFoundException | UserNotFoundException e) {
                printErrorLogMessage("Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    /* ----------------------------------------------------------------------------------------------
                                         LOG AND ERROR MESSAGE
       ----------------------------------------------------------------------------------------------*/

    /**
     * Print in the Server console a Log from the current Class.
     *
     * @param messageToPrint a {@link String} with the message to print.
     */
    private void printLogMessage(String messageToPrint) {
        System.out.println("\tLOBBY: " + messageToPrint);
    }

    /**
     * Print in the Server console Error Stream an Errror Log from the current Class.
     *
     * @param messageToPrint a {@link String} with the message to print.
     */
    private void printErrorLogMessage(String messageToPrint) {
        System.err.println("\tLOBBY: " + messageToPrint);

    }


    /* ----------------------------------------------------------------------------------------------
                                        METHODS NOT IMPLEMENTED
       ----------------------------------------------------------------------------------------------*/

    @Override
    public void handleEvent(CV_RoomUpdateGameEvent event) {
    }

    @Override
    public void handleEvent(CV_GameStartedGameEvent event) {

    }

    @Override
    public void handleEvent(CV_NewTurnEvent event) {

    }

    @Override
    public void handleEvent(CV_IslandUpdateEvent event) {

    }

    @Override
    public void handleEvent(CV_WaitMatchGameEvent event) {

    }

    @Override
    public void handleEvent(VC_PlayerCommandGameEvent event) {

    }

    @Override
    public void handleEvent(PlayerDisconnectedGameEvent event) {

    }

    @Override
    public void handleEvent(GameEvent event) {
    }

    @Override
    public void handleEvent(VC_ConnectionRequestGameEvent event) {
    }

    @Override
    public void handleEvent(CV_RoomSizeRequestGameEvent event) {
    }

    @Override
    public void handleEvent(CV_ConnectionRejectedErrorGameEvent event) {
    }

    @Override
    public void handleEvent(CV_ReconnectionRejectedErrorGameEvent event) {

    }

    @Override
    public void handleEvent(VC_ChallengerCardsChosenEvent event) {
    }

    @Override
    public void handleEvent(VC_PlayerCardChosenEvent event) {
    }

    @Override
    public void handleEvent(VC_ChallengerChosenFirstPlayerEvent event) {
    }

    @Override
    public void handleEvent(CV_ChallengerChosenEvent event) {

    }

    @Override
    public void handleEvent(CV_CardChoiceRequestGameEvent event) {

    }

    @Override
    public void handleEvent(CV_WaitPreMatchGameEvent event) {

    }

    @Override
    public void handleEvent(VC_NewGameResponseEvent event) {

    }

    @Override
    public void handleEvent(CV_GameErrorGameEvent event) {

    }

    @Override
    public void handleEvent(CV_ChallengerChooseFirstPlayerRequestEvent event) {

    }

    @Override
    public void handleEvent(VC_PlayerPlacedWorkerEvent event) {

    }

    @Override
    public void handleEvent(CV_CommandRequestEvent event) {

    }

    @Override
    public void handleEvent(CV_GameOverEvent event) {

    }

    @Override
    public void handleEvent(CV_PlayerPlaceWorkerRequestEvent event) {

    }

    @Override
    public void handleEvent(CV_NewGameRequestEvent event) {

    }
}
