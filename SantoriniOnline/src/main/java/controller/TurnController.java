package controller;

import com.google.gson.Gson;
import event.core.EventListener;
import event.core.EventSource;
import event.core.ListenerType;
import event.gameEvents.CV_GameErrorGameEvent;
import event.gameEvents.CV_WaitGameEvent;
import event.gameEvents.GameEvent;
import event.gameEvents.lobby.*;
import event.gameEvents.match.*;
import event.gameEvents.prematch.*;
import model.*;
import model.exception.InvalidBuildException;
import model.exception.InvalidMovementException;
import model.exception.WinningException;
import model.gamemap.BlockTypeEnum;
import model.gamemap.Worker;
import placeholders.IslandData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static event.core.ListenerType.VIEW;
import static model.TurnAction.*;
import static model.gamemap.Worker.IDs;

/**
 * This class filters VirtualView events, it creates a turn(on model) for current player and manages it
 *
 * @author alelisi
 */
//TODO complete the implementation when model turn class is ready
public class TurnController extends EventSource implements EventListener {

    private Map<Integer, Player> turnSequence;
    private int currentTurnIndex;
    private Turn currentTurnInstance;
    private int currentTurnNumber;

    private int numberOfPlayers;
    private Player currentPlayer;
    private BoardManager board;

    public TurnController(BoardManager boardManager, Map<Integer, Player> turnSequence, int numberOfPlayers) {
        this.board = boardManager;
        this.turnSequence = turnSequence;
        this.currentTurnIndex = -1;
        this.numberOfPlayers = numberOfPlayers;
        this.currentTurnNumber = 0;
    }

    /**
     * sets a turnSequence.
     *
     * @param turnSequence adds a [TURN_NUMBER, PLAYER] map representing for each player its position in the turn rotation
     */
    public void setTurnSequence(Map<Integer, Player> turnSequence) {
        this.turnSequence = turnSequence;
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public String getCurrentPlayerUser() {
        return this.currentPlayer.getUsername();
    }

    /**
     * Called by the PreGame: start the match sending the first Events
     */
    public void firstTurn() {
        if (currentTurnNumber == 0) {
            nextTurn();
        }
    }

    /**
     * This method send a NewTurnEvent to all players, with basic info about the new turn
     */
    private void sendNewTurnEvent() {
        List<String> turnList = getCurrentTurnListUsername();
        CV_NewTurnEvent event = new CV_NewTurnEvent("is your turn!", currentPlayer.getUsername(), turnList);
        notifyAllObserverByType(ListenerType.VIEW, event);
    }

    /**
     * This method return a List of {@link Player} the turnSequence where the first element is the {@code currentPlayer}
     *
     * @return List of {@link Player} the turnSequence where the first element is the {@code currentPlayer}
     */
    private List<Player> getCurrentTurnList() {
        List<Player> turnSequence = getPlayersSequenceAsList();

        List<Player> currentTurn = new ArrayList<>();

        int index = this.currentTurnIndex;

        while (currentTurn.size() < numberOfPlayers) {
            currentTurn.add(turnSequence.get(index));
            if (index == numberOfPlayers - 1) {
                index = 0;
            } else {
                index++;
            }
        }

        return currentTurn;
    }

    /**
     * This method return a List of {@link String} the turnSequence where the first element is the {@code currentPlayer}
     *
     * @return List of {@link String} the turnSequence where the first element is the {@code currentPlayer}
     */
    public List<String> getCurrentTurnListUsername() {
        List<String> usernameTurnList = new ArrayList<>();

        List<Player> playerTurnList = getCurrentTurnList();

        for (Player player : playerTurnList) {
            usernameTurnList.add(player.getUsername());
        }

        return usernameTurnList;
    }

    /**
     * This method return the {@code turnSequence} as List
     *
     * @return return the turn sequence as List
     */
    private List<Player> getPlayersSequenceAsList() {
        List<Player> players = new ArrayList<Player>();
        for (Map.Entry<Integer, Player> player : turnSequence.entrySet()) {
            players.add(player.getValue());
        }
        return players;
    }

    private void sendCommandRequest(String actingPlayer) {
        List<TurnAction> availableActions = new ArrayList<TurnAction>();
        int numberOfMove = currentTurnInstance.getNumberOfMove();
        int numberOfBuild = currentTurnInstance.getNumberOfBuild();
        BehaviourManager behaviour = board.getPlayer(actingPlayer).getBehaviour();
        boolean isPrometheus = currentPlayer.getCard().getName() == CardEnum.PROMETHEUS;

        List<int[]> availableMovementBlocksA = new ArrayList<int[]>();
        List<int[]> availableMovementBlocksB = new ArrayList<int[]>();

        List<int[]> availableBuildBlocksA = new ArrayList<int[]>();
        List<int[]> availableBuildBlocksB = new ArrayList<int[]>();

        //Check if the player can MOVE and set the possible moves
        if ((behaviour.getMovementsRemaining() != 0) &&
                ((numberOfBuild == 0) || ((isPrometheus) && (numberOfBuild == 1)))) {//MOVE is a valid action
            availableActions.add(MOVE);
            if (numberOfMove == 0 && numberOfBuild == 0) { // he can move with any of the two workers

                //gets the possibles move for the worker A
                availableMovementBlocksA.addAll(currentTurnInstance.validActions(IDs.A, MOVE));
                //gets the possible moves for the worker B
                availableMovementBlocksB.addAll(currentTurnInstance.validActions(IDs.B, MOVE));
            } else if (behaviour.getMovementsRemaining() != 0) { // the player can make a second move with the worker used for the other actions, also PROMETHEUS that has built before move

                if (currentTurnInstance.getWorkerID() == IDs.A) { //the player has moved with the worker A
                    availableMovementBlocksA.addAll(currentTurnInstance.validActions(IDs.A, MOVE));

                    availableMovementBlocksB = null;
                } else if (currentTurnInstance.getWorkerID() == IDs.B) { //the player has moved with the worker B
                    availableMovementBlocksA = null;

                    availableMovementBlocksB.addAll(currentTurnInstance.validActions(IDs.B, MOVE));
                }
            }
        } else {
            availableMovementBlocksA = null;
            availableMovementBlocksB = null;
        }
        //Double check about what actions can choose the player
        if (availableMovementBlocksA == null && availableMovementBlocksB == null) {
            availableActions.remove(MOVE);
        }

        //Check if the player can BUILD and set the possible builds
        if ((behaviour.getBlockPlacementLeft() != 0) &&
                ((numberOfMove > 0) || ((isPrometheus) && (numberOfMove == 0)))) { //BUILD is a valid action
            availableActions.add(BUILD);
            if (numberOfMove == 0 && isPrometheus) { // the player can build with any worker (ONLY PROMETHEUS)
                availableBuildBlocksA.addAll(currentTurnInstance.validActions(IDs.A, BUILD));
                availableBuildBlocksB.addAll(currentTurnInstance.validActions(IDs.B, BUILD));
            } else {
                Worker.IDs id = currentTurnInstance.getWorkerID();
                if (id == IDs.A) {
                    availableBuildBlocksA.addAll(currentTurnInstance.validActions(id, BUILD));
                    availableBuildBlocksB = null;
                } else if (id == IDs.B) {
                    availableBuildBlocksA = null;
                    availableBuildBlocksB.addAll(currentTurnInstance.validActions(id, BUILD));
                }
            }
        } else {
            availableBuildBlocksA = null;
            availableBuildBlocksB = null;
        }

        //Double check about what actions can choose the player
        if (availableBuildBlocksA == null && availableBuildBlocksB == null) {
            availableActions.remove(BUILD);
        }

        //todo check se c'è un errrore
        if ((numberOfMove > 0 && numberOfBuild > 0 && !isPrometheus) ||
                (isPrometheus && (numberOfMove == 1 && numberOfBuild >= 1))) { //the player can terminate the turn
            availableActions.add(PASS);
        }

        if (availableActions.isEmpty()) { //the player has no actions possibles so he loses
            lose(actingPlayer);
        }
        CV_CommandRequestEvent requestEvent = new CV_CommandRequestEvent("this are the actions you can do", availableActions, availableBuildBlocksA, availableMovementBlocksA,
                availableBuildBlocksB, availableMovementBlocksB, actingPlayer);
        notifyAllObserverByType(VIEW, requestEvent);
    }

    private void nextTurn() {
        currentTurnInstance = null; //destroys current turn on model
        if (this.currentTurnIndex == numberOfPlayers - 1) { //Ri inizia il giro
            this.currentTurnIndex = 0;
        } else {
            this.currentTurnIndex++;
        }

        currentPlayer = turnSequence.get(currentTurnIndex);
        handleCurrentTurn();
        currentTurnNumber++;

        //Notify All Players
        String currentPlayerUsername = currentPlayer.getUsername();
        List<Player> players = getPlayersSequenceAsList();

        sendNewTurnEvent();

        for (Player recipient : players) {
            if (!recipient.getUsername().equals(currentPlayerUsername)) {
                CV_WaitGameEvent requestEvent = new CV_WaitGameEvent("Is the turn of", currentPlayerUsername, recipient.getUsername());
                notifyAllObserverByType(VIEW, requestEvent);
            }
        }
        sendIslandUpdate();
        sendCommandRequest(currentPlayerUsername);
    }

    //TODO
    private void win(Player winner) {
        List<String> losingPlayers = new ArrayList<>();
        for (Map.Entry<Integer, Player> player : turnSequence.entrySet()) {
            if (!player.getValue().getUsername().equals(winner.getUsername())) {
                losingPlayers.add(player.getValue().getUsername());
            }
        }
        CV_GameOverEvent gameOverEvent = new CV_GameOverEvent("lose", winner.getUsername(), losingPlayers);
        notifyAllObserverByType(VIEW, gameOverEvent);
    }

    //TODO
    private void lose(String player) {
        if (numberOfPlayers == 3) {
            //removes the player from the list
            for (Integer integer : turnSequence.keySet()) {
                if (turnSequence.get(integer).getUsername().equals(player)) {
                    turnSequence.remove(integer);
                    numberOfPlayers = 2;
                }
            }
            List<String> losers = new ArrayList<String>();
            losers.add(player);
            CV_GameOverEvent gameOverEvent = new CV_GameOverEvent("lose", null, losers);
            notifyAllObserverByType(VIEW, gameOverEvent);
        } else if (numberOfPlayers == 2) {
            //perde direttamente
            if (turnSequence.get(0).getUsername().equals(player)) { //the loser
                win(turnSequence.get(1)); //the other one wins the game
            } else {
                win(turnSequence.get(0)); //the other one wins the game
            }
        }
    }

    /**
     * @author: alelisi
     * creates a new {@link Turn} istance (on model) and checks immediately for each worker if you can move or not at least in one cell
     */
    private void handleCurrentTurn() {

        //create a turn on model for this player
        currentTurnInstance = new Turn(currentPlayer, board);


        //current player might play with Prometheus

        boolean isPrometheus;
        isPrometheus = currentPlayer.getCard().getName() == CardEnum.PROMETHEUS;

        System.out.println("DEBUG: he/her's not playing with Prometheus");

        Worker wa = currentPlayer.getWorker(IDs.A);
        Worker wb = currentPlayer.getWorker(IDs.B);

        boolean isWorkerALocked, isWorkerBLocked;

        //Checks if it's able to perform at least one movement
        if (!isPrometheus) {

            //get available movement cells for each worker
            List<int[]> availableCells_Worker_A = currentTurnInstance.validActions(IDs.A, MOVE);
            List<int[]> availableCells_Worker_B = currentTurnInstance.validActions(IDs.B, MOVE);
            isWorkerALocked = availableCells_Worker_A == null;
            isWorkerBLocked = availableCells_Worker_B == null;

            if (isWorkerALocked && isWorkerBLocked) {
                lose(currentPlayer.getUsername());
                //you lost, notify the view
            }
            //you can move at least one time.

            //TODO
        } else if (isPrometheus) {
            List<int[]> availableCells_Worker_A = currentTurnInstance.validActions(IDs.A, MOVE);
            List<int[]> availableCells_Worker_B = currentTurnInstance.validActions(IDs.B, MOVE);
            List<int[]> availableBuildCells_Worker_A = currentTurnInstance.validActions(IDs.A, BUILD);
            List<int[]> availableBuildCells_Worker_B = currentTurnInstance.validActions(IDs.B, BUILD);
            isWorkerALocked = availableCells_Worker_A == null && availableBuildCells_Worker_A == null;
            isWorkerBLocked = availableCells_Worker_B == null && availableBuildCells_Worker_B == null;

            if (isWorkerALocked && isWorkerBLocked) {
                lose(currentPlayer.getUsername());
                //you lost, notify the view
            }
        }
    }

    public void sendIslandUpdate() {
        IslandData currentIsland = board.getIsland().getIslandDataCopy();
        Gson gson = new Gson();
        String islandDataJson = gson.toJson(currentIsland);
        CV_IslandUpdateEvent islandUpdateEvent = new CV_IslandUpdateEvent("island update", islandDataJson);
        notifyAllObserverByType(VIEW, islandUpdateEvent);
    }

    /**
     * @param player
     * @author: alelisi
     * invoked by the virtual view when a next turn event is called
     */
    private void invokeNextTurn(Player player) {
        boolean hasAlreadyMovedAndBuilt = false;
        //checks if the player requesting next turn is the one that is playing the turn
        if (currentTurnInstance.getCurrentPlayer().getUsername().equals(player.getUsername())) {
            hasAlreadyMovedAndBuilt = currentTurnInstance.getNumberOfMove() > 0
                    && currentTurnInstance.getNumberOfBuild() > 0;
            if (hasAlreadyMovedAndBuilt) {
                nextTurn();
            } else {
                return; //request ignored
            }
        }
    }

    private void invokeMovement(Player player, Worker w, int x, int y) {

        if (!player.getUsername().equals(getCurrentPlayerUser())) { //if is not the player turn
            CV_GameErrorGameEvent errorEvent = new CV_GameErrorGameEvent("is not your turn!", player.getUsername());
            notifyAllObserverByType(VIEW, errorEvent);
        } else {
            //check if it's not the first time he moves / build
            if (currentTurnInstance.getNumberOfBuild() > 0 || currentTurnInstance.getNumberOfMove() > 0) {
                if (w.getWorkerID() != currentTurnInstance.getWorkerID()) {
                    CV_GameErrorGameEvent errorEvent = new CV_GameErrorGameEvent("you can move only with the same used during the turn!", player.getUsername());
                    notifyAllObserverByType(VIEW, errorEvent);
                    sendCommandRequest(player.getUsername());
                }
            }

            //is your turn, you may move:
            try {
                player.getCard().move(w, x, y, board.getIsland());
                currentTurnInstance.setNumberOfMove(currentTurnInstance.getNumberOfMove() + 1);
                currentTurnInstance.chooseWorker(w.getWorkerID());

                sendIslandUpdate();
                if (isCompletelyLocked(currentTurnInstance.getWorkerID())) {
                    lose(currentPlayer.getUsername());
                }
                sendCommandRequest(player.getUsername());
            } catch (InvalidMovementException e) {
                System.out.println(e.toString());
                e.printStackTrace();
                
                CV_GameErrorGameEvent errorEvent = new CV_GameErrorGameEvent("this is a invalid move!", player.getUsername());
                notifyAllObserverByType(VIEW, errorEvent);
                sendCommandRequest(player.getUsername());
            } catch (WinningException e) {
                win(player);
            }
        }
    }

    private void invokeBuild(Player player, Worker w, BlockTypeEnum block, int x, int y) {

        //are you sure it's your turn?
        if (!player.getUsername().equals(getCurrentPlayerUser())) {
            CV_GameErrorGameEvent errorEvent = new CV_GameErrorGameEvent("is not your turn!", player.getUsername());
            notifyAllObserverByType(VIEW, errorEvent);
        } else {
            //check if it's building with the same worker
            if (currentTurnInstance.getNumberOfBuild() > 0 || currentTurnInstance.getNumberOfMove() > 0) {
                if (w.getWorkerID() != currentTurnInstance.getWorkerID()) {
                    CV_GameErrorGameEvent errorEvent = new CV_GameErrorGameEvent("you can build only with the same used during the turn!", player.getUsername());
                    notifyAllObserverByType(VIEW, errorEvent);
                    sendCommandRequest(player.getUsername());
                }
            }
            try {
                player.getCard().build(w, block, x, y, board.getIsland());
                currentTurnInstance.setNumberOfBuild(currentTurnInstance.getNumberOfBuild() + 1);
                sendIslandUpdate();
                sendCommandRequest(player.getUsername());
            } catch (InvalidBuildException e) {
                CV_GameErrorGameEvent errorEvent = new CV_GameErrorGameEvent("this is a invalid build!", player.getUsername());
                notifyAllObserverByType(VIEW, errorEvent);
                sendCommandRequest(player.getUsername());
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException();
            }
        }
    }

    /**
     * checks if both movement and build are unavailable for choosen worker
     *
     * @param workerChosen the worker you started the turn with
     * @return true if your choosen worker can't completely move or build
     */
    private boolean isCompletelyLocked(IDs workerChosen) {

        //get available movement cells for each worker
        List<int[]> availableCells_Worker = currentTurnInstance.validActions(workerChosen, MOVE);
        List<int[]> availableCells_Worker_build = currentTurnInstance.validActions(workerChosen, BUILD);

        return (availableCells_Worker == null && availableCells_Worker_build == null);

    }

    @Override
    public void handleEvent(VC_PlayerCommandGameEvent event) {
        if (event.getFromPlayer().equals(currentPlayer.getUsername())) {
            Worker worker;
            int[] position;
            switch (event.getCommand()) {
                case MOVE:
                    worker = currentPlayer.getWorker(event.getWorkerID());
                    position = event.getPosition();
                    invokeMovement(currentPlayer, worker, position[0], position[1]);
                    break;
                case BUILD:
                    try {
                        worker = currentPlayer.getWorker(event.getWorkerID());
                        position = event.getPosition();
                        BlockTypeEnum blockToBuild;
                        if (event.isBlockSet()) {
                            blockToBuild = event.getBlockToBuild();
                        } else {
                            blockToBuild = board.getIsland().getCellCluster(position[0], position[1]).nextBlockToBuild();
                        }
                        invokeBuild(currentPlayer, worker, blockToBuild, position[0], position[1]);
                    } catch (InvalidBuildException e) {
                        CV_GameErrorGameEvent errorGameEvent = new CV_GameErrorGameEvent("Automatic block selection failed, please select a valid block", event.fromPlayer);
                        notifyAllObserverByType(VIEW, errorGameEvent);
                        sendCommandRequest(currentPlayer.getUsername());
                    }
                    break;
                case PASS:
                    invokeNextTurn(currentPlayer);
                    break;
            }
        } else {
            CV_GameErrorGameEvent errorGameEvent = new CV_GameErrorGameEvent("It's not your turn, please wait!", event.fromPlayer);
            notifyAllObserverByType(VIEW, errorGameEvent);
        }
    }


    //NOT IMPLEMENTED
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
    public void handleEvent(VC_ConnectionRequestGameEvent event) {

    }

    @Override
    public void handleEvent(CC_ConnectionRequestGameEvent event) {

    }

    @Override
    public void handleEvent(CV_RoomSizeRequestGameEvent event) {

    }

    /* TurnController doesn't have to implement this handleEvent*/
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
