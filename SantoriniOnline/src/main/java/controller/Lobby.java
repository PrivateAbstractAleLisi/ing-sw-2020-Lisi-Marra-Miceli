package controller;

import event.core.EventListener;
import event.core.EventSource;
import event.core.ListenerType;
import event.events.*;
import view.VirtualView;

import java.util.ArrayList;
import java.util.List;

public class Lobby extends EventSource implements EventListener {

    private List<Room> activeRooms;
    private List<String> activeUsersList;
    private static boolean isRoomAlreadyCreated;
    private String pendingUsername;
    private VirtualView pendingVirtualView;

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

    @Override
    public void handleEvent(ConnectionRequestServerGameEvent event) {
        if (activeUsersList.contains(event.getUsername())) {
            ConnectionRejectedErrorGameEvent msgError = new ConnectionRejectedErrorGameEvent("", "USER_TAKEN", "The choosen username is already used in this Server", event.getUserIP(), event.getUserPort(), event.getUsername());
            notifyAllObserverByType(ListenerType.VIEW, msgError);
        } else {
            if (isRoomAlreadyCreated()) {
                if (activeRooms.get(0).getSIZE() > activeRooms.get(0).getLastOccupiedPosition()) {
                    activeRooms.get(0).addUser(event.getUsername(),event.getVirtualView());
                } else {
                    ConnectionRejectedErrorGameEvent msgError = new ConnectionRejectedErrorGameEvent("", "ROOM_FULL", "The room is actually full, please retry later.", event.getUserIP(), event.getUserPort(), event.getUsername());
                    notifyAllObserverByType(ListenerType.VIEW, msgError);
                }
            } else {
                pendingUsername = event.getUsername();
                pendingVirtualView=event.getVirtualView();
                RoomSizeRequestGameEvent request = new RoomSizeRequestGameEvent("Inserisci la dimensione della stanza: ");
                notifyAllObserverByType(ListenerType.VIEW, request);
            }
        }
    }


    @Override
    public void handleEvent(RoomSizeResponseGameEvent event) {
        Room newRoom = new Room(event.getSize());
        activeRooms.add(newRoom);
        activeRooms.get(0).addUser(pendingUsername,pendingVirtualView);
        setIsRoomAlreadyCreated(true);
    }

    @Override
    public void handleEvent(RoomUpdateGameEvent event) {
    }

    @Override
    public void handleEvent(GameEvent event) {
    }

    @Override
    public void handleEvent(ConnectionRequestGameEvent event) {
    }


    @Override
    public void handleEvent(RoomSizeRequestGameEvent event) {
    }

    @Override
    public void handleEvent(ConnectionRejectedErrorGameEvent event) {
    }

    @Override
    public void handleEvent(ChallengerCardsChosenEvent event) {
    }

    @Override
    public void handleEvent(PlayerCardChosenEvent event) {
    }

    @Override
    public void handleEvent(ChallengerChosenFirstPlayerEvent event) {
    }

    @Override
    public void handleEvent(ChallengerChosenEvent event) {

    }
}
