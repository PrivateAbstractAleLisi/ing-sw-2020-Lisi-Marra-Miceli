package it.polimi.ingsw.psp58.controller;

import it.polimi.ingsw.psp58.auxiliary.IslandData;
import it.polimi.ingsw.psp58.event.core.ControllerListener;
import it.polimi.ingsw.psp58.event.core.EventSource;
import it.polimi.ingsw.psp58.event.gameEvents.gamephase.CV_PreGameStartedGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.gamephase.CV_WorkerPlacementGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.lobby.*;
import it.polimi.ingsw.psp58.event.gameEvents.match.CV_IslandUpdateEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.VC_PlayerCommandGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.*;
import it.polimi.ingsw.psp58.exceptions.InvalidCardException;
import it.polimi.ingsw.psp58.exceptions.InvalidMovementException;
import it.polimi.ingsw.psp58.model.BoardManager;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.model.Player;
import it.polimi.ingsw.psp58.model.WorkerColors;
import it.polimi.ingsw.psp58.model.gamemap.Worker;

import javax.naming.LimitExceededException;
import java.util.*;

import static it.polimi.ingsw.psp58.event.core.ListenerType.VIEW;

/**
 * This class handles the event and sets the information of the pregame phase of a game: the choice of the challenger,
 * the placement of the workers, and choice of the first player. Then it lets start the actual game.
 */
public class PreGameController extends EventSource implements ControllerListener {
    private final BoardManager boardManager;
    private String challenger;
    private final Room room;
    private final Map<String, CardEnum> playersCardsCorrespondence;
    private List<CardEnum> availableCards;
    private final ArrayList<CardEnum> alreadyTakenCards;
    private final Map<Integer, Player> turnSequence;
    private int currentTurnIndex;

    public PreGameController(BoardManager boardManager, Room room) {
        this.boardManager = boardManager;
        this.room = room;
        this.playersCardsCorrespondence = new HashMap<>();
        this.availableCards = new ArrayList<>();
        this.alreadyTakenCards = new ArrayList<>();
        this.turnSequence = new HashMap<>();
    }

    /**
     * Method called by {@link Room} to initialize some information, sets the colors of the players and calls the {@code chooseChallenger} method
     */
    public void start() {
        printLogMessage("Pregame started");
        setColors();
        chooseChallenger();
    }

    /**
     * @return the name of the {@code challenger}
     */
    public String getChallenger() {
        return challenger;
    }

    /**
     * Chooses at random the {@code challenger} of the game and sends to him the {@link CV_PreGameStartedGameEvent} to choose the cards
     */
    public void chooseChallenger() {
        //choose the challenger in a random way
        Random random = new Random();
        int number = random.nextInt(room.getSIZE());
        List<String> players = room.getActiveUsers();
        challenger = players.get(number);

        CV_PreGameStartedGameEvent preGameStartedGameEvent = new CV_PreGameStartedGameEvent("", challenger, players);
        notifyAllObserverByType(VIEW, preGameStartedGameEvent);

        for (String recipient : players) {
            if (!recipient.equals(challenger)) {
                CV_WaitPreMatchGameEvent requestEvent = new CV_WaitPreMatchGameEvent("is the challenger, he's now choosing the cards", challenger, recipient, "CHALLENGER_CARDS");
                notifyAllObserverByType(VIEW, requestEvent);
            }
        }

        CV_ChallengerChosenEvent event = new CV_ChallengerChosenEvent(challenger, room.getSIZE());
        notifyAllObserverByType(VIEW, event);
        printLogMessage(challenger.toUpperCase() + " is the Challenger");
    }

    /**
     * @return a map of the correspondence of the player and the cards that they chosen
     */
    public Map<String, CardEnum> getPlayersCardsCorrespondence() {
        return playersCardsCorrespondence;
    }

    /**
     * @return a map of the turn sequence
     */
    public Map<Integer, Player> getTurnSequence() {
        return turnSequence;
    }

    /**
     * This method is called by {@code start} and chooses at random the {@link WorkerColors} of the player
     */
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

    /**
     * This method return the {@code turnSequence} as List
     *
     * @return return the turn sequence as List
     */
    private List<Player> getPlayersSequenceAsList() {
        List<Player> players = new ArrayList<>();
        for (Map.Entry<Integer, Player> player : turnSequence.entrySet()) {
            players.add(player.getValue());
        }
        return players;
    }

    /**
     * Handles the event of the cards chosen by the challenger and sends a {@link CV_CardChoiceRequestGameEvent} to the player that has to choose a card
     */
    @Override
    public void handleEvent(VC_ChallengerCardsChosenEvent event) {
        availableCards = event.getCardsChosen();
        for (CardEnum card : availableCards) {
            try {
                boardManager.selectCard(card);
            } catch (InvalidCardException e) {
                CV_PreGameErrorGameEvent errorEvent = new CV_PreGameErrorGameEvent("invalid cards chosen!", challenger);
                notifyAllObserverByType(VIEW, errorEvent);

                CV_ChallengerChosenEvent requestEvent = new CV_ChallengerChosenEvent("", room.getSIZE());
                notifyAllObserverByType(VIEW, requestEvent);

                printLogMessage(challenger.toUpperCase() + " has selected invalid cards. New request sent!");
            } catch (LimitExceededException e) {
                CV_PreGameErrorGameEvent errorEvent = new CV_PreGameErrorGameEvent("too many cards selected!", challenger);
                notifyAllObserverByType(VIEW, errorEvent);

                CV_ChallengerChosenEvent requestEvent = new CV_ChallengerChosenEvent("", room.getSIZE());
                notifyAllObserverByType(VIEW, requestEvent);

                printLogMessage(challenger.toUpperCase() + " has selected too many cards. New request sent!");
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
                CV_WaitPreMatchGameEvent requestEvent = new CV_WaitPreMatchGameEvent("is choosing his card", players.get(indexOfNextChoosingPlayer), recipient, "PLAYER_CARD");
                notifyAllObserverByType(VIEW, requestEvent);
            }
        }
        CV_CardChoiceRequestGameEvent requestEvent = new CV_CardChoiceRequestGameEvent("Choose one card from the list", availableCards, alreadyTakenCards, players.get(indexOfNextChoosingPlayer));
        notifyAllObserverByType(VIEW, requestEvent);
    }

    /**
     * Handles the event of choice of a card from one player and if there is a next player that has to choose sends a new {@link CV_CardChoiceRequestGameEvent} to him,
     * else calls {@code challengerChooseFirstPlayer} to let the {@code challenger} choose the player that has to start
     */
    @Override
    public void handleEvent(VC_PlayerCardChosenEvent event) {
        try {
            boardManager.takeCard(event.getCard());
            boardManager.getPlayer(event.getPlayer()).setCard(event.getCard());
            playersCardsCorrespondence.put(event.getPlayer(), event.getCard());
            availableCards.remove(event.getCard());
            alreadyTakenCards.add(event.getCard());
            List<String> players = room.getActiveUsers();

            int indexOfChoosingPlayer = players.indexOf(event.getPlayer());
            int indexOfNextChoosingPlayer = indexOfChoosingPlayer;
            if (!availableCards.isEmpty()) { //there are cards remaining
                if ((indexOfChoosingPlayer < room.getSIZE() - 1)) {
                    indexOfNextChoosingPlayer++;
                } else if ((indexOfChoosingPlayer == room.getSIZE() - 1)) {
                    indexOfNextChoosingPlayer = 0;
                }
                for (String recipient : players) {
                    if (!recipient.equals(players.get(indexOfNextChoosingPlayer))) {
                        CV_WaitPreMatchGameEvent requestEvent = new CV_WaitPreMatchGameEvent("is choosing his card", players.get(indexOfNextChoosingPlayer), recipient, "PLAYER_CARD");
                        notifyAllObserverByType(VIEW, requestEvent);
                    }
                }
                //send the event to the next player that has to choose the card
                CV_CardChoiceRequestGameEvent requestEvent = new CV_CardChoiceRequestGameEvent("Choose one card from the list", availableCards, alreadyTakenCards, players.get(indexOfNextChoosingPlayer));
                notifyAllObserverByType(VIEW, requestEvent);
            } else { // there are no more cards remaining
                challengerChooseFirstPlayer();
            }
        } catch (InvalidCardException e) {
            CV_PreGameErrorGameEvent errorEvent = new CV_PreGameErrorGameEvent("invalid card chosen!", challenger);
            notifyAllObserverByType(VIEW, errorEvent);

            CV_CardChoiceRequestGameEvent requestEvent = new CV_CardChoiceRequestGameEvent("Choose one card from the list", availableCards, alreadyTakenCards, event.getPlayer());
            notifyAllObserverByType(VIEW, requestEvent);
        }
    }

    /**
     * Sends to the {@code challenger} a {@link CV_ChallengerChooseFirstPlayerRequestEvent} to let him choose the first player
     */
    public void challengerChooseFirstPlayer() {
        List<String> players = room.getActiveUsers();
        for (String recipient : players) {
            if (!recipient.equals(challenger)) {
                CV_WaitPreMatchGameEvent requestEvent = new CV_WaitPreMatchGameEvent("is choosing the first player", challenger, recipient, "FIRST_PLAYER");
                notifyAllObserverByType(VIEW, requestEvent);
            }
        }
        //the challenger has to choose the first player
        CV_ChallengerChooseFirstPlayerRequestEvent requestEvent = new CV_ChallengerChooseFirstPlayerRequestEvent("Choose the first player", challenger, players, playersCardsCorrespondence);
        notifyAllObserverByType(VIEW, requestEvent);
    }

    /**
     * Handles the event of the {@code challenger} that has choose the player that has to start the turn. Then sets the {@code turnSequence} based on the choice and calls the {@code askPlaceFirstWorkerForCurrentUser} method
     */
    @Override
    public void handleEvent(VC_ChallengerChosenFirstPlayerEvent event) {
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

        printLogMessage("The challenger chosen " + turnSequence.get(0).getUsername().toUpperCase() + " as first player");

        room.setTurnSequence(turnSequence);
        //send an event to clients: pre game done, game is starting
        CV_WorkerPlacementGameEvent upEvent;
        Map<String, CardEnum> turnMap = new HashMap<>();
        Map<String, WorkerColors> colorMap = new HashMap<>();
        for (int t = 0; t < turnSequence.size(); t++) {
            String user = turnSequence.get(t).getUsername();
            turnMap.put(user, turnSequence.get(t).getCard().getName());
            colorMap.put(turnSequence.get(t).getUsername(), boardManager.getPlayer(user).getColor());

        }

        upEvent = new CV_WorkerPlacementGameEvent("game is starting with worker placement", turnMap, colorMap);
        notifyAllObserverByType(VIEW, upEvent);
        currentTurnIndex = 0;
        askPlaceFirstWorkerForCurrentUser();
    }

    /**
     * This method ask to the current Active player to place the first worker (IDs.A) and send a WaitEvent to the other players
     * The first time currentTurnIndex should be 0
     */
    private void askPlaceFirstWorkerForCurrentUser() {
        List<Player> players = getPlayersSequenceAsList();

        Player activePlayer = turnSequence.get(currentTurnIndex);

        for (Player recipient : players) {
            if (!recipient.getUsername().equals(activePlayer.getUsername())) {
                CV_WaitPreMatchGameEvent requestEvent = new CV_WaitPreMatchGameEvent("is placing his workers", activePlayer.getUsername(), recipient.getUsername(), "PLACE_WORKER");
                notifyAllObserverByType(VIEW, requestEvent);
            }
        }

        IslandData currentIsland = boardManager.getIsland().getIslandDataCopy();
        CV_PlayerPlaceWorkerRequestEvent event = new CV_PlayerPlaceWorkerRequestEvent("Choose where to put your workers", activePlayer.getUsername(),
                currentIsland, Worker.IDs.A);
        notifyAllObserverByType(VIEW, event);
    }

    /**
     * This method is called by handleEvent(VC_PlayerPlacedWorkerEvent event) to place the worker
     *
     * @param event event with info about the placement
     * @throws InvalidMovementException The placement isn't valid
     */
    private void placeInvoke(VC_PlayerPlacedWorkerEvent event) throws InvalidMovementException {
        Player actingPlayer = boardManager.getPlayer(event.getActingPlayer());
        Worker worker = actingPlayer.getWorker(event.getId());
        int x = event.getPosX();
        int y = event.getPosY();

        if (worker != null) {
            if (worker.getPosition() == null) {
                actingPlayer.getCard().placeWorker(worker, x, y, boardManager.getIsland());
            } else {
                throw new InvalidMovementException("Worker already placed");
            }
        } else {
            throw new IllegalArgumentException("Worker not found");
        }
    }

    /**
     * Sends a {@link CV_IslandUpdateEvent} to the recipient. If there is no recipient sends it to all
     *
     * @param recipient the recipient to which the island update has to be send
     */
    private void sendIslandUpdate(String recipient) {
        IslandData currentIsland = boardManager.getIsland().getIslandDataCopy();
        CV_IslandUpdateEvent islandUpdateEvent = new CV_IslandUpdateEvent("island update", currentIsland, recipient);
        notifyAllObserverByType(VIEW, islandUpdateEvent);
    }

    public IslandData getCurrentIsland() {
        return boardManager.getIsland().getIslandDataCopy();
    }

    /**
     * this method handle the VC_PlayerPlacedWorkerEvent.
     * if the worker placed is the IDs.A it sends an event asking for the placement of IDs.B worker to the same player
     * if the worker placed is the IDs.B the method increment the currentTurnIndex and call askPlaceFirstWorkerForCurrentUser()
     *
     * @param event the event containing the info of the placement of the worker
     */
    @Override
    public void handleEvent(VC_PlayerPlacedWorkerEvent event) {
        try {
            placeInvoke(event);

            //done only if no exception is thrown

            List<Player> players = getPlayersSequenceAsList();
            for (Player recipient : players) {
                if (!recipient.getUsername().equals(event.getActingPlayer())) {
                    sendIslandUpdate(recipient.getUsername());
                }
            }

            if (event.getId() == Worker.IDs.A) {
                CV_PlayerPlaceWorkerRequestEvent newEvent = new CV_PlayerPlaceWorkerRequestEvent("Choose where to put your workers",
                        event.getActingPlayer(), getCurrentIsland(), Worker.IDs.B);
                notifyAllObserverByType(VIEW, newEvent);

            } else {
                sendIslandUpdate(event.getActingPlayer());
                if (currentTurnIndex + 1 < turnSequence.size()) {
                    currentTurnIndex++;
                    askPlaceFirstWorkerForCurrentUser();
                } else {
                    room.setGameCanStartTrue();
                }
            }

        } catch (InvalidMovementException e) {
            CV_PreGameErrorGameEvent errorEvent = new CV_PreGameErrorGameEvent("invalid placement of the worker!", event.getActingPlayer());
            notifyAllObserverByType(VIEW, errorEvent);

            printErrorLogMessage(e.toString() + " - A new PlacementRequest has been send.");

            CV_PlayerPlaceWorkerRequestEvent requestEvent = new CV_PlayerPlaceWorkerRequestEvent("", event.getActingPlayer(), getCurrentIsland(), event.getId());
            notifyAllObserverByType(VIEW, requestEvent);
        } catch (IllegalArgumentException e) {
            CV_PreGameErrorGameEvent errorEvent = new CV_PreGameErrorGameEvent("Worker not found", event.getActingPlayer());
            notifyAllObserverByType(VIEW, errorEvent);

            printErrorLogMessage(e.toString() + " - worker not found - A new PlacementRequest has been send.");

            CV_PlayerPlaceWorkerRequestEvent requestEvent = new CV_PlayerPlaceWorkerRequestEvent("", event.getActingPlayer(), getCurrentIsland(), event.getId());
            notifyAllObserverByType(VIEW, requestEvent);
        }
    }

    /**
     * Print in the Server console a Log from the current Class
     *
     * @param messageToPrint a {@link String} with the message to print
     */
    private void printLogMessage(String messageToPrint) {
        System.out.println("\t \tROOM(" + room.getRoomID() + ")-PREGAME: " + messageToPrint);
    }

    /**
     * Print in the Server console Error Stream an Error Log from the current Class
     *
     * @param messageToPrint a {@link String} with the message to print
     */
    private void printErrorLogMessage(String messageToPrint) {
        System.err.println("\t \tROOM(" + room.getRoomID() + ")-GAME: " + messageToPrint);
    }


    /**
     * Not implemented handle of the event
     */
    @Override
    public void handleEvent(VC_RoomSizeResponseGameEvent event) {
        /* This class has not to implement this handleEvent*/
    }

    /**
     * Not implemented handle of the event
     */
    @Override
    public void handleEvent(VC_NewGameResponseEvent event) {
        /* This class has not to implement this handleEvent*/
    }


    /**
     * Not implemented handle of the event
     */
    @Override
    public void handleEvent(VC_PlayerCommandGameEvent event) {
        /* This class has not to implement this handleEvent*/
    }


    /**
     * Not implemented handle of the event
     */
    @Override
    public void handleEvent(VC_ConnectionRequestGameEvent event) {
        /* This class has not to implement this handleEvent*/
    }

    /**
     * Not implemented handle of the event
     */
    @Override
    public void handleEvent(CC_ConnectionRequestGameEvent event) {
        /* This class has not to implement this handleEvent*/
    }

    /**
     * Not implemented handle of the event
     */
    @Override
    public void handleEvent(CC_NewGameResponseEvent event) {
        /* This class has not to implement this handleEvent*/
    }

}
