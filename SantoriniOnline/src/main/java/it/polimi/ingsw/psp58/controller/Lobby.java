package it.polimi.ingsw.psp58.controller;

import it.polimi.ingsw.psp58.event.core.ControllerListener;
import it.polimi.ingsw.psp58.event.core.EventSource;
import it.polimi.ingsw.psp58.event.gameEvents.lobby.*;
import it.polimi.ingsw.psp58.event.gameEvents.match.VC_PlayerCommandGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.VC_ChallengerCardsChosenEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.VC_ChallengerChosenFirstPlayerEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.VC_PlayerCardChosenEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.VC_PlayerPlacedWorkerEvent;
import it.polimi.ingsw.psp58.exceptions.NotFreeRoomAvailableException;
import it.polimi.ingsw.psp58.exceptions.RoomNotFoundException;
import it.polimi.ingsw.psp58.exceptions.UserNotFoundException;
import it.polimi.ingsw.psp58.view.VirtualView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import static it.polimi.ingsw.psp58.event.core.ListenerType.VIEW;

/**
 * Handle the connections to the game, create the Rooms and start PreGame and Game, implements the Singleton Pattern.
 */
public class Lobby extends EventSource implements ControllerListener {

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
        activeRooms = new ArrayList<>();
        activeUsersList = new ArrayList<>();
        roomCounter = 0;
    }

    /**
     * Private method: it creates the object if it doesn't exist.
     */
    private static synchronized void createInstance() {
        instance = new Lobby();
    }

    /**
     * Public method that return the unique instance of Lobby.
     * It calls the synchronized and private method only if the object doesn't exist.
     * If the object is already created, the method only return the instance of the object.
     *
     * @return Return the Instance of Lobby
     */
    public static Lobby instance() {
        if (instance == null) {
            createInstance();
        }
        return instance;
    }


    /* ----------------------------------------------------------------------------------------------
                                         FIELDS DECLARATIONS
       ----------------------------------------------------------------------------------------------*/

    /**
     * List of the currently active Rooms in this Server
     */
    private final List<Room> activeRooms;

    /**
     * List of the currently active users in this Server, stored by their Username ({@link String})
     */
    private final List<String> activeUsersList;

    /**
     * {@link AtomicBoolean} variable that help to synchronize the creation of a Room.
     */
    private final AtomicBoolean canCreateNewRoom;

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
    private final ReentrantLock creatingRoomLock = new ReentrantLock();

    /**
     * Number of maximum Rooms in this server
     */
    private static final int MAX_ROOMS = 16;

    /**
     * Message to show when there's an exception
     */
    private static final String EXCEPTION_MESSAGE = "Exception: ";


    /* ----------------------------------------------------------------------------------------------
                                  SYNCHRONIZED AND PUBLIC METHODS IMPLEMENTATION
       ----------------------------------------------------------------------------------------------*/

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

                if (!userAlreadyInRoom && pendingUsername != null && pendingUsername.toLowerCase().equalsIgnoreCase(username)) { //Waiting user to type room size
                    disconnectPendingUser(username);
                } else if (!userAlreadyInRoom) {
                    //the user ended a game and now disconnected
                    printLogMessage("Disconnecting " + username + " after the end of the game.");
                    activeUsersList.remove(username);
                } else {  //at least one room has been created, the room can be 1..2/3 full
                    disconnectUserInARoom(username);
                }
            }
        } catch (UserNotFoundException e) {
            printErrorLogMessage(EXCEPTION_MESSAGE + e.getMessage());
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
            sendConnectionRejectedEvent(event, "USER_TAKEN", "The chosen username is already used in this Server");
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

                sendConnectionRejectedEvent(event, "SERVER_FULL", "The room is actually full, please retry later.");

                printErrorLogMessage("Connection rejected because the Server is actually full");
            } else if (!canCreateNewRoom.get()) {
                // If another player is creating a Room the method send a {@link CV_ConnectionRejectedErrorGameEvent} to the client with errorCode: "WAIT_FOR_CREATION".

                sendConnectionRejectedEvent(event, "WAIT_FOR_CREATION", "A room is being created by another user, please wait few seconds.");

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
        int desiredSize = event.getSize();

        if (desiredSize == 2 || desiredSize == 3) {
            try {
                Room newRoom = new Room(event.getSize(), roomID);
                activeRooms.add(newRoom);
                updateRoomCounter();

                newRoom.addUser(pendingUsername, pendingVirtualView);

                canCreateNewRoom.set(true);

                printLogMessage("Successfully created new room (" + roomID + "). Size:" + event.getSize());

                pendingUsername = null;
                pendingVirtualView = null;
            } catch (IllegalArgumentException e) {
                CV_RoomSizeRequestGameEvent request = new CV_RoomSizeRequestGameEvent("Insert a valid size for the room: ", pendingUsername);
                notifyAllObserverByType(VIEW, request);
                printErrorLogMessage("Size of the room not valid - Sent a new request");
            }
        } else {
            CV_RoomSizeRequestGameEvent request = new CV_RoomSizeRequestGameEvent("Insert a valid size for the room: ", pendingUsername);
            notifyAllObserverByType(VIEW, request);
            printErrorLogMessage("Size of the room not valid - Sent a new request");
        }
    }

    /**
     * This method handle a {@link CC_ConnectionRequestGameEvent} and readmit the client to the Lobby.
     * If the server is full (no rooms available) the method send a {@link CV_ReconnectionRejectedErrorGameEvent} to the client with {@code errorCode: "ROOM_FULL"}.
     * If another player is creating a Room the method send a {@link CV_ReconnectionRejectedErrorGameEvent} to the client with {@code errorCode: "WAIT_FOR_CREATION"}.
     * If every room is full but the server can manage a new room, the method send a {@link CV_RoomSizeRequestGameEvent} to the client asking the desired size of the room.
     * If there's an available Room, the method add the client to the Room.
     *
     * @param event A {@link CC_ConnectionRequestGameEvent} with the info about the client.
     */
    @Override
    public synchronized void handleEvent(CC_NewGameResponseEvent event) {
        printLogMessage("Reconnection request to lobby from: " + event.getUsername());

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

            sendReconnectionRejectedEvent(event, "SERVER_FULL", "The room is actually full, please retry later.");

            printErrorLogMessage("Connection rejected because the Server is actually full");
        } else if (!canCreateNewRoom.get()) {
            // If another player is creating a Room the method send a {@link CV_ConnectionRejectedErrorGameEvent} to the client with errorCode: "WAIT_FOR_CREATION".

            sendReconnectionRejectedEvent(event, "WAIT_FOR_CREATION", "A room is being created by another user, please wait few seconds.");

            printErrorLogMessage("Connection rejected because a room is being created by " + event.getUsername().toUpperCase());
        }
    }

    /**
     * Add the user to the the first free room.
     *
     * @param username    Username chosen by the user
     * @param virtualView VirtualView of the user
     * @throws NotFreeRoomAvailableException if there aren't FreeRoomAvailable
     */
    private void addUserToRoom(String username, VirtualView virtualView) throws NotFreeRoomAvailableException {
        if (!allRoomsAreFull()) {
            //If there's an available Room, the method add the client to the Room.
            activeUsersList.add(username);
            Room room;
            try {
                room = firstFreeRoom();
                if (room != null && !room.isFull()) {
                    room.addUser(username, virtualView);
                }
            } catch (NotFreeRoomAvailableException | NullPointerException e) {
                e.printStackTrace();
            }
        } else {
            throw new NotFreeRoomAvailableException("Room not available");
        }
    }

    /**
     * Ask the user a specific size of the room. Uses the {@code creatingRoomLock} Lock.
     *
     * @param username    Username chosen by the user
     * @param virtualView VirtualView of the user
     */
    private void createNewRoom(String username, VirtualView virtualView) {
        creatingRoomLock.lock();
        if (!activeUsersList.contains(username)) {
            activeUsersList.add(username);
        }
        try {
            pendingUsername = username;
            pendingVirtualView = virtualView;

            canCreateNewRoom.set(false);
            CV_RoomSizeRequestGameEvent request = new CV_RoomSizeRequestGameEvent("Insert the desired size of the room: ", username);
            notifyAllObserverByType(VIEW, request);
        } finally {
            creatingRoomLock.unlock();
        }
    }

    /**
     * Send a {@link CV_ConnectionRejectedErrorGameEvent} event with the given error code.
     *
     * @param event        A {@link CC_ConnectionRequestGameEvent} with the info about the client and the user.
     * @param errCode      The error code to add to the event.
     * @param errorMessage The message to send with the event.
     */
    private void sendConnectionRejectedEvent(CC_ConnectionRequestGameEvent event, String errCode, String errorMessage) {
        CV_ConnectionRejectedErrorGameEvent msgError = new CV_ConnectionRejectedErrorGameEvent("", errCode, errorMessage, event.getUserIP(), event.getUserPort(), event.getUsername());
        notifyAllObserverByType(VIEW, msgError);
    }

    /**
     * Send a {@link CV_ReconnectionRejectedErrorGameEvent} event with the given error code.
     *
     * @param event        A {@link CC_NewGameResponseEvent} with the info about the client and the user.
     * @param errCode      The error code to add to the event.
     * @param errorMessage The message to send with the event.
     */
    private void sendReconnectionRejectedEvent(CC_NewGameResponseEvent event, String errCode, String errorMessage) {
        CV_ReconnectionRejectedErrorGameEvent msgError = new CV_ReconnectionRejectedErrorGameEvent("", errCode, errorMessage, event.getUsername());
        notifyAllObserverByType(VIEW, msgError);
    }

    /**
     * Disconnect the pending user that was creating the room and clean everything.
     *
     * @param username Username of disconnecting user
     */
    private void disconnectPendingUser(String username) {
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
    }

    /**
     * Disconnect a user in a Room and clean everything.
     *
     * @param username Username of disconnecting user
     */
    private void disconnectUserInARoom(String username) throws UserNotFoundException, RoomNotFoundException {
        Room roomWithUser = getRoomWithUser(username.toLowerCase());
        if (roomWithUser.getSIZE() == 3 && roomWithUser.getSpectator() != null
                && roomWithUser.getSpectator().equalsIgnoreCase(username)) {
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
     */
    private boolean isPlayerInLobby(String username) {
        return activeUsersList.contains(username.toLowerCase());
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

    /**
     * Remove a room from the list in Lobby.
     *
     * @param room Room to remove.
     */
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
        Room userRoom;
        try {
            userRoom = getRoomWithUser(username);
            if (userRoom.isFull()) {
                return true;
            }
        } catch (RoomNotFoundException | UserNotFoundException e) {
            printErrorLogMessage(EXCEPTION_MESSAGE + e.getMessage());

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
                printErrorLogMessage(EXCEPTION_MESSAGE + e.getMessage());
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
            return getRoomWithUser(username).canGameStart();
        } catch (RoomNotFoundException | UserNotFoundException e) {
            printErrorLogMessage(EXCEPTION_MESSAGE + e.getMessage());
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
                printErrorLogMessage(EXCEPTION_MESSAGE + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Check if is possible to clean the room containing this user.
     *
     * @param username Username of a player
     * @return True if the room can be cleaned, false otherwise
     */
    public boolean canCleanRoomForThisUser(String username) {
        try {
            return getRoomWithUser(username).roomMustBeCleaned();
        } catch (RoomNotFoundException | UserNotFoundException e) {
            printErrorLogMessage(EXCEPTION_MESSAGE + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Clean the room for this user and delete everything.
     *
     * @param username Username of a player
     */
    public synchronized void cleanRoomForThisUser(String username) {
        if (canCleanRoomForThisUser(username)) {
            try {
                getRoomWithUser(username).cleanRoom();
            } catch (RoomNotFoundException | UserNotFoundException e) {
                printErrorLogMessage(EXCEPTION_MESSAGE + e.getMessage());
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
     * Print in the Server console Error Stream an Error Log from the current Class.
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
    public void handleEvent(VC_PlayerCommandGameEvent event) {
        //NOT IMPLEMENTED IN THIS CLASS
    }

    @Override
    public void handleEvent(VC_ConnectionRequestGameEvent event) {
        //NOT IMPLEMENTED IN THIS CLASS
    }

    @Override
    public void handleEvent(VC_ChallengerCardsChosenEvent event) {
        //NOT IMPLEMENTED IN THIS CLASS
    }

    @Override
    public void handleEvent(VC_PlayerCardChosenEvent event) {
        //NOT IMPLEMENTED IN THIS CLASS
    }

    @Override
    public void handleEvent(VC_ChallengerChosenFirstPlayerEvent event) {
        //NOT IMPLEMENTED IN THIS CLASS
    }

    @Override
    public void handleEvent(VC_NewGameResponseEvent event) {
        //NOT IMPLEMENTED IN THIS CLASS
    }

    @Override
    public void handleEvent(VC_PlayerPlacedWorkerEvent event) {
        //NOT IMPLEMENTED IN THIS CLASS
    }
}
