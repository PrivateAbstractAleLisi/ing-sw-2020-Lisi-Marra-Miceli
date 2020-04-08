package model;

import model.exception.InvalidBuildException;
import model.exception.InvalidMovementException;
import model.exception.NoRemainingBlockException;
import model.exception.WinningException;
import model.gamemap.BlockTypeEnum;
import model.gamemap.CellCluster;
import model.gamemap.Island;
import model.gamemap.Worker;

import static java.lang.StrictMath.sqrt;

/**
 * Generic {@code abstract class} for Gods Cards. It implements all the methods that {@link Player} needs to use like {@code move} and {@code build}.
 * <p>
 * This class has to be extended for each God and lots of method should be override to provide the correct behaviour of each God.
 * <br>Each method of this class implements the standard rules of the game, not modified by the God Powers.
 * </p>
 * <p>
 * To define what action is valid or not, the methods analise the {@link Island} and the moves that any {@link Player} can do (accessing to {@link BoardManager} object in {@link Player}).
 */
public abstract class Card {
    protected String name;
    protected String description;
    private Object avatarImage;
    protected Player playedBy;

    public Card(Player p) {
        playedBy = p;
    }

    /**
     * Place a {@link Worker} at the beginning of the match in the desired coordinated.
     *
     * @param worker   A worker of the actual player
     * @param desiredX X Position where the player wants to place the worker
     * @param desiredY Y Position where the player wants to place the worker
     * @param island   The current board of game
     * @throws InvalidMovementException Exception thrown when the coordinates are not valid, or the behaviour of the player block this action
     */
    public void placeWorker(Worker worker, int desiredX, int desiredY, Island island) throws CloneNotSupportedException, InvalidMovementException {
        if (!isValidWorkerPlacement(worker, desiredX, desiredY, island)) {
            throw new InvalidMovementException("Invalid Placement for this card");
        }

        island.placeWorker(worker, desiredX, desiredY);

        if (!checkWorkerPosition(island, worker, desiredX, desiredY)) {
            throw new InvalidMovementException("The move is valid there was an error applying desired changes");
        }
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
    public void move(Worker worker, int desiredX, int desiredY, Island island) throws InvalidMovementException, WinningException {
        int actualX = worker.getPosition()[0];
        int actualY = worker.getPosition()[1];

        if (!isValidDestination(actualX, actualY, desiredX, desiredY, island)) {
            throw new InvalidMovementException("Invalid move for this worker");
        }
        //decrementa il numero di movimenti rimasti
        playedBy.getBehaviour().setMovementsRemaining(playedBy.getBehaviour().getMovementsRemaining() - 1);

        island.moveWorker(worker, desiredX, desiredY);
        if (!checkWorkerPosition(island, worker, desiredX, desiredY)) {
            throw new InvalidMovementException("The move is valid but there was an error applying desired changes");
        } else {
            //Memorizzo l'altitudine del worker per poi controllare se è effettivamente salito
            int oldAltitudeOfPlayer = island.getCellCluster(actualX, actualY).getCostructionHeight();
            checkWin(island, desiredX, desiredY, oldAltitudeOfPlayer);
        }
    }

    /**
     * Build a block, near a {@link Worker}, in the the desired coordinates.
     *
     * @param worker   A worker of the actual player
     * @param block    level of block desired to build
     * @param desiredX X Position where the player wants to build the block
     * @param desiredY Y Position where the player wants to build the block
     * @param island   The current board of game
     * @throws InvalidBuildException Exception thrown when the coordinates are not valid, or the behaviour of the player block this action
     */
    public void build(Worker worker, BlockTypeEnum block, int desiredX, int desiredY, Island island) throws InvalidBuildException, CloneNotSupportedException {
        int actualX = worker.getPosition()[0];
        int actualY = worker.getPosition()[1];
        int[] oldCellCluster = null;

        CellCluster old = island.getCellCluster(desiredX, desiredY);
        if (old.getCostructionHeight() != 0) {
            oldCellCluster = old.toIntArray();
        } else {
            oldCellCluster = new int[1];
        }

        if (!isValidConstruction(block, actualX, actualY, desiredX, desiredY, island)) {
            throw new InvalidBuildException("Invalid build for this worker");
        }
        //decrementa il numero di blocchi da costruire rimasti e ritorno true
        playedBy.getBehaviour().setBlockPlacementLeft(playedBy.getBehaviour().getBlockPlacementLeft() - 1);

        //decrease the number of block of this type available
        try {
            playedBy.getBoardManager().drawBlock(block);
        } catch (NoRemainingBlockException e) {
            throw new InvalidBuildException("The build is valid but BoardManager has no block remaining");
        }

        island.buildBlock(block, desiredX, desiredY);
        if (!checkBlockPosition(island, block, desiredX, desiredY, oldCellCluster)) {
            throw new InvalidBuildException("The build is valid but there was an error applying desired changes");
        }
    }

    /**
     * Reset the {@code behaviour} of the {@link Player} to the default value of the card, some Gods need to override this method
     */
    public void resetBehaviour() {
        BehaviourManager behaviour = playedBy.getBehaviour();
        behaviour.setBlockPlacementLeft(1);
        behaviour.setMovementsRemaining(1);
        behaviour.setCanClimb(true);
        behaviour.setCanBuildDomeEverywhere(false);
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
    protected boolean isValidDestination(int actualX, int actualY, int desiredX, int desiredY, Island island) {
        CellCluster actualCellCluster = island.getCellCluster(actualX, actualY);
        CellCluster desiredCellCluster = island.getCellCluster(desiredX, desiredY);
        BehaviourManager behaviour = playedBy.getBehaviour();

        //Verifico che la coordinate di destinazione siano diverse da quelle attuali
        if (actualX == desiredX && actualY == desiredY) {
            return false;
        }
        //verifica il behaviour permette di muoversi
        if (behaviour.getMovementsRemaining() <= 0) {
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
     * Check if the destination is reachable from the actual position of the {@link Worker}, also available GodPower.
     *
     * @param actualX  Actual X Position of the worker
     * @param actualY  Actual Y Position of the worker
     * @param desiredX X Position where the player wants to place the worker
     * @param desiredY Y Position where the player wants to place the worker
     * @param island   The current board of game
     * @return true when the destination is reachable from the actual position, false otherwise
     */
    protected boolean checkCellMovementAvailability(int actualX, int actualY, int desiredX, int desiredY, Island island) {
        return this.isValidDestination(actualX, actualY, desiredX, desiredY, island);
    }

    /**
     * Check if the construction can be done from the actual position of the {@link Worker}., also available GodPower.
     *
     * @param actualX  Actual X Position of the worker
     * @param actualY  Actual Y Position of the worker
     * @param desiredX X Position where the player wants to build the worker
     * @param desiredY Y Position where the player wants to build the worker
     * @param island   The current board of game
     * @return rue when the construction can be done from the actual position, false otherwise
     */
    protected boolean checkCellCostructionAvailability(int actualX, int actualY, int desiredX, int desiredY, Island island) {
        for (BlockTypeEnum block : BlockTypeEnum.values()) {
            if (this.isValidConstruction(block, actualX, actualY, desiredX, desiredY, island)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the desired location is free and not complete for a {@link Worker} Placement.
     *
     * @param worker   A worker of the actual player
     * @param desiredX X Position where the player wants to place the worker
     * @param desiredY Y Position where the player wants to place the worker
     * @param island   The current board of game
     * @return true if the the desired location is free and not complete, false otherwise
     */
    protected boolean isValidWorkerPlacement(Worker worker, int desiredX, int desiredY, Island island) throws IndexOutOfBoundsException {
        CellCluster desiredCellCluster = island.getCellCluster(desiredX, desiredY);
        if (desiredCellCluster.hasWorkerOnTop()) {
            return false;
        }
        if (desiredCellCluster.isComplete()) {
            return false;
        }
        if (worker.isPlacedOnIsland()) {
            return false;
        }
        return true;
    }

    /**
     * Check if the construction can be done from the actual position of the {@link Worker}.
     *
     * @param block    level of construction {@link Player} wants to build
     * @param actualX  Actual X Position of the worker
     * @param actualY  Actual Y Position of the worker
     * @param desiredX X Position where the player wants to place the worker
     * @param desiredY Y Position where the player wants to place the worker
     * @param island   The current board of game
     * @return true when the construction can be done from the actual position, false otherwise
     */
    protected boolean isValidConstruction(BlockTypeEnum block, int actualX, int actualY, int desiredX, int desiredY, Island island) throws IndexOutOfBoundsException {
        //        CellCluster actualCellCluster = islandRef.getCellCluster(actualX, actualY);
        CellCluster desiredCellCluster = island.getCellCluster(desiredX, desiredY);
        BehaviourManager behaviour = playedBy.getBehaviour();

        //verifica il behaviour permette di costruire
        if (behaviour.getBlockPlacementLeft() <= 0) {
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

        //genero un array contenente la struttura del cellcluster (e il nuovo blocco) e l'analizzo nella funzione successiva
        int[] desiredConstruction = desiredCellCluster.toIntArrayWithHypo(block);
        if (!isValidBlockPlacement(block, desiredConstruction, behaviour)) {
            return false;
        }
        return true;
    }

    /**
     * Check if the desired block can be added to the selected {@link CellCluster}.
     *
     * @param block               level of construction {@link Player} wants to build
     * @param desiredConstruction Array of int that represent the current {@link CellCluster} with the addition of the desired block
     * @param behaviour           behaviour of the {@link Card}
     * @return true if the block order and placement is valid, false otherwise
     */
    protected boolean isValidBlockPlacement(BlockTypeEnum block, int[] desiredConstruction, BehaviourManager behaviour) {
        BoardManager boardManager = playedBy.getBoardManager();

        if (boardManager.getNumberOfBlocksRemaining(block) <= 0) {
            return false;
        }

        int[] longArray = new int[desiredConstruction.length + 1];
        longArray[0] = 0;

        for (int i = 0; i < desiredConstruction.length; i++) {
            longArray[i + 1] = desiredConstruction[i];
        }

        for (int i = 0; i < desiredConstruction.length; i++) {
            longArray[i] -= desiredConstruction[i];
            if (longArray[i] < -1) {
                if (block != BlockTypeEnum.DOME || !(behaviour.isCanBuildDomeEverywhere())) {
                    return false;
                }
            }
        }

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
    public double distance(int x1, int y1, int x2, int y2) {
        return sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }

    /**
     * The {@code checkWorkerPosition method} check if the selected {@link Worker} is one the selected {@link CellCluster}
     *
     * @param island The current board of game
     * @param worker The {@code Worker} is needed to check the position
     * @param x      The X Coordinate where the {@code Worker} should be
     * @param y      The y Coordinate where the {@code Worker} should be
     * @return true if the {@code Worker} is on the right position, false if the {@code CellCluster}  doesn't have a player on Top or the position of worker is different.
     */
    protected boolean checkWorkerPosition(Island island, Worker worker, int x, int y) {
        CellCluster cellCluster = island.getCellCluster(x, y);
        if (!cellCluster.hasWorkerOnTop()) {
            return false;
        }

        int xRead = worker.getPosition()[0];
        int yRead = worker.getPosition()[1];
        if (xRead != x || yRead != y) {
            return false;
        }

        return true;
    }

    /**
     * NOT IMPLEMENTED - The {@code checkBlockPosition method} check if the selected {@code block} is one the right place in the right {@link CellCluster}.
     *
     * @param island         The current board of game
     * @param block          The {@code block} is needed to check the position
     * @param x              The X Coordinate where the {@code block} should be
     * @param y              The Y Coordinate where the {@code block} should be
     * @param oldCellCluster The CellCluster before the building of the block
     * @return true if the {@code block} is on the right position, false otherwise
     */
    protected boolean checkBlockPosition(Island island, BlockTypeEnum block, int x, int y, int[] oldCellCluster) {
        int[] actualCellCluster = island.getCellCluster(x, y).toIntArray();

        int blockToAdd = 0;

        switch (block) {
            case LEVEL1:
                blockToAdd = 1;
                break;
            case LEVEL2:
                blockToAdd = 2;
                break;
            case LEVEL3:
                blockToAdd = 3;
                break;
            case DOME:
                blockToAdd = 4;
                break;
        }

        int[] subCellCluster = actualCellCluster.clone();

        for (int i = 0; i < oldCellCluster.length; i++) {
            subCellCluster[i] -= oldCellCluster[i];
        }
        for (int i = 0; i < subCellCluster.length; i++) {
            if (subCellCluster[i] != 0 && subCellCluster[i] == blockToAdd) {
                return true;
            }
        }
        return false;
    }

    /**
     * The {@code checkWin method} throw a new {@code WinningException} when the {@link Worker} reach the 3th Level.
     *
     * @param island              The current board of game
     * @param x                   The X Coordinate where the Worker is
     * @param y                   The y Coordinate where the Worker is
     * @param oldAltitudeOfPlayer The altitude of worker before the move
     * @throws WinningException The {@code WinningException} is thrown when the Player triggered one of the Win check
     */
    protected void checkWin(Island island, int x, int y, int oldAltitudeOfPlayer) throws WinningException {
        CellCluster cellCluster = island.getCellCluster(x, y);
        //The Worker must increase its Altitude to win
        if (cellCluster.hasWorkerOnTop() && cellCluster.getCostructionHeight() == 3 && cellCluster.getCostructionHeight() > oldAltitudeOfPlayer) {
            throw new WinningException("Worker on 3th level!!");
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
