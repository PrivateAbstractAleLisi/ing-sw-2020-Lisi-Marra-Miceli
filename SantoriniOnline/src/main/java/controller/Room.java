package controller;

import event.core.EventSource;
import event.core.ListenerType;
import event.events.RoomUpdateGameEvent;
import model.BoardManager;
import model.Player;
import model.exception.AlreadyExistingPlayerException;
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

//    todo AFTER DEBUG: make private
//    public PreGameController preGame;
    private PreGameController preGame;


    public Room(int size) {
        this.SIZE = size;
        activeUsers = new ArrayList<String>();
        lastOccupiedPosition = activeUsers.size();
        boardManager = new BoardManager();
        virtualViewMap = new HashMap<>(SIZE);

//        DEBUG
        System.out.println("DEBUG: ROOM: Stanza creata");
    }

    public void addUser(String username, VirtualView virtualView) {
        try {
            boardManager.addPlayer(username);

            this.activeUsers.add(username);
            this.virtualViewMap.put(username, virtualView);
            attachListenerByType(ListenerType.VIEW, virtualView);
            this.lastOccupiedPosition=activeUsers.size();

            //        DEBUG
            System.out.println("DEBUG: ROOM: username aggiunto");

            RoomUpdateGameEvent updateEvent = new RoomUpdateGameEvent("Added a new Player", getActiveUsersCopy(), SIZE);
            notifyAllObserverByType(ListenerType.VIEW, updateEvent);

            if (lastOccupiedPosition == SIZE) {
                beginPreGame();
            }
        } catch (LimitExceededException e) {
            e.printStackTrace();
        } catch (AlreadyExistingPlayerException e) {
            e.printStackTrace();
        }

    }

    public void logAllUsers() {
        System.out.println("IN THIS ROOM: ");
        for (int i = 0; i < activeUsers.size(); i++) {
            System.out.println(i + " | " + activeUsers.get(i));
        }
    }

    public void beginPreGame() {
        preGame = new PreGameController(boardManager, this);

        //virtualView added to listener
        for (int i = 0; i < SIZE; i++) {
            String tempUser = activeUsers.get(i);
            VirtualView tempVirtualView = virtualViewMap.get(tempUser);
            preGame.attachListenerByType(ListenerType.VIEW,tempVirtualView);
        }
        preGame.start();
    }

    public void beginGame(Map<Integer, Player> turnSequence) {
        turnController = new TurnController(turnSequence, SIZE);
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
}
