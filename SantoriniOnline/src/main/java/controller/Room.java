package controller;

import event.core.EventSource;
import event.core.ListenerType;
import event.gameEvents.lobby.CV_RoomUpdateGameEvent;
import event.gameEvents.match.CV_GameStartedGameEvent;
import model.BoardManager;
import model.Player;
import model.WorkerColors;
import model.exception.AlreadyExistingPlayerException;
import model.gamemap.Worker;
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

    //    todo AFTER DEBUG: make private
    public PreGameController preGame;
//    private PreGameController preGame;

    public Room(int size) {
        this.SIZE = size;
        activeUsers = new ArrayList<String>();
        lastOccupiedPosition = activeUsers.size();
        boardManager = new BoardManager();
        virtualViewMap = new HashMap<>(SIZE);
        gameCanStart = false;
        this.turnSequence = new HashMap<Integer, Player>();

//        DEBUG
        System.out.println("DEBUG: ROOM: Stanza creata");
    }

    public void addUser(String username, VirtualView virtualView) {
        try {
            boardManager.addPlayer(username);

            this.activeUsers.add(username);
            this.virtualViewMap.put(username, virtualView);
            attachListenerByType(ListenerType.VIEW, virtualView);
            this.lastOccupiedPosition = activeUsers.size();

//            setColor(username);
            //        DEBUG
            System.out.println("DEBUG: ROOM: username aggiunto");

            CV_RoomUpdateGameEvent updateEvent = new CV_RoomUpdateGameEvent("Added a new Player", getActiveUsersCopy(), SIZE);
            notifyAllObserverByType(ListenerType.VIEW, updateEvent);

            /*if (lastOccupiedPosition == SIZE) {  //when room is filled.
                //beginPreGame();
                throw new Exception("ROOMREADY");
            } */
        } catch (LimitExceededException e) {
            e.printStackTrace();
        } catch (AlreadyExistingPlayerException e) {
            e.printStackTrace();
        }
    }

    //todo farlo con lo stack
    private void setColor(String username) {
        Player player;
        switch (activeUsers.size()) {
            case 1:
                player = boardManager.getPlayer(username);
                player.setColor(WorkerColors.BEIGE);
                player.getWorker(Worker.IDs.A).setColor(WorkerColors.BEIGE);
                player.getWorker(Worker.IDs.B).setColor(WorkerColors.BEIGE);
                break;
            case 2:
                player = boardManager.getPlayer(username);
                player.setColor(WorkerColors.BLUE);
                player.getWorker(Worker.IDs.A).setColor(WorkerColors.BLUE);
                player.getWorker(Worker.IDs.B).setColor(WorkerColors.BLUE);
                break;
            case 3:
                player = boardManager.getPlayer(username);
                player.setColor(WorkerColors.WHITE);
                player.getWorker(Worker.IDs.A).setColor(WorkerColors.WHITE);
                player.getWorker(Worker.IDs.B).setColor(WorkerColors.WHITE);
                break;
        }
    }

    public void logAllUsers() {
        System.out.println("IN THIS ROOM: ");
        for (int i = 0; i < activeUsers.size(); i++) {
            System.out.println(i + " | " + activeUsers.get(i));
        }
    }

    public boolean isFull() {
        return ((lastOccupiedPosition == SIZE));
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
        System.out.println("pregame started");
    }

    //    public void beginGame(Map<Integer, Player> turnSequence) {
//        turnController = new TurnController (boardManager, turnSequence, SIZE);
//    }
    public void beginGame() {
        turnController = new TurnController(boardManager, this.turnSequence, SIZE);

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
}
