package controller;

import com.google.gson.Gson;
import event.core.EventListener;
import event.core.EventSource;
import event.gameEvents.CV_GameErrorGameEvent;
import event.gameEvents.CV_WaitGameEvent;
import event.gameEvents.GameEvent;
import event.gameEvents.lobby.*;
import event.gameEvents.match.*;
import event.gameEvents.prematch.*;
import model.BoardManager;
import model.CardEnum;
import model.Player;
import model.WorkerColors;
import model.exception.InvalidCardException;
import model.exception.InvalidMovementException;
import model.gamemap.Island;
import model.gamemap.Worker;
import placeholders.IslandData;

import javax.naming.LimitExceededException;
import java.util.*;

import static event.core.ListenerType.VIEW;

public class PreGameController extends EventSource implements EventListener {
    private BoardManager boardManager;
    private String challenger;
    private Room room;
    private Map<String, CardEnum> playersCardsCorrespondence;
    private List<CardEnum> availableCards;
    private Map<Integer, Player> turnSequence;
    private int currentTurnIndex;

    public PreGameController(BoardManager boardManager, Room room) {
        this.boardManager = boardManager;
        this.room = room;
        this.playersCardsCorrespondence = new HashMap<String, CardEnum>();
        this.availableCards = new ArrayList<CardEnum>();
        this.turnSequence = new HashMap<Integer, Player>();
    }

    public void start() {
        setColors();
        chooseChallenger();
    }

    public void chooseChallenger() {
        //choose the challenger in a random way
        Random random = new Random();
        int number = random.nextInt(room.getSIZE());
        List<String> players = room.getActiveUsers();
        challenger = players.get(number);
        for (String recipient : players) {
            if (!recipient.equals(challenger)) {
                CV_WaitGameEvent requestEvent = new CV_WaitGameEvent("is the challenger, he's now choosing the cards", challenger, recipient);
                notifyAllObserverByType(VIEW, requestEvent);
            }
        }

        //DEBUG
//        System.out.println("Il challenger è "+challenger);

        CV_ChallengerChosenEvent event = new CV_ChallengerChosenEvent(challenger, room.getSIZE());
        notifyAllObserverByType(VIEW, event);
    }

    public Map<String, CardEnum> getPlayersCardsCorrespondence() {
        return playersCardsCorrespondence;
    }

    public Map<Integer, Player> getTurnSequence() {
        return turnSequence;
    }

    public void setColors() {
        List<String> players = room.getActiveUsers();
        for (String p : players) {
            int i = players.indexOf(p);
            Player player = boardManager.getPlayer(p);

            WorkerColors color = WorkerColors.values()[i];

            player.setColor(color);

            for (Worker.IDs id : Worker.IDs.values()) {
                player.getWorker(id).setColor(color);
            }
        }
    }


    @Override
    public void handleEvent(VC_ChallengerCardsChosenEvent event) {
        availableCards = event.getCardsChosen();
        for (CardEnum card : availableCards) {
            try {
                boardManager.selectCard(card);
            } catch (InvalidCardException e) {
                CV_GameErrorGameEvent errorEvent = new CV_GameErrorGameEvent("invalid cards chosen!", challenger);
                notifyAllObserverByType(VIEW, errorEvent);

                CV_ChallengerChosenEvent requestEvent = new CV_ChallengerChosenEvent("", room.getSIZE());
                notifyAllObserverByType(VIEW, requestEvent);

            } catch (LimitExceededException e) {
                CV_GameErrorGameEvent errorEvent = new CV_GameErrorGameEvent("too many cards selected!", challenger);
                notifyAllObserverByType(VIEW, errorEvent);

                CV_ChallengerChosenEvent requestEvent = new CV_ChallengerChosenEvent("", room.getSIZE());
                notifyAllObserverByType(VIEW, requestEvent);


            }
        }
        List<String> players = room.getActiveUsers();
        int indexOfNextChoosingPlayer = players.indexOf(challenger);
        if (indexOfNextChoosingPlayer < room.getSIZE() - 1) {
            indexOfNextChoosingPlayer++;
        } else if (indexOfNextChoosingPlayer == room.getSIZE() - 1) {
            indexOfNextChoosingPlayer = 0;
        }
        for (String recipient : players) {
            if (!recipient.equals(players.get(indexOfNextChoosingPlayer))) {
                CV_WaitGameEvent requestEvent = new CV_WaitGameEvent("is choosing his card", players.get(indexOfNextChoosingPlayer), recipient);
                notifyAllObserverByType(VIEW, requestEvent);
            }
        }
        CV_CardChoiceRequestGameEvent requestEvent = new CV_CardChoiceRequestGameEvent("Choose one card from the list", availableCards, players.get(indexOfNextChoosingPlayer));
        notifyAllObserverByType(VIEW, requestEvent);
    }

    @Override
    public void handleEvent(VC_PlayerCardChosenEvent event) {
        try {
            boardManager.takeCard(event.getCard());
            boardManager.getPlayer(event.getPlayer()).setCard(event.getCard());
            playersCardsCorrespondence.put(event.getPlayer(), event.getCard());
            availableCards.remove(event.getCard());
            List<String> players = room.getActiveUsers();

            int indexOfChoosingPlayer = players.indexOf(event.getPlayer());
            int indexOfNextChoosingPlayer = indexOfChoosingPlayer;
            if (availableCards.size() > 0) { //there are cards remaining
                if ((indexOfChoosingPlayer < room.getSIZE() - 1)) {
                    indexOfNextChoosingPlayer++;
                } else if ((indexOfChoosingPlayer == room.getSIZE() - 1)) {
                    indexOfNextChoosingPlayer = 0;
                }
                for (String recipient : players) {
                    if (!recipient.equals(players.get(indexOfNextChoosingPlayer))) {
                        CV_WaitGameEvent requestEvent = new CV_WaitGameEvent("is choosing his card", players.get(indexOfNextChoosingPlayer), recipient);
                        notifyAllObserverByType(VIEW, requestEvent);
                    }
                }
                //send the event to the next player that has to choose the card
                CV_CardChoiceRequestGameEvent requestEvent = new CV_CardChoiceRequestGameEvent("Choose one card from the list", availableCards, players.get(indexOfNextChoosingPlayer));
                notifyAllObserverByType(VIEW, requestEvent);
            } else { // there are no more cards remaining
                ChallengerChooseFirstPlayer();
            }
        } catch (InvalidCardException e) {
            CV_GameErrorGameEvent errorEvent = new CV_GameErrorGameEvent("invalid card chosen!", challenger);
            notifyAllObserverByType(VIEW, errorEvent);

            CV_CardChoiceRequestGameEvent requestEvent = new CV_CardChoiceRequestGameEvent("Choose one card from the list", availableCards, event.getPlayer());
            notifyAllObserverByType(VIEW, requestEvent);


        }
    }

    public void ChallengerChooseFirstPlayer() {
        List<String> players = room.getActiveUsers();
        for (String recipient : players) {
            if (!recipient.equals(challenger)) {
                CV_WaitGameEvent requestEvent = new CV_WaitGameEvent("is choosing the first player", challenger, recipient);
                notifyAllObserverByType(VIEW, requestEvent);
            }
        }
        //the challenger has to choose the first player
        CV_ChallengerChooseFirstPlayerRequestEvent requestEvent = new CV_ChallengerChooseFirstPlayerRequestEvent("Choose the first player", challenger, players);
        notifyAllObserverByType(VIEW, requestEvent);
    }

    @Override
    public void handleEvent(VC_ChallengerChosenFirstPlayerEvent event) {
        boardManager.setPlayersCardsCorrespondence(playersCardsCorrespondence);

        String firstPlayer = event.getUsername();
        int turnSequenceIndex = 0;
        turnSequence.put(turnSequenceIndex, boardManager.getPlayer(firstPlayer));
        turnSequenceIndex++;


        String[] players = room.getActiveUsersCopy();
        int indexOfChosenPlayer = 0;
        //get the index of the chosen player
        for (int i = 0; i < players.length; i++) {
            if (players[i].equals(firstPlayer)) indexOfChosenPlayer = i;
        }
        //create the turnSequence based on the challenger choice
        int indexOfPlayerToAdd = 0;
        if (indexOfChosenPlayer == players.length - 1) { //the chosen player is the last in the list of the room

            while (indexOfPlayerToAdd < players.length - 1) { //adds all the players before the last one
                turnSequence.put(turnSequenceIndex, boardManager.getPlayer(players[indexOfPlayerToAdd]));
                turnSequenceIndex++;
                indexOfPlayerToAdd++;
            }
        } else { // the chosen player is not the last in the list of the room
            indexOfPlayerToAdd = indexOfChosenPlayer + 1;
            while (indexOfPlayerToAdd < players.length) { //adds all the player past the chosen player
                turnSequence.put(turnSequenceIndex, boardManager.getPlayer(players[indexOfPlayerToAdd]));
                turnSequenceIndex++;
                indexOfPlayerToAdd++;
            }
            if (turnSequence.size() < players.length) { // if there are, adds all the player before the chosen one
                indexOfPlayerToAdd = 0;
                while (indexOfPlayerToAdd < indexOfChosenPlayer) {
                    turnSequence.put(turnSequenceIndex, boardManager.getPlayer(players[indexOfPlayerToAdd]));
                    turnSequenceIndex++;
                    indexOfPlayerToAdd++;
                }
            }
        }

        room.setTurnSequence(turnSequence);
        currentTurnIndex = 0;
        askPlaceFirstWorkerForCurrentUser();
    }

    /**
     * This method ask to the current Active player to place the first worker (IDs.A) and send a WaitEvent to the other players
     * The first time currentTurnIndex should be 0
     */
    private void askPlaceFirstWorkerForCurrentUser() {
        List<Player> players = new ArrayList<Player>();
        for (Map.Entry<Integer, Player> player : turnSequence.entrySet()) {
            players.add(player.getValue());
        }

        Player activePlayer = turnSequence.get(currentTurnIndex);

        for (Player recipient : players) {
            if (!recipient.getUsername().equals(activePlayer.getUsername())) {
                CV_WaitGameEvent requestEvent = new CV_WaitGameEvent("is placing his workers", activePlayer.getUsername(), recipient.getUsername());
                notifyAllObserverByType(VIEW, requestEvent);
            }
        }

        IslandData currentIsland = boardManager.getIsland().getIslandDataCopy();
        Gson gson = new Gson();
        String islandDataJson = gson.toJson(currentIsland);
        CV_PlayerPlaceWorkerRequestEvent event = new CV_PlayerPlaceWorkerRequestEvent("Choose where to put your workers", activePlayer.getUsername(),
                islandDataJson, Worker.IDs.A);
        notifyAllObserverByType(VIEW, event);
    }

    /**
     * This method is called by handleEvent(VC_PlayerPlacedWorkerEvent event) to place the worker
     *
     * @param event
     * @throws InvalidMovementException
     * @throws CloneNotSupportedException
     */
    public void placeInvoke(VC_PlayerPlacedWorkerEvent event) throws InvalidMovementException, CloneNotSupportedException {
        Player actingPlayer = boardManager.getPlayer(event.getActingPlayer());
        Worker worker = actingPlayer.getWorker(event.getId());
        int x = event.getPosX();
        int y = event.getPosY();

        actingPlayer.getCard().placeWorker(worker, x, y, boardManager.getIsland());
    }

    public String getCurrentIslandJson(){
        IslandData currentIsland = boardManager.getIsland().getIslandDataCopy();
        Gson gson = new Gson();
        String islandDataJson = gson.toJson(currentIsland);
        return islandDataJson;
    }

    /**
     * this method handle the VC_PlayerPlacedWorkerEvent.
     * if the worker placed is the IDs.A it sends an event asking for the placement of IDs.B worker to the same player
     * if the worker placed is the IDs.B the method increment the currentTurnIndex and call askPlaceFirstWorkerForCurrentUser()
     *
     * @param event
     */
    @Override
    public void handleEvent(VC_PlayerPlacedWorkerEvent event) {
        try {
            placeInvoke(event);
        } catch (InvalidMovementException e) {
            CV_GameErrorGameEvent errorEvent = new CV_GameErrorGameEvent("invalid placement of the worker!", event.getActingPlayer());
            notifyAllObserverByType(VIEW, errorEvent);


            CV_PlayerPlaceWorkerRequestEvent requestEvent = new CV_PlayerPlaceWorkerRequestEvent("", event.getActingPlayer(), getCurrentIslandJson(), event.getId());
            notifyAllObserverByType(VIEW, requestEvent);

        } catch (CloneNotSupportedException e) {
            throw new RuntimeException();
        }

        if (event.getId() == Worker.IDs.A) {

            CV_PlayerPlaceWorkerRequestEvent newEvent = new CV_PlayerPlaceWorkerRequestEvent("Choose where to put your workers",
                    event.getActingPlayer(), getCurrentIslandJson(), Worker.IDs.B);
            notifyAllObserverByType(VIEW, newEvent);
        } else {
            if (currentTurnIndex + 1 < turnSequence.size()) {
                currentTurnIndex++;
                askPlaceFirstWorkerForCurrentUser();
            } else {
                room.setGameCanStart(true);
//                room.beginGame(turnSequence);
            }
        }
    }

    @Override
    public void handleEvent(CV_CommandRequestEvent event) {

    }

    @Override
    public void handleEvent(CV_GameOverEvent event) {

    }


    @Override //NO IMPL
    public void handleEvent(CV_ChallengerChosenEvent event) {
    }

    @Override
    public void handleEvent(CV_CardChoiceRequestGameEvent event) {

    }

    @Override
    public void handleEvent(CV_WaitGameEvent event) {

    }

    @Override
    public void handleEvent(CV_GameErrorGameEvent event) {

    }

    @Override
    public void handleEvent(CV_ChallengerChooseFirstPlayerRequestEvent event) {

    }

    @Override
    public void handleEvent(CV_PlayerPlaceWorkerRequestEvent event) {

    }

    @Override
    public void handleEvent(GameEvent event) {
    }

    @Override
    public void handleEvent(VC_RoomSizeResponseGameEvent event) {
    }

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
    public void handleEvent(VC_PlayerCommandGameEvent event) {

    }

    @Override
    public void handleEvent(VC_ConnectionRequestGameEvent event) {
    }

    @Override
    public void handleEvent(CC_ConnectionRequestGameEvent event) {

    }

    @Override
    public void handleEvent(CV_RoomSizeRequestGameEvent event) {
    }

    @Override
    public void handleEvent(CV_ConnectionRejectedErrorGameEvent event) {
    }
}
