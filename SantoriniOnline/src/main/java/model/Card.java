package model;

import auxiliary.Range;
import model.exception.InvalidBuildException;
import model.exception.InvalidMovementException;
import model.gamemap.BlockTypeEnum;
import model.gamemap.CellCluster;
import model.gamemap.Island;
import model.gamemap.Worker;

import static java.lang.StrictMath.sqrt;

//todo implementare i behavoiur!!

/**
 * Generic {@code abstract class} for Gods Cards. It implements all the methods that {@link Player} needs to use like {@code move} and {@code build}.
 * <p>
 * This class has to be extended for each God and lots of method should be override to provide the correct behaviour of each God.
 * <br>Each method of this class implements the standard rules of the game, not modified by the God Powers.
 * </p>
 * <p>
 * To define what action is valid or not, the methods analise the {@link Island} and the moves that any {@link Player} can do (accessing to {@link GameManager} object in {@link Player}).
 */
public abstract class Card {
    private String name;
    private String description;
    private Object avatarImage;
    private Player playedBy;

    public Card(Player p) {
        playedBy = p;
    }

    /**
     * Place a {@link Worker} at the beginning of the match in the desired coordinated.
     *
     * @param worker   A worker of the actual player
     * @param desiredX X Position where the player wants to place the worker
     * @param desiredY Y Position where the player wants to place the worker
     * @throws InvalidMovementException Exception thrown when the coordinates are not valid, or the behaviour of the player block this action
     */
    public void placeWorker(Worker worker, int desiredX, int desiredY) throws InvalidMovementException {
        if (!isValidPlacement(desiredX, desiredY)) {
            throw new InvalidMovementException("Invalid move for this card");
        }

        playedBy.match.island.placeWorker(worker, desiredX, desiredY);
    }

    /**
     * Move a {@link Worker} from his actual position to the desired coordinates.
     *
     * @param worker   A worker of the actual player
     * @param desiredX X Position where the player wants to move the worker
     * @param desiredY Y Position where the player wants to move the worker
     * @throws InvalidMovementException Exception thrown when the coordinates are not valid
     */
    public void move(Worker worker, int desiredX, int desiredY) throws InvalidMovementException {
        int actualX = worker.getPosition()[0];
        int actualY = worker.getPosition()[1];

        if (!isValidDestination(actualX, actualY, desiredX, desiredY)) {
            throw new InvalidMovementException("Invalid move for this worker");
        }

        playedBy.match.island.moveWorker(worker, desiredX, desiredY);
    }

    /**
     * Build a block, near a {@link Worker}, in the the desired coordinates.
     *
     * @param worker   A worker of the actual player
     * @param block    level of block desired to build
     * @param desiredX X Position where the player wants to build the block
     * @param desiredY Y Position where the player wants to build the block
     * @throws InvalidBuildException Exception thrown when the coordinates are not valid, or the behaviour of the player block this action
     */
    public void build(Worker worker, BlockTypeEnum block, int desiredX, int desiredY) throws InvalidBuildException {
        int actualX = worker.getPosition()[0];
        int actualY = worker.getPosition()[1];

        if (!isValidConstruction(block, actualX, actualY, desiredX, desiredY)) {
            throw new InvalidBuildException("Invalid build for this worker");
        }

        playedBy.match.island.buildBlock(block, desiredX, desiredY);
    }

    /**
     * Reset the {@code behaviour} of the {@link Player} to the default value of the card, some Gods need to override this method
     */
    public void resetBehaviour() {
        playedBy.behaviour.setBlockPlacementLeft(1);
        playedBy.behaviour.setMovementsRemaining(1);
        playedBy.behaviour.setCanClimb(true);
        playedBy.behaviour.setCanBuildDomeEverywhere(false);
    }

    /**
     * Check if the destination is reachable from the actual position of the {@link Worker}.
     *
     * @param actualX  Actual X Position of the worker
     * @param actualY  Actual Y Position of the worker
     * @param desiredX X Position where the player wants to place the worker
     * @param desiredY Y Position where the player wants to place the worker
     * @return true when the destination is reachable from the actual position, false otherwise
     */
    private boolean isValidDestination(int actualX, int actualY, int desiredX, int desiredY) {
        Island islandRef = playedBy.match.island;
        CellCluster actualCellCluster = islandRef.getCellCluster(actualX, actualY);
        CellCluster desiredCellCluster = islandRef.getCellCluster(desiredX, desiredY);

//        Controllo se l'indice della destinazione è valido (0<index<5)
        Range indexOk = new Range(0, 4);
        if (!(indexOk.contains(desiredX) && indexOk.contains(desiredY))) {
            return false;
        }
        //verifica il behaviour permette di muoversi
        if (playedBy.behaviour.getMovementsRemaining() <= 0) {
            return false;
        }
        if (desiredCellCluster.hasWorkerOnTop()) {
            return false;
        }
        if (desiredCellCluster.isComplete()) {
            return false;
        }
        //calcola la distanza euclidea e verifica che sia min di 2 (ritorna false altrimenti)
        if (distance(actualX, actualY, desiredX, desiredY) >= 2) {
            return false;
        }
        //verifica il behaviour permette di salire
        if (playedBy.behaviour.isCanClimb()) {
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

        //decrementa il numero di movimenti rimasti
        playedBy.behaviour.setMovementsRemaining(playedBy.behaviour.getMovementsRemaining() - 1);

        return true;
    }

    /**
     * Check if the desired location is free and not complete for a {@link Worker} Placement.
     *
     * @param desiredX X Position where the player wants to place the worker
     * @param desiredY Y Position where the player wants to place the worker
     * @return true if the the desired location is free and not complete, false otherwise
     */
    private boolean isValidPlacement(int desiredX, int desiredY) {
        CellCluster desiredCellCluster = playedBy.match.island.getCellCluster(desiredX, desiredY);
        if (desiredCellCluster.hasWorkerOnTop()) {
            return false;
        }
        if (desiredCellCluster.isComplete()) {
            return false;
        }
        return true;
    }

    /**
     * Check if the construction can be done from the actual position of the {@link Worker}.
     *
     * @param block    level of construction player wants to build
     * @param actualX  Actual X Position of the worker
     * @param actualY  Actual Y Position of the worker
     * @param desiredX X Position where the player wants to place the worker
     * @param desiredY Y Position where the player wants to place the worker
     * @return true when the construction can be done from the actual position, false otherwise
     */
    private boolean isValidConstruction(BlockTypeEnum block, int actualX, int actualY, int desiredX, int desiredY) {
        Island islandRef = playedBy.match.island;
//        CellCluster actualCellCluster = islandRef.getCellCluster(actualX, actualY);
        CellCluster desiredCellCluster = islandRef.getCellCluster(desiredX, desiredY);

        //Controllo se l'indice della costruzione è valido (0<index<5)
        Range indexOk = new Range(0, 4);
        if (!(indexOk.contains(desiredX) && indexOk.contains(desiredY))) {
            return false;
        }
        //verifica il behaviour permette di costruire
        if (playedBy.behaviour.getBlockPlacementLeft() <= 0) {
            return false;
        }
        if (desiredCellCluster.hasWorkerOnTop()) {
            return false;
        }
        if (desiredCellCluster.isComplete()) {
            return false;
        }
        //calcola la distanza euclidea e verifica che sia min di 2 (ritorna false altrimenti)
        if (distance(actualX, actualY, desiredX, desiredY) >= 2) {
            return false;
        }
        //todo verificare la possibilità di mettere quel preciso blocco


        //decrementa il numero di blocchi da costruire rimasti
        playedBy.behaviour.setBlockPlacementLeft(playedBy.behaviour.getBlockPlacementLeft() - 1);
        return true;
    }

    /**
     * Calculate the Euclidean distance between two given cells.
     *
     * @param x1 Coordinate X of first Cell
     * @param y1 Coordinate X of first Cell
     * @param x2 Coordinate X of first Cell
     * @param y2 Coordinate X of first Cell
     * @return Value of Euclidean distance between the two cells
     */
    private double distance(int x1, int y1, int x2, int y2) {
        return sqrt(((x1 - x2) ^ 2 + (y1 - y2) ^ 2));
    }

}
