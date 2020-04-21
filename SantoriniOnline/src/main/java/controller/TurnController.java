package controller;

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

    public void firstTurn() {
        if (currentTurnNumber == 0) {
            currentTurnIndex = 0;
            String firstPlayer = turnSequence.get(currentTurnIndex).getUsername();

            List<Player> players = new ArrayList<Player>();
            for (Map.Entry<Integer, Player> player : turnSequence.entrySet()) {
                players.add(player.getValue());
            }
            //todo fare sequenza

            CV_NewTurnEvent event = new CV_NewTurnEvent("is your turn!", players, firstPlayer);
            notifyAllObserverByType(ListenerType.VIEW, event);


            for (Player recipient : players) {
                if (!recipient.getUsername().equals(firstPlayer)) {
                    CV_WaitGameEvent requestEvent = new CV_WaitGameEvent("Is the turn of", firstPlayer, recipient.getUsername());
                    notifyAllObserverByType(VIEW, requestEvent);
                }
            }
            sendCommandRequest(firstPlayer);
        }

    }

    public void sendNewTurnEvent(){
        //todo
    }


    public void sendCommandRequest(String actingPlayer) {
        List<TurnAction> availableActions = new ArrayList<TurnAction>();
        int numberOfMove = currentTurnInstance.getNumberOfMove();
        int numberOfBuild = currentTurnInstance.getNumberOfMove();
        BehaviourManager behaviour = board.getPlayer(actingPlayer).getBehaviour();
        boolean isPrometheus = currentPlayer.getCard().getName() == CardEnum.PROMETHEUS;

        List<int[]> availableMovementBlocksA = new ArrayList<int[]>();
        List<int[]> availableMovementBlocksB = new ArrayList<int[]>();

        List<int[]> availableBuildBlocksA = new ArrayList<int[]>();
        List<int[]> availableBuildBlocksB = new ArrayList<int[]>();

        if (behaviour.getMovementsRemaining() != 0 &&
                (numberOfBuild == 0 || ((isPrometheus) && (numberOfBuild == 1)))) {//MOVE is a valid action
            availableActions.add(MOVE);
            if (numberOfMove == 0 && numberOfBuild == 0) { // he can move with any of the two workers

                //gets the possibles move for the worker A
                availableMovementBlocksA.addAll(currentTurnInstance.validActions(IDs.A, MOVE));
                //gets the possible moves for the worker B
                availableMovementBlocksA.addAll(currentTurnInstance.validActions(IDs.B, MOVE));
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
        if (behaviour.getBlockPlacementLeft() != 0 &&
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
        if ((numberOfMove > 0 && numberOfBuild > 0) ||
                (isPrometheus &&(numberOfMove == 1 && numberOfBuild >= 1))) { //the player can terminate the turn
            availableActions.add(PASS);
        }
        if (availableActions.isEmpty()){ //the player has no actions possibles so he loses
            lose(actingPlayer);
        }
        CV_CommandRequestEvent requestEvent = new CV_CommandRequestEvent("this are the actions you can do", availableActions, availableBuildBlocksA, availableMovementBlocksA,
            availableBuildBlocksB, availableMovementBlocksB, actingPlayer);
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
    }

    //TODO
    private void win(Player winner) {
        List<String> losingPlayers = new ArrayList<>();
        for (Map.Entry<Integer, Player> player : turnSequence.entrySet()) {
            if (!player.getValue().getUsername().equals(winner.getUsername())) {
                losingPlayers.add(player.getValue().getUsername());
            }
        }
        CV_GameOverEvent gameOverEvent = new CV_GameOverEvent("lose",winner.getUsername(), losingPlayers);
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

        if (!isPrometheus) {

            System.out.println("DEBUG: he/her's not playing with Prometheus");

            Worker wa = currentPlayer.getWorker(IDs.A);
            Worker wb = currentPlayer.getWorker(IDs.B);

            boolean isWorkerALocked, isWorkerBLocked;

            //Checks if it's able to perform at least one movement


            //get available movement cells for each worker
            List<int[]> availableCells_Worker_A = currentTurnInstance.validActions(IDs.A, MOVE);
            List<int[]> availableCells_Worker_B = currentTurnInstance.validActions(IDs.B, MOVE);
            isWorkerALocked = availableCells_Worker_A.size() == 0;
            isWorkerBLocked = availableCells_Worker_B.size() == 0;

            if (isWorkerALocked && isWorkerBLocked) {
                lose(currentPlayer);
                //you lost, notify the view
            }
            //you can move at least one time.

            //TODO
        } else if (isPrometheus) {

            System.out.println("DEBUG: is playing with prom.");
        }

        //attende eventi
    }

    /**
     * @param player
     * @author: alelisi
     * invoked by the virtual view when a next turn event is called
     */
    public void invokeNextTurn(Player player) {
        boolean hasAlreadyMovedAndBuilt = false;
        //checks if the player requesting next turn is the one that is playing the turn
        if (currentTurnInstance.getCurrentPlayer().getUsername().equals(player.getUsername())) {
            hasAlreadyMovedAndBuilt = currentTurnInstance.getNumberOfMove()
                    && currentTurnInstance.getNumberOfBuild();
            if (hasAlreadyMovedAndBuilt) {
                nextTurn();
            } else {
                return; //request ignored
            }
        }

        return; //request ignored

    }

    public void invokeMovement(Player player, Worker w, int x, int y) {

        //is your turn?
        if (!player.getUsername().equals(getCurrentPlayerUser())) {
            //TODO send event to vv : it's not your turn
            return;
        } else {

            //check if it's not the first time he moves / build
            if (currentTurnInstance.getNumberOfBuild() || currentTurnInstance.getNumberOfMove()) {
                if (w.getWorkerID() != currentTurnInstance.getWorkerID()) {
                    //TODO send event to vv : you must move with your previous...
                }
            }

            //is your turn, you may move:
            try {
                player.getCard().move(w, x, y, board.getIsland());
                currentTurnInstance.setNumberOfMove(true);
                currentTurnInstance.chooseWorker(w.getWorkerID());
                if (isCompletelyLocked(currentTurnInstance.getWorkerID())) {
                    lose(currentPlayer);
                }

            } catch (InvalidMovementException e) {
                //todo manda errore alla view
                e.printStackTrace();
            } catch (WinningException e) {
                win(player);
            }

        }

    }

    public void invokeBuild(Player player, Worker w, BlockTypeEnum block, int x, int y) {

        //are you sure it's your turn?
        if (!player.getUsername().equals(getCurrentPlayerUser())) {
            return;
        } else {


            //check if it's building with the same worker
            if (currentTurnInstance.getNumberOfBuild() || currentTurnInstance.getNumberOfMove()) {
                if (w.getWorkerID() != currentTurnInstance.getWorkerID()) {
                    //TODO send event to vv : you must build with the same worker...
                }
            }

            try {
                player.getCard().build(w, block, x, y, board.getIsland());
                currentTurnInstance.setNumberOfBuild(true);
            } catch (InvalidBuildException e) {
                //todo Manda errore alla view
                e.printStackTrace();
            } catch (CloneNotSupportedException e) {
                //todo ERRORE GRAVE del codice
                e.printStackTrace();
            }

        }
    }

    /**
     * checks if both movement and build are unavailable for choosen worker
     *
     * @param workerChoosen the worker you started the turn with
     * @return true if your choosen worker can't completely move or build
     */
    private boolean isCompletelyLocked(IDs workerChoosen) {

        boolean isWorkerLocked, isWorkerBuildLocked;
        //get available movement cells for each worker
        List<int[]> availableCells_Worker = currentTurnInstance.validActions(workerChoosen, MOVE);
        List<int[]> availableCells_Worker_build = currentTurnInstance.validActions(workerChoosen, BUILD);

        return (availableCells_Worker.size() == 0 && availableCells_Worker_build.size() == 0);

    }

    @Override
    public void handleEvent(VC_PlayerCommandGameEvent event) {
        /**
         * il metodo riceve un evento con:
         * - il tipo di comando (enum)
         * - il player che lo ha generato
         * - il body del comando
         * - la posizione desiderata
         * - il worker ID utilizzato
         *
         * il metodo deve in ordine: verificare che il player sia il suo turno altrimenti lancia un errore
         * che il player possa fare quella mossa?
         *
         * prendere le informazioni contenute nel body, position e worker ed eventualmente richiedere al
         * model le info necessarie per chiamare i metodi Invoke
         *
         * se il comando è next turn allora deve controllare che abbia svolto i movimenti necessari prima
         * di passare il turno
         *
         */
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
    public void handleEvent(MV_IslandUpdateEvent event) {

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
