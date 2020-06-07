package it.polimi.ingsw.psp58.model.gods;

import it.polimi.ingsw.psp58.exceptions.InvalidMovementException;
import it.polimi.ingsw.psp58.exceptions.WinningException;
import it.polimi.ingsw.psp58.model.Card;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.model.Player;
import it.polimi.ingsw.psp58.model.gamemap.Island;
import it.polimi.ingsw.psp58.model.gamemap.Worker;

public class Triton extends Card {

    public Triton(Player p) {
        super(p);
        name = CardEnum.TRITON;
    }

    /**
     * Triton overrides the move method
     * SPECIAL EFFECT: Triton can immediately move another time if he moves into a perimeter space
     *
     * @param worker   A worker of the actual player
     * @param desiredX X Position where the player wants to move the worker
     * @param desiredY Y Position where the player wants to move the worker
     * @param island   The current board of game
     * @throws InvalidMovementException
     * @throws WinningException
     */
    @Override
    public void move(Worker worker, int desiredX, int desiredY, Island island) throws InvalidMovementException, WinningException {
        //where my worker starts
        int actualX = worker.getPosition()[0];
        int actualY = worker.getPosition()[1];

        //Checks if movement is available and if I can move there
        if (!isValidDestination(actualX, actualY, desiredX, desiredY, island)) {
            throw new InvalidMovementException("Invalid move for this worker");
        }

        //se posso allora mi sposto
        island.moveWorker(worker, desiredX, desiredY);

        //decrementa il numero di movimenti rimasti
        playedBy.getBehaviour().setMovementsRemaining(playedBy.getBehaviour().getMovementsRemaining() - 1);

        if (!checkWorkerPosition(island, worker, desiredX, desiredY)) {
            throw new InvalidMovementException("The move is valid but there was an error applying desired changes");
        } else {
            //Memorizzo l'altitudine del worker per poi controllare se è effettivamente salito
            int oldAltitudeOfPlayer = island.getCellCluster(actualX, actualY).getCostructionHeight();
            checkWin(island, desiredX, desiredY, oldAltitudeOfPlayer);
        }

        //if he moved in a perimeter space increment the remaining movement
        if (desiredX == 0 || desiredX == 4 || desiredY == 0 || desiredY == 4){
            playedBy.getBehaviour().setMovementsRemaining(playedBy.getBehaviour().getMovementsRemaining() + 1);
        }
    }
}
