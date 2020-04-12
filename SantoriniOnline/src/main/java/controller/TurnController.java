package controller;

import model.BoardManager;
import model.Player;
import model.Turn;
import model.exception.InvalidBuildException;
import model.exception.InvalidMovementException;
import model.exception.WinningException;
import model.gamemap.BlockTypeEnum;
import model.gamemap.Worker;

import java.util.List;
import java.util.Map;

import static model.TurnAction.BUILD;
import static model.TurnAction.MOVE;
import static model.gamemap.Worker.IDs;

/**
 * This class filters VirtualView events, it creates a turn(on model) for current player and manages it
 *
 * @author alelisi
 */
//TODO complete the implementation when model turn class is ready
public class TurnController {

    private Map<Integer, Player> turnSequence;
    private int currentTurnIndex;
    private Turn currentTurnIstance;

    private int numberOfPlayers;
    private Player currentPlayer;
    private BoardManager board;

    public TurnController(Map<Integer, Player> turnSequence, int numberOfPlayers) {
        this.turnSequence = turnSequence;
        this.currentTurnIndex = -1;
        this.numberOfPlayers = numberOfPlayers;
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

    private void nextTurn() {

        currentTurnIstance = null; //destroys current turn on model
        if (this.currentTurnIndex == numberOfPlayers - 1) { //Ri inizia il giro
            this.currentTurnIndex = 0;

        } else {
            this.currentTurnIndex++;
        }

        currentPlayer = turnSequence.get(currentTurnIndex);
        handleCurrentTurn();

    }

    //TODO
    private void win(Player p) {
        //Fa vincere P
    }

    //TODO
    private void loose(Player p) {

        if (numberOfPlayers == 3) {
            //removes the player from the list
            for (Integer integer : turnSequence.keySet()) {
                if (turnSequence.get(integer).getUsername() == p.getUsername()) {
                    turnSequence.remove(integer);
                    numberOfPlayers = 2;
                }
            }
        } else if (numberOfPlayers == 2) {
            //perde direttamente
            if (turnSequence.get(0).getUsername() == p.getUsername()) { //the looser
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
        currentTurnIstance = new Turn(currentPlayer, board);


        //current player might play with Prometheus

        boolean isPrometheus;
        isPrometheus = currentPlayer.getCard().getName().toUpperCase().equals("Prometheus".toUpperCase());

        if (!isPrometheus) {

            System.out.println("DEBUG: he/her's not playing with Prometheus");

            Worker wa = currentPlayer.getWorker(IDs.A);
            Worker wb = currentPlayer.getWorker(IDs.B);

            boolean isWorkerALocked, isWorkerBLocked;

            //Checks if it's able to perform at least one movement


            //get available movement cells for each worker
            List<int[]> availableCells_Worker_A = currentTurnIstance.validActions(IDs.A, MOVE);
            List<int[]> availableCells_Worker_B = currentTurnIstance.validActions(IDs.B, MOVE);
            isWorkerALocked = availableCells_Worker_A.size() == 0;
            isWorkerBLocked = availableCells_Worker_B.size() == 0;

            if (isWorkerALocked && isWorkerBLocked) {
                loose(currentPlayer);
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
        if (currentTurnIstance.getCurrentPlayer().getUsername().equals(player.getUsername())) {
            hasAlreadyMovedAndBuilt = currentTurnIstance.isHasAlreadyMoved()
                    && currentTurnIstance.isHasAlreadyBuild();
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
            if (currentTurnIstance.isHasAlreadyBuild() || currentTurnIstance.isHasAlreadyMoved()) {
                if (w.getWorkerID() != currentTurnIstance.getWorkerID()) {
                    //TODO send event to vv : you must move with your previous...
                }
            }

            //is your turn, you may move:
            try {
                player.getCard().move(w, x, y, board.getIsland());
                currentTurnIstance.setHasAlreadyMoved(true);
                currentTurnIstance.chooseWorker(w.getWorkerID());
                if (isCompletelyLocked(currentTurnIstance.getWorkerID())) {
                    loose(currentPlayer);
                }

            } catch (InvalidMovementException e) {
                //manda errore alla view
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
            if (currentTurnIstance.isHasAlreadyBuild() || currentTurnIstance.isHasAlreadyMoved()) {
                if (w.getWorkerID() != currentTurnIstance.getWorkerID()) {
                    //TODO send event to vv : you must build with the same worker...
                }
            }

            try {
                player.getCard().build(w, block, x, y, board.getIsland());
                currentTurnIstance.setHasAlreadyBuild(true);
            } catch (InvalidBuildException e) {
                //Manda errore alla view
            } catch (CloneNotSupportedException e) {
                //ERRORE GRAVE del codice
            }

        }
    }

    public void placeInvoke(Player player, Worker w, int x, int y) {
        try {
            player.getCard().placeWorker(w, x, y, board.getIsland());
        } catch (CloneNotSupportedException e) {
            //ERRORE GRAVE CODICE
        } catch (InvalidMovementException e) {
            //NON POSSO PIAZZARE
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
        List<int[]> availableCells_Worker = currentTurnIstance.validActions(workerChoosen, MOVE);
        List<int[]> availableCells_Worker_build = currentTurnIstance.validActions(workerChoosen, BUILD);

        return (availableCells_Worker.size() == 0 && availableCells_Worker_build.size() == 0);

    }

}
