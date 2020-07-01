package it.polimi.ingsw.psp58.controller;

import it.polimi.ingsw.psp58.auxiliary.IslandData;
import it.polimi.ingsw.psp58.event.core.ControllerListener;
import it.polimi.ingsw.psp58.event.core.EventSource;
import it.polimi.ingsw.psp58.event.gameEvents.gamephase.CV_SpectatorGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.lobby.*;
import it.polimi.ingsw.psp58.event.gameEvents.match.*;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.VC_ChallengerCardsChosenEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.VC_ChallengerChosenFirstPlayerEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.VC_PlayerCardChosenEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.VC_PlayerPlacedWorkerEvent;
import it.polimi.ingsw.psp58.exceptions.InvalidBuildException;
import it.polimi.ingsw.psp58.exceptions.InvalidMovementException;
import it.polimi.ingsw.psp58.exceptions.WinningException;
import it.polimi.ingsw.psp58.model.*;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;
import it.polimi.ingsw.psp58.model.gamemap.Worker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.psp58.event.core.ListenerType.VIEW;
import static it.polimi.ingsw.psp58.model.TurnAction.*;
import static it.polimi.ingsw.psp58.model.gamemap.Worker.IDs;

/**
 * This class filters VirtualView events, it creates a turn(on model) for current player and manages it.
 * It also handles all the information and {@link TurnAction} related to the turn, operating with all the references it has on the model.
 */

public class TurnController extends EventSource implements ControllerListener {

    private Map<Integer, Player> turnSequence;
    private int currentTurnIndex;
    private Turn currentTurnInstance;
    private int currentTurnNumber;
    private boolean thereIsChronus;
    private int numberOfPlayers;
    private Player currentPlayer;
    private final BoardManager board;
    private final Room room;

    public TurnController(BoardManager boardManager, Map<Integer, Player> turnSequence, int numberOfPlayers, Room room) {
        this.board = boardManager;
        this.turnSequence = turnSequence;
        this.currentTurnIndex = -1;
        this.numberOfPlayers = numberOfPlayers;
        this.currentTurnNumber = 0;
        this.room = room;
        checkIfThereIsChronus();
    }

    /**
     * sets a turnSequence.
     *
     * @param turnSequence adds a [TURN_NUMBER, PLAYER] map representing for each player its position in the turn rotation
     */
    public void setTurnSequence(Map<Integer, Player> turnSequence) {
        this.turnSequence = turnSequence;
    }

    /**
     * checks if there is Chronus in the game and set the apposite boolean
     */
    private void checkIfThereIsChronus() {
        this.thereIsChronus = false;
        for (Player player : turnSequence.values()) {
            if (player.getCard().getName() == CardEnum.CHRONUS) {
                this.thereIsChronus = true;
                break;
            }
        }
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public int getCurrentTurnNumber() {
        return this.currentTurnNumber;
    }

    public String getCurrentPlayerUser() {
        return this.currentPlayer.getUsername();
    }

    public Turn getCurrentTurnInstance() {
        return currentTurnInstance;
    }

    /**
     * Called by the PreGame: start the match sending the first Events
     */
    public void firstTurn() {
        if (currentTurnNumber == 0) {
            printLogMessage("First turn is starting");
            nextTurn();
        }
    }

    /**
     * This method send a NewTurnEvent to all players, with basic info about the new turn
     */
    private void sendNewTurnEvent() {
        List<String> turnList = getCurrentTurnListUsername();
        CV_NewTurnEvent event = new CV_NewTurnEvent("is your turn!", currentPlayer.getUsername(), turnList);
        notifyAllObserverByType(VIEW, event);
    }

    /**
     * This method return a List of {@link Player} the turnSequence where the first element is the {@code currentPlayer}
     *
     * @return List of {@link Player} the turnSequence where the first element is the {@code currentPlayer}
     */
    private List<Player> getCurrentTurnList() {
        List<Player> actualTurnSequence = getPlayersSequenceAsList();

        List<Player> currentTurn = new ArrayList<>();

        int index = this.currentTurnIndex;

        while (currentTurn.size() < numberOfPlayers) {
            currentTurn.add(actualTurnSequence.get(index));
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
        List<Player> players = new ArrayList<>();
        for (Map.Entry<Integer, Player> player : turnSequence.entrySet()) {
            players.add(player.getValue());
        }
        return players;
    }

    /**
     * Adds all the available destination for both the worker A and the worker B
     *
     * @param behaviour           the {@link BehaviourManager} of the player that has to make the move
     * @param availableMovementsA all the possible destination for the worker A
     * @param availableMovementsB all the possible destination for the worker B
     */
    private void setUpAvailableMovements(BehaviourManager behaviour, List<int[]> availableMovementsA, List<int[]> availableMovementsB) {
        if (currentTurnInstance.getNumberOfMove() == 0 && currentTurnInstance.getNumberOfBuild() == 0) { // he can move with any of the two workers

            //gets the possibles move for the worker A
            availableMovementsA.addAll(currentTurnInstance.validActions(IDs.A, MOVE));
            //gets the possible moves for the worker B
            availableMovementsB.addAll(currentTurnInstance.validActions(IDs.B, MOVE));
        } else if (behaviour.getMovementsRemaining() != 0) { // the player can make a second move with the worker used for the other actions, also PROMETHEUS that has built before move

            if (currentTurnInstance.getWorkerID() == IDs.A) { //the player has moved with the worker A
                availableMovementsA.addAll(currentTurnInstance.validActions(IDs.A, MOVE));

            } else if (currentTurnInstance.getWorkerID() == IDs.B) { //the player has moved with the worker B

                availableMovementsB.addAll(currentTurnInstance.validActions(IDs.B, MOVE));
            }
        }
    }

    /**
     * Adds all the available cell in which the player can build with both the worker A and the worker B
     *
     * @param availableBuildA all the possible destination for the build of the worker A
     * @param availableBuildB all the possible destination for the build of the worker B
     */
    private void setUpAvailableBuild(List<int[]> availableBuildA, List<int[]> availableBuildB) {
        if (currentTurnInstance.getNumberOfMove() == 0) { // the player can build with any worker (ONLY PROMETHEUS)
            availableBuildA.addAll(currentTurnInstance.validActions(IDs.A, BUILD));
            availableBuildB.addAll(currentTurnInstance.validActions(IDs.B, BUILD));
        } else {
            Worker.IDs id = currentTurnInstance.getWorkerID();
            if (id == IDs.A) {
                availableBuildA.addAll(currentTurnInstance.validActions(id, BUILD));
            } else if (id == IDs.B) {
                availableBuildB.addAll(currentTurnInstance.validActions(id, BUILD));
            }
        }
    }

    /**
     * Checks, during the calculation of the possible action of the turn, if the player has no possible action and so he has to lose
     *
     * @param availableActions the list of the possible {@link TurnAction} of the player
     * @param player           the name of the player
     * @return true if he lost, false otherwise
     */
    private boolean checkLose(List<TurnAction> availableActions, String player) {
        if (availableActions.isEmpty()) { //the player has no actions possibles so he loses
            lose(player);
            return true;
        }
        return false;
    }

    /**
     * Checks if {@code MOVE} is a possible action for the phase of the turn for the {@code actingPlayer}
     *
     * @param actingPlayer the name of the player that has to make an action
     * @return true if {@code MOVE} is a possible action, false otherwise
     */
    private boolean canMove(String actingPlayer) {
        BehaviourManager behaviour = board.getPlayer(actingPlayer).getBehaviour();
        boolean isPrometheus = currentPlayer.getCard().getName() == CardEnum.PROMETHEUS;
        int numberOfBuild = currentTurnInstance.getNumberOfBuild();

        return (behaviour.getMovementsRemaining() > 0) &&
                ((numberOfBuild == 0) || ((isPrometheus) && (numberOfBuild == 1)));
    }

    /**
     * Checks if {@code BUILD} is a possible action for the phase of the turn for the {@code actingPlayer}
     *
     * @param actingPlayer the name of the player that has to make an action
     * @return true if {@code BUILD} is a possible action, false otherwise
     */
    private boolean canBuild(String actingPlayer) {
        BehaviourManager behaviour = board.getPlayer(actingPlayer).getBehaviour();
        boolean isPrometheus = currentPlayer.getCard().getName() == CardEnum.PROMETHEUS;
        int numberOfMove = currentTurnInstance.getNumberOfMove();
        int numberOfBuild = currentTurnInstance.getNumberOfBuild();

        return (behaviour.getBlockPlacementLeft() > 0) &&
                ((numberOfMove > 0) || ((isPrometheus) && (numberOfMove == 0) && (numberOfBuild == 0)));
    }

    /**
     * Checks if {@code PASS} is a possible action for the phase of the turn for the {@code actingPlayer}
     *
     * @return true if {@code PASS} is a possible action, false otherwise
     */
    private boolean canPassTurn() {
        boolean isPrometheus = currentPlayer.getCard().getName() == CardEnum.PROMETHEUS;
        if (isPrometheus) {
            //if is Prometheus, check more things
            boolean hasBuiltBeforeMove = currentTurnInstance.getHasBuiltBeforeMove();
            boolean normalValidTurn = currentTurnInstance.getNumberOfBuild() == 1 && !hasBuiltBeforeMove;
            boolean powerValidTurn = currentTurnInstance.getNumberOfBuild() == 2 && hasBuiltBeforeMove;

            return (currentTurnInstance.getNumberOfMove() == 1 && (normalValidTurn || powerValidTurn));
        }
        return (currentTurnInstance.getNumberOfMove() > 0 && currentTurnInstance.getNumberOfBuild() > 0);
    }

    /**
     * Sends to the {@code actingPlayer} a {@link CV_CommandExecutedGameEvent} event to let the client know that the command has been executed correctly
     *
     * @param actingPlayer the recipient for the event
     */
    private void sendCommandExecuted(String actingPlayer) {
        CV_CommandExecutedGameEvent event = new CV_CommandExecutedGameEvent("", actingPlayer);
        notifyAllObserverByType(VIEW, event);
    }

    /**
     * Sends a {@link CV_CommandRequestEvent} to the {@code actingPlayer} containing all the possible {@link TurnAction}, movements and build that he can perform.
     * If there aren't it calls the {@code lose} method because the {@code actingPlayer} has to lose the game
     *
     * @param actingPlayer the name of the player that has to perform an action
     */
    private void sendCommandRequest(String actingPlayer) {
        List<TurnAction> availableActions = new ArrayList<>();
        BehaviourManager behaviour = board.getPlayer(actingPlayer).getBehaviour();

        List<int[]> availableMovementsA = new ArrayList<>();
        List<int[]> availableMovementsB = new ArrayList<>();

        List<int[]> availableBuildA = new ArrayList<>();
        List<int[]> availableBuildB = new ArrayList<>();

        //Check if the player can MOVE and set the possible moves
        if (canMove(actingPlayer)) {//MOVE may be a valid action

            setUpAvailableMovements(behaviour, availableMovementsA, availableMovementsB);

            //if there any MOVE is actually a valid action
            if (!availableMovementsA.isEmpty() || !availableMovementsB.isEmpty()) {
                availableActions.add(MOVE);
            }
        }


        //Check if the player can BUILD and set the possible builds
        if (canBuild(actingPlayer)) { //BUILD may be a valid action

            setUpAvailableBuild(availableBuildA, availableBuildB);

            //if there any BUILD is actually a valid action
            if (!availableBuildA.isEmpty() || !availableBuildB.isEmpty()) {
                availableActions.add(BUILD);
            }
        }

        if (canPassTurn()) { //the player can terminate the turn
            availableActions.add(PASS);
        }

        //checks if the player loses or not
        if (!checkLose(availableActions, actingPlayer)) {
            //send info of the turn
            CV_TurnInfoEvent infoEvent = new CV_TurnInfoEvent("", behaviour.getMovementsRemaining(), behaviour.getBlockPlacementLeft(), behaviour.isCanClimb(), actingPlayer);
            notifyAllObserverByType(VIEW, infoEvent);
            //send the event for the command selection
            CV_CommandRequestEvent requestEvent = new CV_CommandRequestEvent("this are the actions you can do", availableActions, availableBuildA, availableMovementsA,
                    availableBuildB, availableMovementsB, actingPlayer);
            notifyAllObserverByType(VIEW, requestEvent);
        }
    }

    /**
     * Method called when a player perform a {@code pass} action and create a new {@link Turn} instance for the next player in the {@code turnSequence}.
     * Then notify the actingPlayer calling the {@code sendCommandRequest} method.
     */
    private void nextTurn() {
        currentTurnInstance = null; //destroys current turn on model
        if (this.currentTurnIndex == numberOfPlayers - 1) {
            this.currentTurnIndex = 0;
        } else {
            this.currentTurnIndex++;
        }

        currentPlayer = turnSequence.get(currentTurnIndex);
        currentTurnInstance = new Turn(currentPlayer, board);
        currentTurnNumber++;

        //Notify All Players
        String currentPlayerUsername = currentPlayer.getUsername();
        List<Player> players = getPlayersSequenceAsList();

        sendNewTurnEvent();

        for (Player recipient : players) {
            if (!recipient.getUsername().equals(currentPlayerUsername)) {
                CV_WaitMatchGameEvent requestEvent = new CV_WaitMatchGameEvent("Is the turn of", currentPlayerUsername, recipient.getUsername());
                notifyAllObserverByType(VIEW, requestEvent);
            }
        }
        sendIslandUpdate();
        sendCommandRequest(currentPlayerUsername);
        printLogMessage("New turn for " + currentPlayerUsername.toUpperCase());
    }

    /**
     * Sends the {@link CV_GameOverEvent} and then a {@link CV_NewGameRequestEvent} to all the players
     *
     * @param winner the name of the player that has won
     */
    private void win(Player winner) {
        List<String> losingPlayers = new ArrayList<>();
        for (Map.Entry<Integer, Player> player : turnSequence.entrySet()) {
            if (!player.getValue().getUsername().equals(winner.getUsername())) {
                losingPlayers.add(player.getValue().getUsername());
            }
        }
        CV_GameOverEvent gameOverEvent = new CV_GameOverEvent("lose", winner.getUsername(), losingPlayers);
        notifyAllObserverByType(VIEW, gameOverEvent);
        printLogMessage(winner.getUsername().toUpperCase() + "IS THE WINNER");

        room.setRoomMustBeCleanedTrue();
        CV_NewGameRequestEvent requestEvent = new CV_NewGameRequestEvent("Do you want to play with me again?");
        notifyAllObserverByType(VIEW, requestEvent);
    }

    /**
     * Removes the {@code player} from the {@code turnSequence}, decrements the {@code numberOfPlayers}, removes his worker from the island, sends a {@link CV_GameOverEvent} to the loser.
     * If is a two player match let the other player win, if is a three player one the losing player becomes a spectator.
     *
     * @param player the name of the player that lost the game
     */
    private void lose(String player) {
        printLogMessage(player.toUpperCase() + "LOST THE GAME");
        if (numberOfPlayers == 3) {
            //removes the player from the list
            Integer playerIndex = 0;
            for (Integer integer : turnSequence.keySet()) {
                if (turnSequence.get(integer).getUsername().equals(player)) {
                    playerIndex = integer;
                }
            }
            turnSequence.remove(playerIndex);
            numberOfPlayers = 2;

            //removes the workers of that player from the island
            Player defeatedPlayer = board.getPlayer(player);
            if (defeatedPlayer.getCard().getName() == CardEnum.CHRONUS) {
                this.thereIsChronus = false;
            }
            board.getIsland().removeWorker(defeatedPlayer.getWorker(IDs.A));
            board.getIsland().removeWorker(defeatedPlayer.getWorker(IDs.B));

            List<String> losers = new ArrayList<>();
            losers.add(player);
            CV_GameOverEvent gameOverEvent = new CV_GameOverEvent("lose", null, losers);
            notifyAllObserverByType(VIEW, gameOverEvent);

            removePlayerFromGame(player);
            room.setSpectator(player);
            CV_SpectatorGameEvent spectatorGameEvent = new CV_SpectatorGameEvent("", player);
            notifyAllObserverByType(VIEW, spectatorGameEvent);
            nextTurn();
        } else if (numberOfPlayers == 2) {
            if (turnSequence.get(0).getUsername().equals(player)) { //the loser
                win(turnSequence.get(1)); //the other one wins the game
            } else {
                win(turnSequence.get(0)); //the other one wins the game
            }
        }
    }

    /**
     * Removes a {@link Player} from the {@code turnSequence} remaking a new one if there are others players in the game
     *
     * @param usernameToRemove the name of the player to remove
     */
    private void removePlayerFromGame(String usernameToRemove) {
        List<Player> players = getPlayersSequenceAsList();
        Player playerToRemove = board.getPlayer(usernameToRemove);
        players.remove(playerToRemove);
        Map<Integer, Player> newTurnSequence = new HashMap<>();

        for (int i = 0; i < players.size(); i++) {
            newTurnSequence.put(i, players.get(i));
        }

        setTurnSequence(newTurnSequence);
        setNumberOfPlayers(players.size());
        if (currentTurnIndex == 0) {
            currentTurnIndex = numberOfPlayers - 1;
        } else {
            currentTurnIndex--;
        }
    }

    /**
     * Sends a {@link CV_IslandUpdateEvent} with updated island, after an action has been performed, to all the players
     */
    private void sendIslandUpdate() {
        IslandData currentIsland = board.getIsland().getIslandDataCopy();
        CV_IslandUpdateEvent islandUpdateEvent = new CV_IslandUpdateEvent("island update", currentIsland);
        notifyAllObserverByType(VIEW, islandUpdateEvent);
    }

    /**
     * invoked by the virtual view when a next turn event is called
     * @param player player that is requiring the next turn
     *
     */
    private void invokeNextTurn(Player player) {
        //checks if the player requesting next turn is the one that is playing the turn
        if (currentTurnInstance.getCurrentPlayer().getUsername().equals(player.getUsername())) {
            if (canPassTurn()) {
                player.getCard().resetBehaviour();
                nextTurn();
            } else {
                CV_GameErrorGameEvent errorEvent = new CV_GameErrorGameEvent("you can't pass turn!", player.getUsername());
                notifyAllObserverByType(VIEW, errorEvent);
                sendCommandRequest(player.getUsername());
            }
        }
    }

    /**
     * Checks if is the turn of the {@code player}
     *
     * @param player the player on which the method check if is his turn
     * @return true if is the turn of the {@code player}, false otherwise
     */
    private boolean checkIsHisTurn(Player player) {
        if (!player.getUsername().equals(getCurrentPlayerUser())) {
            CV_GameErrorGameEvent errorEvent = new CV_GameErrorGameEvent("is not your turn!", player.getUsername());
            notifyAllObserverByType(VIEW, errorEvent);
            return false;
        } else return true;
    }

    /**
     * @param worker the worker to check if is the same of the one used before, if the player has already made a move
     * @return true if the worker passed as argument is the worker used in the prior moves, false if is different or if it is the first move of the turn
     */
    private boolean checkIsUsingTheSameWorker(Worker worker) {
        return (currentTurnInstance.getNumberOfBuild() > 0 || currentTurnInstance.getNumberOfMove() > 0) && (worker.getWorkerID() != currentTurnInstance.getWorkerID());
    }

    /**
     * Performs the actual move. sends a {@link CV_CommandExecutedGameEvent} and the next {@link CV_CommandRequestEvent} calling the {@code sendCommandRequest} method
     *
     * @param player the player that makes the move
     * @param worker the worker he his using to make this move
     * @param x      the x position where the player wants to move
     * @param y      the y position where the player wants to move
     * @throws InvalidMovementException if the player can't perform that move
     * @throws WinningException         if the player has won
     */
    private void performMove(Player player, Worker worker, int x, int y) throws InvalidMovementException, WinningException {
        player.getCard().move(worker, x, y, board.getIsland());
        currentTurnInstance.incrementNumberOfMove();
        currentTurnInstance.chooseWorker(worker.getWorkerID());

        sendIslandUpdate();
        if (isCompletelyLocked(currentTurnInstance.getWorkerID())) {
            lose(currentPlayer.getUsername());
        }
        sendCommandExecuted(player.getUsername());
        sendCommandRequest(player.getUsername());
    }

    /**
     * Checks if the player can actually move and if he calls the {@code performMove} method, otherwise sends an {@link CV_GameErrorGameEvent}
     *
     * @param player the player that makes the move
     * @param worker the worker he his using to make this move
     * @param x      the x position where the player wants to move
     * @param y      the y position where the player wants to move
     */
    private void invokeMovement(Player player, Worker worker, int x, int y) {

        if (checkIsHisTurn(player)) {
            //check if it's not the first time he moves / build, if yes check if he's using the same worker
            if (checkIsUsingTheSameWorker(worker)) {
                CV_GameErrorGameEvent errorEvent = new CV_GameErrorGameEvent("you can move only with the same used during the turn!", player.getUsername());
                notifyAllObserverByType(VIEW, errorEvent);
                sendCommandRequest(player.getUsername());
            } else {
                //is your turn and your worker is ok, you may try to move:
                try {
                    performMove(player, worker, x, y);
                } catch (InvalidMovementException | IllegalArgumentException e) {
                    printErrorLogMessage(e.toString() + " - A new CommandRequest has been send.");

                    CV_GameErrorGameEvent errorEvent = new CV_GameErrorGameEvent("This is a invalid move!", player.getUsername());
                    notifyAllObserverByType(VIEW, errorEvent);
                    sendCommandRequest(player.getUsername());
                } catch (WinningException e) {
                    sendIslandUpdate();
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    win(player);
                }
            }
        }
    }

    /**
     * Performs the actual build
     *
     * @param player the player that makes the build
     * @param worker the worker he his using to make this build
     * @param block  the blockTypeEnum that the player wants to build
     * @param x      the x position where the player wants to build
     * @param y      the y position where the player wants to build
     */
    private void performBuild(Player player, Worker worker, BlockTypeEnum block, int x, int y) {
        try {
            player.getCard().build(worker, block, x, y, board.getIsland());
            currentTurnInstance.incrementNumberOfBuild();
            if (currentTurnInstance.getNumberOfMove() == 0) {
                currentTurnInstance.setHasBuiltBeforeMove(true);
            }

            if (currentTurnInstance.getWorkerID() == null) currentTurnInstance.chooseWorker(worker.getWorkerID());

            sendIslandUpdate();

            //if there is Chronus and this build has made the fifth complete tower Chronus wins
            if (thereIsChronus && board.getIsland().getNumberOfCompleteTowers() >= 5) {
                Player playerHasToWin = null;
                for (Player p : turnSequence.values()) {
                    if (p.getCard().getName() == CardEnum.CHRONUS) playerHasToWin = p;
                }
                win(playerHasToWin);
            }
            sendCommandExecuted(player.getUsername());
            sendCommandRequest(player.getUsername());

        } catch (InvalidBuildException | IllegalArgumentException e) {
            printErrorLogMessage(e.toString() + " - A new CommandRequest has been send.");

            CV_GameErrorGameEvent errorEvent = new CV_GameErrorGameEvent("this is a invalid build!", player.getUsername());
            notifyAllObserverByType(VIEW, errorEvent);
            sendCommandRequest(player.getUsername());
        }
    }

    /**
     * Checks if the player can actually build and if he can calls the {@code performBuild} method, otherwise sends an {@link CV_GameErrorGameEvent}
     *
     * @param player the player that makes the build
     * @param worker the worker he his using to make this build
     * @param block  the blockTypeEnum that the player wants to build
     * @param x      the x position where the player wants to build
     * @param y      the y position where the player wants to build
     */
    private void invokeBuild(Player player, Worker worker, BlockTypeEnum block, int x, int y) {
        if (checkIsHisTurn(player)) {
            //check if it's not the first time he moves / build, if yes check if he's using the same worker
            if ((currentTurnInstance.getNumberOfBuild() > 0 || currentTurnInstance.getNumberOfMove() > 0) && (worker.getWorkerID() != currentTurnInstance.getWorkerID())) {
                CV_GameErrorGameEvent errorEvent = new CV_GameErrorGameEvent("you can build only with the same used during the turn!", player.getUsername());
                notifyAllObserverByType(VIEW, errorEvent);
                sendCommandRequest(player.getUsername());
            } else {
                //is your turn and your worker is ok, you may try build:
                performBuild(player, worker, block, x, y);
            }
        }
    }

    /**
     * checks if both movement and build are unavailable for chosen worker
     *
     * @param workerChosen the worker you started the turn with
     * @return true if your choosen worker can't completely move or build
     */
    private boolean isCompletelyLocked(IDs workerChosen) {

        //get available movement cells for each worker
        List<int[]> availableMovements = currentTurnInstance.validActions(workerChosen, MOVE);
        List<int[]> availableBuild = currentTurnInstance.validActions(workerChosen, BUILD);

        return (availableMovements.isEmpty() && availableBuild.isEmpty());
    }

    /**
     * Checks the block to build and calls the {@code invokeBuild} if it is possible, sends error messages otherwise
     *
     * @param event the {@link VC_PlayerCommandGameEvent} just arrived from the acting player of the turn
     */
    private void checkBuildCorrectness(VC_PlayerCommandGameEvent event) {
        Worker worker;
        int[] position;
        try {
            worker = currentPlayer.getWorker(event.getWorkerID());
            position = event.getPosition();
            BlockTypeEnum blockToBuild;

            if (event.isBlockSet()) {
                blockToBuild = event.getBlockToBuild();
            } else {
                blockToBuild = board.getIsland().getCellCluster(position[0], position[1]).nextBlockToBuild();
                printLogMessage("Block to build found: " + blockToBuild);
            }
            invokeBuild(currentPlayer, worker, blockToBuild, position[0], position[1]);
        } catch (InvalidBuildException e) {
            CV_GameErrorGameEvent errorGameEvent = new CV_GameErrorGameEvent("Automatic block selection failed, please select a valid block", event.fromPlayer);
            notifyAllObserverByType(VIEW, errorGameEvent);
            sendCommandRequest(currentPlayer.getUsername());
            printLogMessage("Block to build not found. New command request sent.");
        }
    }

    /**
     * Parses the {@link VC_PlayerCommandGameEvent} just received by the client, checks his correctness and eventually performs the action.
     * Sends error messages otherwise.
     *
     * @param event the event containing the command to compute
     */
    @Override
    public void handleEvent(VC_PlayerCommandGameEvent event) {
        printLogMessage("New Command from " + event.getFromPlayer().toUpperCase() + " -> " + event.toStringSmall());

        try {
            if (isCommandEventValid(event)) {
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
                            checkBuildCorrectness(event);
                            break;
                        case PASS:
                            invokeNextTurn(currentPlayer);
                            break;
                    }
                } else {
                    //if the player who sent the command is not the currentPlayer
                    printErrorLogMessage(event.fromPlayer.toUpperCase() + " is not the current player. Command rejected");
                    CV_GameErrorGameEvent errorGameEvent = new CV_GameErrorGameEvent("It's not your turn, please wait!", event.fromPlayer);
                    notifyAllObserverByType(VIEW, errorGameEvent);
                }
            } else {
                //if the command is not well formatted send an error
                printErrorLogMessage("Some error in the command event: " + event);
                if (event.fromPlayer != null) {
                    CV_GameErrorGameEvent errorGameEvent = new CV_GameErrorGameEvent("The command request wasn't valid. Please retry.", event.fromPlayer);
                    notifyAllObserverByType(VIEW, errorGameEvent);
                    sendCommandRequest(event.fromPlayer);
                } else {
                    printErrorLogMessage("Not able to send events to this player because is Unknown");
                }
            }
        } catch (NullPointerException e) {
            printErrorLogMessage("Some error in the command event: " + event);
            CV_GameErrorGameEvent errorGameEvent = new CV_GameErrorGameEvent("The command request wasn't valid. Please retry.", event.fromPlayer);
            notifyAllObserverByType(VIEW, errorGameEvent);
            sendCommandRequest(event.fromPlayer);
        }
    }

    /**
     * Checks if the {@link VC_PlayerCommandGameEvent} received by the client is a correct and complete one
     *
     * @param commandEvent the {@link VC_PlayerCommandGameEvent} that has just received by the client
     * @return true if the command is correct and complete, false otherwise
     */
    private boolean isCommandEventValid(VC_PlayerCommandGameEvent commandEvent) {
        return commandEvent.isCommandEventValid() && getCurrentTurnListUsername().contains(commandEvent.getFromPlayer());
    }

    /**
     * Print in the Server console a Log from the current Class
     *
     * @param messageToPrint a {@link String} with the message to print
     */
    private void printLogMessage(String messageToPrint) {
        System.out.println("\t \tROOM(" + room.getRoomID() + ")-GAME: " + messageToPrint);
    }

    /**
     * Print in the Server console Error Stream an Error Log from the current Class
     *
     * @param messageToPrint a {@link String} with the message to print
     */
    private void printErrorLogMessage(String messageToPrint) {
        System.err.println("\t \tROOM(" + room.getRoomID() + ")-GAME: " + messageToPrint);
    }

    //NOT IMPLEMENTED

    /**
     * Not implemented handle of the event
     */
    @Override
    public void handleEvent(VC_RoomSizeResponseGameEvent event) {
        /* TurnController doesn't have to implement this handleEvent*/
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


    /**
     * Not implemented handle of the event
     */
    @Override
    public void handleEvent(VC_ChallengerCardsChosenEvent event) {
        /* This class has not to implement this handleEvent*/
    }

    /**
     * Not implemented handle of the event
     */
    @Override
    public void handleEvent(VC_PlayerCardChosenEvent event) {
        /* This class has not to implement this handleEvent*/
    }

    /**
     * Not implemented handle of the event
     */
    @Override
    public void handleEvent(VC_ChallengerChosenFirstPlayerEvent event) {
        /* This class has not to implement this handleEvent*/
    }


    /**
     * Not implemented handle of the event
     */
    @Override
    public void handleEvent(VC_PlayerPlacedWorkerEvent event) {
        /* This class has not to implement this handleEvent*/
    }

}
