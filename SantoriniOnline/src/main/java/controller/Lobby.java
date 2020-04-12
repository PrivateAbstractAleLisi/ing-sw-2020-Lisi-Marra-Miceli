package controller;

import event.core.EventListener;
import event.core.EventSource;
import event.core.ListenerType;
import event.events.*;

import java.util.ArrayList;
import java.util.List;

public class Lobby extends EventSource implements EventListener {

    private List<Room> activeRooms;
    private List<String> activeUsersList;
    private static boolean isRoomAlreadyCreated;
    private String pendingUsername;

    public Lobby() {
        isRoomAlreadyCreated = false;
        activeRooms = new ArrayList<Room>();
        activeUsersList = new ArrayList<String>();
    }

    public static boolean isRoomAlreadyCreated() {
        return isRoomAlreadyCreated;
    }

    public static void setIsRoomAlreadyCreated(boolean isRoomAlreadyCreated) {
        Lobby.isRoomAlreadyCreated = isRoomAlreadyCreated;
    }

    //todo deve lanciare gli eventi
    @Override
    public void handleEvent(ConnectionRequestGameEvent event) {
        if (activeUsersList.contains(event.getUsername())) {
            //todo l'username non va bene
        } else {
            if (isRoomAlreadyCreated()) {
                activeRooms.get(0).addUser(event.getUsername());
            } else {
                pendingUsername = event.getUsername();
                RoomSizeRequestGameEvent request = new RoomSizeRequestGameEvent("Inserisci la dimensione della stanza: ");
                notifyAllObserverByType(ListenerType.VIEW, request);
            }
        }
    }


    @Override
    public void handleEvent(GameEvent event) {

    }

    @Override
    public void handleEvent(RoomSizeResponseGameEvent event) {
        Room newRoom = new Room(event.getSize());
        activeRooms.add(newRoom);
        activeRooms.get(0).addUser(pendingUsername);
        setIsRoomAlreadyCreated(true);
    }


    @Override
    public void handleEvent(RoomSizeRequestGameEvent event) {

    }

    @Override
    public void handleEvent(ConnectionRejectedErrorGameEvent event) {

    }
}
