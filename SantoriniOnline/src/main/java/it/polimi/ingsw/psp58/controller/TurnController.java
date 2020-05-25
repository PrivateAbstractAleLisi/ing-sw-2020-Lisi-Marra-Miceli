package it.polimi.ingsw.psp58.controller;

import com.google.gson.Gson;
import it.polimi.ingsw.psp58.auxiliary.IslandData;
import it.polimi.ingsw.psp58.event.core.ControllerListener;
import it.polimi.ingsw.psp58.event.core.EventSource;
import it.polimi.ingsw.psp58.event.gameEvents.CV_GameErrorGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.lobby.*;
import it.polimi.ingsw.psp58.event.gameEvents.match.*;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.VC_ChallengerCardsChosenEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.VC_ChallengerChosenFirstPlayerEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.VC_PlayerCardChosenEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.VC_PlayerPlacedWorkerEvent;
import it.polimi.ingsw.psp58.exceptions.InvalidBuildException;
import it.polimi.ingsw.psp58.exceptions.InvalidMovementException;
import it.polimi.ingsw.psp58.exceptions.InvalidWorkerRemovalException;
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
 * This class filters VirtualView events, it creates a turn(on it.polimi.ingsw.sp58.model) for current player and manages it
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
    public void checkIfThereIsChronus(){
        this.thereIsChronus = false;
        for (Player player : turnSequence.values()){
            if(player.getCard().getName()== CardEnum.CHRONUS) this.thereIsChronus=true;
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
        List<Player> players = new ArrayList<>();
        for (Map.Entry<Integer, Player> player : turnSequence.entrySet()) {
            players.add(player.getValue());
        }
        return players;
    }

    public void setUpAvailableMovements(BehaviourManager behaviour, List<int[]> availableMovementsA, List<int[]> availableMovementsB) {
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

    public void setUpAvailableBuild(List<int[]> availableBuildA, List<int[]> availableBuildB) {
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

    public boolean checkLose(List<TurnAction> availableActions, String player) {
        if (availableActions.isEmpty()) { //the player has no actions possibles so he loses
            lose(player);
            return true;
        }
        return false;
    }

    public boolean canPassTurn() {
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

    private void sendCommandRequest(String actingPlayer) {
        List<TurnAction> availableActions = new ArrayList<>();
        int numberOfMove = currentTurnInstance.getNumberOfMove();
        int numberOfBuild = currentTurnInstance.getNumberOfBuild();
        BehaviourManager behaviour = board.getPlayer(actingPlayer).getBehaviour();
        boolean isPrometheus = currentPlayer.getCard().getName() == CardEnum.PROMETHEUS;

        List<int[]> availableMovementsA = new ArrayList<>();
        List<int[]> availableMovementsB = new ArrayList<>();

        List<int[]> availableBuildA = new ArrayList<>();
        List<int[]> availableBuildB = new ArrayList<>();

        //Check if the player can MOVE and set the possible moves
        if ((behaviour.getMovementsRemaining() > 0) &&
                ((numberOfBuild == 0) || ((isPrometheus) && (numberOfBuild == 1)))) {//MOVE may be a valid action

            setUpAvailableMovements(behaviour, availableMovementsA, availableMovementsB);

            //if there any MOVE is actually a valid action
            if (!availableMovementsA.isEmpty() || !availableMovementsB.isEmpty()) {
                availableActions.add(MOVE);
            }
        }


        //Check if the player can BUILD and set the possible builds
        if ((behaviour.getBlockPlacementLeft() > 0) &&
                ((numberOfMove > 0) || ((isPrometheus) && (numberOfMove == 0) && (numberOfBuild == 0)))) { //BUILD may be a valid action

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
            //send the it.polimi.ingsw.sp58.event for the command selection
            CV_CommandRequestEvent requestEvent = new CV_CommandRequestEvent("this are the actions you can do", availableActions, availableBuildA, availableMovementsA,
                    availableBuildB, availableMovementsB, actingPlayer);
            notifyAllObserverByType(VIEW, requestEvent);
        }
    }

    private void nextTurn() {
        currentTurnInstance = null; //destroys current turn on it.polimi.ingsw.sp58.model
        if (this.currentTurnIndex == numberOfPlayers - 1) { //Ri inizia il giro
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
                notifyAllObserverByType(VIEW,requestEvent);
            }
        }
        sendIslandUpdate();
        sendCommandRequest(currentPlayerUsername);
        printLogMessage("New turn for " + currentPlayerUsername.toUpperCase());
    }

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

        room.setRoomMustBeCleaned(true);
        CV_NewGameRequestEvent requestEvent = new CV_NewGameRequestEvent("Do you want to play with me again?");
        notifyAllObserverByType(VIEW, requestEvent);
    }

    private void lose(String player) {
        printLogMessage(player.toUpperCase() + "LOST THE GAME");
        if (numberOfPlayers == 3) {
            //removes the player from the list
            for (Integer integer : turnSequence.keySet()) {
                if (turnSequence.get(integer).getUsername().equals(player)) {
                    turnSequence.remove(integer);
                    numberOfPlayers = 2;
                }
            }
            //removes the workers of that player from the island
            Player defeatedPlayer = board.getPlayer(player);
            if (defeatedPlayer.getCard().getName()==CardEnum.CHRONUS){
                this.thereIsChronus=false;
            }
            try {
                board.getIsland().removeWorker(defeatedPlayer.getWorker(IDs.A));
                board.getIsland().removeWorker(defeatedPlayer.getWorker(IDs.B));
            } catch (InvalidWorkerRemovalException e) {
                e.printStackTrace();
                //todo eliminare eccezione
            }

            List<String> losers = new ArrayList<String>();
            losers.add(player);
            CV_GameOverEvent gameOverEvent = new CV_GameOverEvent("lose", null, losers);
            notifyAllObserverByType(VIEW,gameOverEvent);

            removePlayerFromGame(player);
            room.setSpectator(player);
            nextTurn();
        } else if (numberOfPlayers == 2) {
            if (turnSequence.get(0).getUsername().equals(player)) { //the loser
                win(turnSequence.get(1)); //the other one wins the game
            } else {
                win(turnSequence.get(0)); //the other one wins the game
            }
        }
    }

    private void removePlayerFromGame(String usernameToRemove) {
        List<Player> players = getPlayersSequenceAsList();
        Player playerToRemove = board.getPlayer(usernameToRemove);
        players.remove(playerToRemove);
        Map<Integer, Player> newTurnSequence = new HashMap<Integer, Player>();

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
     * invoked by the virtual it.polimi.ingsw.sp58.view when a next turn it.polimi.ingsw.sp58.event is called
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

    public boolean checkIsHisTurn(Player player) {
        if (!player.getUsername().equals(getCurrentPlayerUser())) {
            CV_GameErrorGameEvent errorEvent = new CV_GameErrorGameEvent("is not your turn!", player.getUsername());
            notifyAllObserverByType(VIEW,errorEvent);
            return false;
        } else return true;
    }

    private void invokeMovement(Player player, Worker w, int x, int y) {

        if (checkIsHisTurn(player)) {
            //check if it's not the first time he moves / build, if yes check if he's using the same worker
            if ((currentTurnInstance.getNumberOfBuild() > 0 || currentTurnInstance.getNumberOfMove() > 0) && (w.getWorkerID() != currentTurnInstance.getWorkerID())) {
                CV_GameErrorGameEvent errorEvent = new CV_GameErrorGameEvent("you can move only with the same used during the turn!", player.getUsername());
                notifyAllObserverByType(VIEW,errorEvent);
                sendCommandRequest(player.getUsername());
            } else {
                //is your turn and your worker is ok, you may try to move:
                try {
                    player.getCard().move(w, x, y, board.getIsland());
                    currentTurnInstance.setNumberOfMove(currentTurnInstance.getNumberOfMove() + 1);
                    currentTurnInstance.chooseWorker(w.getWorkerID());

                    sendIslandUpdate();
                    if (isCompletelyLocked(currentTurnInstance.getWorkerID())) {
                        lose(currentPlayer.getUsername());
                    }
                    sendCommandRequest(player.getUsername());
                } catch (InvalidMovementException | IllegalArgumentException e) {
                    printErrorLogMessage(e.toString() + " - A new CommandRequest has been send.");

                    CV_GameErrorGameEvent errorEvent = new CV_GameErrorGameEvent("This is a invalid move!", player.getUsername());
                    notifyAllObserverByType(VIEW,errorEvent);
                    sendCommandRequest(player.getUsername());
                } catch (WinningException e) {
                    win(player);
                }
            }
        }
    }

    private void invokeBuild(Player player, Worker w, BlockTypeEnum block, int x, int y) {

        //are you sure it's your turn?
        if (checkIsHisTurn(player)) {
            //check if it's not the first time he moves / build, if yes check if he's using the same worker
            if ((currentTurnInstance.getNumberOfBuild() > 0 || currentTurnInstance.getNumberOfMove() > 0) && (w.getWorkerID() != currentTurnInstance.getWorkerID())) {
                CV_GameErrorGameEvent errorEvent = new CV_GameErrorGameEvent("you can build only with the same used during the turn!", player.getUsername());
                notifyAllObserverByType(VIEW,errorEvent);
                sendCommandRequest(player.getUsername());
            } else {
                //is your turn and your worker is ok, you may try build:
                try {
                    player.getCard().build(w, block, x, y, board.getIsland());
                    currentTurnInstance.setNumberOfBuild(currentTurnInstance.getNumberOfBuild() + 1);
                    if(currentTurnInstance.getNumberOfMove()==0){
                        currentTurnInstance.setHasBuiltBeforeMove(true);
                    }

                    if (currentTurnInstance.getWorkerID() == null) currentTurnInstance.chooseWorker(w.getWorkerID());

                    //if there is Chronus and this build has made the fifth complete tower Chronus wins
                    if(thereIsChronus && board.getIsland().getNumberOfCompleteTowers() >= 5){
                        Player playerHasToWin = null;
                        for (Player p : turnSequence.values()){
                            if(p.getCard().getName() == CardEnum.CHRONUS) playerHasToWin= p;
                        }
                        win(playerHasToWin);
                    }

                    sendIslandUpdate();
                    sendCommandRequest(player.getUsername());

                } catch (InvalidBuildException | IllegalArgumentException e) {
                    printErrorLogMessage(e.toString() + " - A new CommandRequest has been send.");

                    CV_GameErrorGameEvent errorEvent = new CV_GameErrorGameEvent("this is a invalid build!", player.getUsername());
                    notifyAllObserverByType(VIEW,errorEvent);
                    sendCommandRequest(player.getUsername());
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException("Clone not supported!");
                }
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

        return (availableCells_Worker.isEmpty() && availableCells_Worker_build.isEmpty());

    }

    @Override
    public void handleEvent(VC_PlayerCommandGameEvent event) {
        printLogMessage("New Command from " + event.getFromPlayer().toUpperCase() + " -> " + event.toStringSmall());

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
                            printLogMessage("Block to build found: " + blockToBuild);
                        }
                        invokeBuild(currentPlayer, worker, blockToBuild, position[0], position[1]);
                    } catch (InvalidBuildException e) {
                        CV_GameErrorGameEvent errorGameEvent = new CV_GameErrorGameEvent("Automatic block selection failed, please select a valid block", event.fromPlayer);
                        notifyAllObserverByType(VIEW, errorGameEvent);
                        sendCommandRequest(currentPlayer.getUsername());
                        printLogMessage("Block to build not found. New command request sent.");
                    }
                    break;
                case PASS:
                    invokeNextTurn(currentPlayer);
                    break;
            }
        } else {
            printLogMessage(event.fromPlayer.toUpperCase() + " is not the current player. Command rejected");
            CV_GameErrorGameEvent errorGameEvent = new CV_GameErrorGameEvent("It's not your turn, please wait!", event.fromPlayer);
            notifyAllObserverByType(VIEW, errorGameEvent);
        }
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
     * Print in the Server console Error Stream an Errror Log from the current Class
     *
     * @param messageToPrint a {@link String} with the message to print
     */
    private void printErrorLogMessage(String messageToPrint) {
        System.err.println("\t \tROOM(" + room.getRoomID() + ")-GAME: " + messageToPrint);
    }

    //NOT IMPLEMENTED

    @Override
    public void handleEvent(VC_RoomSizeResponseGameEvent event) {
        /* TurnController doesn't have to implement this handleEvent*/
    }

    @Override
    public void handleEvent(VC_NewGameResponseEvent event) {

    }


    @Override
    public void handleEvent(VC_ConnectionRequestGameEvent event) {
        /* TurnController doesn't have to implement this handleEvent*/
    }

    @Override
    public void handleEvent(CC_ConnectionRequestGameEvent event) {
        /* TurnController doesn't have to implement this handleEvent*/
    }

    @Override
    public void handleEvent(CC_NewGameResponseEvent event) {

    }


    @Override
    public void handleEvent(VC_ChallengerCardsChosenEvent event) {
        /* TurnController doesn't have to implement this handleEvent*/
    }

    @Override
    public void handleEvent(VC_PlayerCardChosenEvent event) {
        /* TurnController doesn't have to implement this handleEvent*/
    }

    @Override
    public void handleEvent(VC_ChallengerChosenFirstPlayerEvent event) {
        /* TurnController doesn't have to implement this handleEvent*/
    }



    @Override
    public void handleEvent(VC_PlayerPlacedWorkerEvent event) {
        /* TurnController doesn't have to implement this handleEvent*/
    }

}
