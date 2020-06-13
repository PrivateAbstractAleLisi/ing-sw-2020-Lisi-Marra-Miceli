package it.polimi.ingsw.psp58.model.gods;

import it.polimi.ingsw.psp58.exceptions.InvalidMovementException;
import it.polimi.ingsw.psp58.exceptions.WinningException;
import it.polimi.ingsw.psp58.model.BehaviourManager;
import it.polimi.ingsw.psp58.model.Card;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.model.Player;
import it.polimi.ingsw.psp58.model.gamemap.CellCluster;
import it.polimi.ingsw.psp58.model.gamemap.Island;
import it.polimi.ingsw.psp58.model.gamemap.Worker;

import java.util.List;

/**
 * @author Gabriele_Marra
 */
public class Minotaur extends Card {
    public Minotaur(Player player) {
        super(player);
        name = CardEnum.MINOTAUR;
    }

    /**
     * Move a {@link Worker} from his actual position to the desired coordinates.
     *
     * @param worker   A worker of the actual player
     * @param desiredX X Position where the player wants to move the worker
     * @param desiredY Y Position where the player wants to move the worker
     * @param island   The current board of game
     * @throws InvalidMovementException Exception thrown when the coordinates are not valid
     */
    @Override
    public void move(Worker worker, int desiredX, int desiredY, Island island) throws InvalidMovementException, WinningException {
        if (island.getCellCluster(desiredX, desiredY).hasWorkerOnTop()) {
            int actualX = worker.getPosition()[0];
            int actualY = worker.getPosition()[1];
            CellCluster desiredCellCluster = island.getCellCluster(desiredX, desiredY);

            //ADDED Verifico che il worker sulla cella desiderata sia di un altro giocatore
            if (desiredCellCluster.getWorkerOwnerUsername().equals(playedBy.getUsername())) {
                throw new InvalidMovementException("Invalid move for this worker");
            }

            if (!isValidDestination(actualX, actualY, desiredX, desiredY, island)) {
                throw new InvalidMovementException("Invalid move for this worker");
            }

            //decrementa il numero di movimenti rimasti
            playedBy.getBehaviour().setMovementsRemaining(playedBy.getBehaviour().getMovementsRemaining() - 1);

            Worker enemyWorker = getEnemyWorker(desiredX, desiredY, island);
            int[] shiftedCoordinates = getShiftedCoordinates(actualX, actualY, desiredX, desiredY);

            island.removeWorker(enemyWorker);
            island.moveWorker(worker, desiredX, desiredY);
            island.placeWorker(enemyWorker, shiftedCoordinates[0], shiftedCoordinates[1]);

            if (!checkWorkerPosition(island, worker, desiredX, desiredY) && !checkWorkerPosition(island, enemyWorker, shiftedCoordinates[0], shiftedCoordinates[1])) {
                throw new InvalidMovementException("The move is valid but there was an error applying desired changes");
            } else {
                //Memorizzo l'altitudine del worker per poi controllare se è effettivamente salito
                int oldAltitudeOfPlayer = island.getCellCluster(actualX, actualY).getCostructionHeight();
                checkWin(island, desiredX, desiredY, oldAltitudeOfPlayer);
            }
        } else {
            super.move(worker, desiredX, desiredY, island);
        }
    }

    /**
     * Check if the destination is reachable from the actual position of the {@link Worker}.
     *
     * @param actualX  Actual X Position of the worker
     * @param actualY  Actual Y Position of the worker
     * @param desiredX X Position where the player wants to place the worker
     * @param desiredY Y Position where the player wants to place the worker
     * @param island   The current board of game
     * @return true when the destination is reachable from the actual position, false otherwise
     */
    @Override
    protected boolean isValidDestination(int actualX, int actualY, int desiredX, int desiredY, Island island) {
        CellCluster actualCellCluster = island.getCellCluster(actualX, actualY);
        CellCluster desiredCellCluster = island.getCellCluster(desiredX, desiredY);
        BehaviourManager behaviour = playedBy.getBehaviour();

        if (desiredCellCluster.getWorkerOwnerUsername() != null) {
            if (desiredCellCluster.getWorkerOwnerUsername().equals(playedBy.getUsername())) {
                return false;
            }
            else{
                try {
                    if (!isValidDirectionShift(actualX, actualY, desiredX, desiredY, island)) {
                        return false;
                    }
                } catch (InvalidMovementException ex) {
                    return false;
                }
            }
        }


        //Verifico che la coordinate di destinazione siano diverse da quelle attuali
        if (actualX == desiredX && actualY == desiredY) {
            return false;
        }
        //verifica il behaviour permette di muoversi
        if (behaviour.getMovementsRemaining() <= 0) {
            return false;
        }
        //calcola la distanza euclidea e verifica che sia min di 2 (ritorna false altrimenti)
        if (distance(actualX, actualY, desiredX, desiredY) >= 2) {
            return false;
        }
        if (desiredCellCluster.isComplete()) {
            return false;
        }
        //verifica il behaviour permette di salire
        if (behaviour.isCanClimb()) {
            //al max salgo di 1
            if (actualCellCluster.getCostructionHeight() + 1 < desiredCellCluster.getCostructionHeight()) {
                return false;
            }
        } else {
            //non posso salire
            if (actualCellCluster.getCostructionHeight() < desiredCellCluster.getCostructionHeight()) {
                return false;
            }
        }
        return true;
    }

    /**
     * This class test if the Minotaur can use his power: return true if the cell close to the desired one is free and not complete.
     *
     * @param actualX  Actual X Position of the worker
     * @param actualY  Actual Y Position of the worker
     * @param desiredX X Position where the player wants to move the worker
     * @param desiredY Y Position where the player wants to move the worker
     * @param island   The current board of game
     * @return true if the worker in the desired CellCluster can shift in the same direction, false otherwise
     */
    private boolean isValidDirectionShift(int actualX, int actualY, int desiredX, int desiredY, Island island) throws InvalidMovementException {
        int[] shiftedCoordinates = getShiftedCoordinates(actualX, actualY, desiredX, desiredY);

        CellCluster shiftCellCluster = island.getCellCluster(shiftedCoordinates[0], shiftedCoordinates[1]);

        if (shiftCellCluster.hasWorkerOnTop() || shiftCellCluster.isComplete()) {
            return false;
        }
        return true;
    }

    /**
     * @param actualX  Actual X Position of the worker
     * @param actualY  Actual Y Position of the worker
     * @param desiredX X Position where the player wants to move the worker
     * @param desiredY Y Position where the player wants to move the worker
     * @return the coordinates of the shift movement
     */
    private int[] getShiftedCoordinates(int actualX, int actualY, int desiredX, int desiredY) throws InvalidMovementException {
        int directionOnX = desiredX - actualX;
        int directionOnY = desiredY - actualY;

        int[] shiftedCoordinates = new int[2];
        shiftedCoordinates[0] = desiredX + directionOnX;
        shiftedCoordinates[1] = desiredY + directionOnY;

        if (shiftedCoordinates[0] < 0 | shiftedCoordinates[0] > 4 | shiftedCoordinates[1] < 0 | shiftedCoordinates[1] > 4) {
            throw new InvalidMovementException("The opposite worker can't shift in this coordinates");
        }

        return shiftedCoordinates;
    }

    /**
     * @param desiredX X Position where the player wants to move the worker
     * @param desiredY Y Position where the player wants to move the worker
     * @param island   The current board of game
     * @return The enemy {@code Worker} in the desired coordinates
     * @throws InvalidMovementException if the opposite worker is not found
     */
    private Worker getEnemyWorker(int desiredX, int desiredY, Island island) throws InvalidMovementException {
        CellCluster desiredCellCluster = island.getCellCluster(desiredX, desiredY);
        String enemyUsername = desiredCellCluster.getWorkerOwnerUsername();
        Worker.IDs enemyWorkerID = desiredCellCluster.getWorkerID();

        List<Player> playerList = playedBy.getPlayers();
        Player oppositePlayer = null;
        for (Player actual : playerList) {
            if (actual.getUsername().equals(enemyUsername)) {
                oppositePlayer = actual;
            }
        }

        if (oppositePlayer == null) throw new InvalidMovementException("Opposite Player not found");

        return oppositePlayer.getWorker(enemyWorkerID);
    }
}
