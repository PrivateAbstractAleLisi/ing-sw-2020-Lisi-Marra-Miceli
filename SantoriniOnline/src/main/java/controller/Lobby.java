package controller;

import exceptions.NotFreeRoomAvailableException;
import event.core.EventListener;
import event.core.EventSource;
import event.core.ListenerType;
import event.gameEvents.CV_GameErrorGameEvent;
import event.gameEvents.prematch.CV_WaitPreMatchGameEvent;
import event.gameEvents.GameEvent;
import event.gameEvents.lobby.*;
import event.gameEvents.match.*;
import event.gameEvents.prematch.*;
import view.VirtualView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class Lobby extends EventSource implements EventListener {

    private static Lobby instance = null; // istanza singola

    private Lobby() {
        isRoomAlreadyCreated = false;
        canCreateNewRoom = new AtomicBoolean();
        canCreateNewRoom.set(true);
        activeRooms = new ArrayList<Room>();
        activeUsersList = new ArrayList<String>();
        roomCounter = 0;
    }

    private synchronized static Lobby createInstance() { // crea l oggetto solo se non esiste:
        instance = new Lobby();
        return instance;
    }

    public static Lobby instance() { //metodo esportato // chiama metodo synchr. solo se l oggetto non esiste: if (instance == null) createInstance();
        if (instance == null) createInstance();
        return instance;
    }

    private List<Room> activeRooms;
    private List<String> activeUsersList;
    private boolean isRoomAlreadyCreated;
    private AtomicBoolean canCreateNewRoom;
    private String pendingUsername;
    private VirtualView pendingVirtualView;
    private int roomCounter;

    private ReentrantLock lock1 = new ReentrantLock();

    //todo only one room for now
    private final int MAX_ROOMS = 1;

    public boolean isRoomAlreadyCreated() {
        return isRoomAlreadyCreated;
    }

    public void setIsRoomAlreadyCreated(boolean isRoomAlreadyCreated) {
        this.isRoomAlreadyCreated = isRoomAlreadyCreated;
    }

    @Override
    public void handleEvent(CC_ConnectionRequestGameEvent event) {
        printLogMessage("Connection request to lobby from: " + event.getUserIP().toString().substring(1) +
                "@" + event.getUserPort() + " with proposed username: " + event.getUsername().toUpperCase());

        if (activeUsersList.contains(event.getUsername())) {
            CV_ConnectionRejectedErrorGameEvent msgError = new CV_ConnectionRejectedErrorGameEvent("", "USER_TAKEN", "The choosen username is already used in this Server", event.getUserIP(), event.getUserPort(), event.getUsername());
            notifyAllObserverByType(ListenerType.VIEW, msgError);
            printErrorLogMessage("Connection rejected because the username " + event.getUsername().toUpperCase()+" is already used in this Server");
        } else {
            if (!allRoomsAreFull()) {
                activeUsersList.add(event.getUsername());
                Room room = null;
                try {
                    room = activeRooms.get(roomFree());
                } catch (NotFreeRoomAvailableException e) {
                    e.printStackTrace();
                }

                if (!room.isFull()) {
                    room.addUser(event.getUsername(), event.getVirtualView());
                }
            } else if (canCreateNewRoom.get() && !lock1.isLocked() && MAX_ROOMS > activeRooms.size()) {
                lock1.lock();
                activeUsersList.add(event.getUsername());
                try {
                    pendingUsername = event.getUsername();
                    pendingVirtualView = event.getVirtualView();

                    canCreateNewRoom.set(false);
                    CV_RoomSizeRequestGameEvent request = new CV_RoomSizeRequestGameEvent("Insert the desired size of the room: ", event.getUsername());
                    notifyAllObserverByType(ListenerType.VIEW, request);
                } finally {
                    lock1.unlock();
                }
            } else if (MAX_ROOMS == activeRooms.size()) {
                CV_ConnectionRejectedErrorGameEvent msgError = new CV_ConnectionRejectedErrorGameEvent("", "ROOM_FULL", "The room is actually full, please retry later.", event.getUserIP(), event.getUserPort(), event.getUsername());
                notifyAllObserverByType(ListenerType.VIEW, msgError);

                printErrorLogMessage("Connection rejected because the Room is actually full");
            } else if (!canCreateNewRoom.get()) {
                CV_ConnectionRejectedErrorGameEvent msgError = new CV_ConnectionRejectedErrorGameEvent("", "WAIT_FOR_CREATION", "A room is being created by another user, please wait few seconds.", event.getUserIP(), event.getUserPort(), event.getUsername());
                notifyAllObserverByType(ListenerType.VIEW, msgError);

                printErrorLogMessage("Connection rejected because a room is being created by " + event.getUsername().toUpperCase());
            }
        }
    }

    @Override
    public synchronized void handleEvent(VC_RoomSizeResponseGameEvent event) {
        String roomID = "Room_#" + roomCounter;
        Room newRoom = new Room(event.getSize(), roomID);
        activeRooms.add(newRoom);
        roomCounter++;

        newRoom.addUser(pendingUsername, pendingVirtualView);

        setIsRoomAlreadyCreated(true);
        canCreateNewRoom.set(true);

        printLogMessage("Successfully created new room (" + roomID + "). Size:" + event.getSize());

        pendingUsername = null;
        pendingVirtualView = null;

    }

    public boolean canStartPreRoom0() {
        if (!isRoomAlreadyCreated()) {
            return false;
        }
        Room room0 = getRoom0();
        return room0.isFull();
    }

    public boolean canStartGameForThisUser(String username) {
        if (activeUsersList.contains(username)) {
            for (Room room : activeRooms) {
                if (room.getActiveUsers().contains(username)) {
                    return room.isGameCanStart();
                }
            }
        }
        return false;
    }

    public void startGameForThisUser(String username) {
        if (canStartGameForThisUser(username)) {
            if (activeUsersList.contains(username)) {
                for (Room room : activeRooms) {
                    if (room.getActiveUsers().contains(username)) {
                        room.beginGame();
                    }
                }
            }
        }
    }

    private Room getRoom0() {
        return activeRooms.get(0);
    }

    public void startPreGameForRoom0() {
        activeRooms.get(0).beginPreGame();
    }


    public boolean isRoomFull(Room roomToCheck) {
        return roomToCheck.isFull();
    }

    public boolean allRoomsAreFull() {
        for (Room actualRoom : activeRooms) {
            if (!isRoomFull(actualRoom)) {
                return false;
            }
        }
        return true;
    }

    public int roomFree() throws NotFreeRoomAvailableException {
        if (allRoomsAreFull()) {
            throw new NotFreeRoomAvailableException("All room are full");
        }
        for (Room actualRoom : activeRooms) {
            if (!isRoomFull(actualRoom)) {
                return activeRooms.indexOf(actualRoom);
            }
        }
        return -1;
    }

    /**
     * Print in the Server console a Log from the current Class
     *
     * @param messageToPrint a {@link String} with the message to print
     */
    private void printLogMessage(String messageToPrint) {
        System.out.println("\tLOBBY: " + messageToPrint);
    }

    /**
     * Print in the Server console Error Stream an Errror Log from the current Class
     *
     * @param messageToPrint a {@link String} with the message to print
     */
    private void printErrorLogMessage(String messageToPrint) {
        System.err.println("\tLOBBY: " + messageToPrint);

    }

    //    NOT IMPLMENTED
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
}
