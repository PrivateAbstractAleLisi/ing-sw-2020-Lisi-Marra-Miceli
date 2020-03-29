package model;

import model.exception.InvalidMovementException;
import model.gamemap.CellCluster;
import model.gamemap.Island;
import model.gamemap.Worker;

import static java.lang.StrictMath.sqrt;

//todo implementare i behavoiur!!

public class Card {
    private String name;
    private String description;
    private Object avatarImage;
    private Player playedBy;

    public Card(Player p) {
        playedBy = p;
    }

    /**
     * Place a worker at the beginning of the match
     *
     * @param worker   a worker of the actual player
     * @param desiredX X Position where the player wants to place the worker
     * @param desiredY Y Position where the player wants to place the worker
     * @throws InvalidMovementException Exception thrown when the coordinates are not valid
     */
    public void placeWorker(Worker worker, int desiredX, int desiredY) throws InvalidMovementException {
        if (!isValidPlacement(desiredX, desiredY)) {
            throw new InvalidMovementException("Invalid move for this card");
        }

        playedBy.match.island.placeWorker(worker, desiredX, desiredY);
    }

    /**
     * Move a worker from his actual position to the desired coordinates
     *
     * @param worker   a worker of the actual player
     * @param desiredX X Position where the player wants to place the worker
     * @param desiredY Y Position where the player wants to place the worker
     * @throws InvalidMovementException Exception thrown when the coordinates are not valid
     */
    public void move(Worker worker, int desiredX, int desiredY) throws InvalidMovementException {
        int actualX = worker.getPosition()[0];
        int actualY = worker.getPosition()[1];

        if (!isValidDestination(actualX, actualY, desiredX, desiredY)) {
            throw new InvalidMovementException("Invalid move for this card");
        }

        playedBy.match.island.moveWorker(worker, desiredX, desiredY);
    }

    public void build(Worker.IDs worker) {

    }

    public void resetBehaviour() {

    }

    /**
     * Check if the destination is reachable from the actual position
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

        if (desiredCellCluster.hasWorkerOnTop()) {
            return false;
        }
        if (desiredCellCluster.isComplete()) {
            return false;
        }
        if (distance(actualX, actualY, desiredX, desiredY) > 2) {
            return false;
        }
        if (actualCellCluster.getCostructionHeight() + 1 < desiredCellCluster.getCostructionHeight()) {
            return false;
        }

        return true;
    }

    /**
     * Check if the desired location is free and not complete
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
     * Calculate the Euclidean distance between two given cells
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
