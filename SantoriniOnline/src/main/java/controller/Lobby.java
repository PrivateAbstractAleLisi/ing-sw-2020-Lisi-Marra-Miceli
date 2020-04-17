package controller;

import event.core.EventListener;
import event.core.EventSource;
import event.core.ListenerType;
import event.gameEvents.*;
import event.gameEvents.lobby.*;
import event.gameEvents.prematch.*;
import view.VirtualView;

import java.util.ArrayList;
import java.util.List;

public class Lobby extends EventSource implements EventListener {

    private static Lobby instance = null; // istanza singola

    private Lobby() {
        isRoomAlreadyCreated = false;
        activeRooms = new ArrayList<Room>();
        activeUsersList = new ArrayList<String>();
    }

    private synchronized static Lobby createInstance() { // crea l oggetto solo se non esiste:
        if (instance == null) instance = new Lobby();
        return instance;
    }
    public static Lobby instance() { //metodo esportato // chiama metodo synchr. solo se l oggetto non esiste: if (instance == null) createInstance();
        return createInstance();
    }

    private List<Room> activeRooms;
    private List<String> activeUsersList;
    private boolean isRoomAlreadyCreated;
    private String pendingUsername;
    private VirtualView pendingVirtualView;

    public void debug() {
        System.out.println("debug");
    }

    public boolean isRoomAlreadyCreated() {
        return isRoomAlreadyCreated;
    }

    public  void setIsRoomAlreadyCreated(boolean isRoomAlreadyCreated) {
        this.isRoomAlreadyCreated = isRoomAlreadyCreated;
    }

    @Override
    public void handleEvent(CC_ConnectionRequestGameEvent event) {
        System.out.println("debug, lobby gestisce una richiesta di connessione");
        if (activeUsersList.contains(event.getUsername())) {
            CV_ConnectionRejectedErrorGameEvent msgError = new CV_ConnectionRejectedErrorGameEvent("", "USER_TAKEN", "The choosen username is already used in this Server", event.getUserIP(), event.getUserPort(), event.getUsername());
            notifyAllObserverByType(ListenerType.VIEW, msgError);
        } else {
            activeUsersList.add(event.getUsername());
            if (isRoomAlreadyCreated()) {
                if (activeRooms.get(0).getSIZE() > activeRooms.get(0).getLastOccupiedPosition()) {
                    activeRooms.get(0).addUser(event.getUsername(), event.getVirtualView());
                } else {
                    CV_ConnectionRejectedErrorGameEvent msgError = new CV_ConnectionRejectedErrorGameEvent("", "ROOM_FULL", "The room is actually full, please retry later.", event.getUserIP(), event.getUserPort(), event.getUsername());
                    notifyAllObserverByType(ListenerType.VIEW, msgError);
                }
            } else {
                pendingUsername = event.getUsername();
                pendingVirtualView = event.getVirtualView();

                CV_RoomSizeRequestGameEvent request = new CV_RoomSizeRequestGameEvent("Inserisci la dimensione della stanza: ", event.getUsername());
                notifyAllObserverByType(ListenerType.VIEW, request);
            }
        }
    }


    @Override
    public void handleEvent(VC_RoomSizeResponseGameEvent event) {
        Room newRoom = new Room(event.getSize());
        activeRooms.add(newRoom);
        activeRooms.get(0).addUser(pendingUsername, pendingVirtualView);
        setIsRoomAlreadyCreated(true);
        pendingUsername=null;
        pendingVirtualView=null;
    }


    //    NOT IMPLMENTED
    @Override
    public void handleEvent(CV_RoomUpdateGameEvent event) {
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
    public void handleEvent(CV_WaitGameEvent event) {

    }

    @Override
    public void handleEvent(CV_ChallengerChooseFirstPlayerRequestEvent event) {

    }
}
