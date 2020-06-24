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
 * Apollo Card implementation.
 * @author Gabriele_Marra
 */
public class Apollo extends Card {
    public Apollo(Player player) {
        super(player);
        name = CardEnum.APOLLO;
    }

    /**
     * Move a {@link Worker} from his actual position to the desired coordinates and if necessary, use the God Power.
     * If the desired CellCluster has another worker on top this method use the God Power.
     * If the desired CellCluster is free, this method call the super class method.
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

            if (!isValidDestination(actualX, actualY, desiredX, desiredY, island)) {
                throw new InvalidMovementException("Invalid move for this worker");
            }
            //decrementa il numero di movimenti rimasti
            playedBy.getBehaviour().setMovementsRemaining(playedBy.getBehaviour().getMovementsRemaining() - 1);

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
            Worker enemyWorker = oppositePlayer.getWorker(enemyWorkerID);

            island.removeWorker(enemyWorker);

            island.moveWorker(worker, desiredX, desiredY);
            island.placeWorker(enemyWorker, actualX, actualY);

            if (!checkWorkerPosition(island, worker, desiredX, desiredY) && !checkWorkerPosition(island, enemyWorker, actualX, actualY)) {
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

        if (desiredCellCluster.getWorkerOwnerUsername() != null && desiredCellCluster.getWorkerOwnerUsername().equals(playedBy.getUsername())) {
            return false;
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
            return actualCellCluster.getCostructionHeight() + 1 >= desiredCellCluster.getCostructionHeight();
        } else {
            //non posso salire
            return actualCellCluster.getCostructionHeight() >= desiredCellCluster.getCostructionHeight();
        }
    }
}
