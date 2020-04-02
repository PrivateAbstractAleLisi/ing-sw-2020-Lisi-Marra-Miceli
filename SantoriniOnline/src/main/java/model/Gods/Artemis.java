package model.Gods;

import model.BehaviourManager;
import model.Card;
import model.Player;
import model.exception.InvalidMovementException;
import model.exception.WinningException;
import model.gamemap.Island;
import model.gamemap.Worker;

public class Artemis extends Card {

    private int[] startingPosition;

    /**
     * costructor, same as card but it fills name and descriptions
     * @param p the player that plays with the card
     */
    public Artemis(Player p) {
        super(p);
        name = "Artemis";
        description = "Il tuo operatio può muoversi una volta di più, ma non retrocedendo alla casella di partenza";

    }

    /**
     * Artemis overrides the move method
     * SPECIAL EFFECT: Artemis can move a second time without going back to starting position
     * @param worker   A worker of the actual player
     * @param desiredX X Position where the player wants to move the worker
     * @param desiredY Y Position where the player wants to move the worker
     * @param island   The current board of game
     * @throws InvalidMovementException
     * @throws WinningException
     */
    @Override
    public void move(Worker worker, int desiredX, int desiredY, Island island) throws InvalidMovementException, WinningException {

        if (playedBy.getBehaviour().getMovementsRemaining() > 0) { //se non mi hanno bloccato il movimento

            //dove parte il mio worker
            int actualX = worker.getPosition()[0];
            int actualY = worker.getPosition()[1];

            //posso andare li?
            if (!isValidDestination(actualX, actualY, desiredX, desiredY, island)) {
                throw new InvalidMovementException("Invalid move for this worker");
            }


            //se non mi sono mai mosso mi salvo da dove parto
            if (playedBy.getBehaviour().getMovementsRemaining() == 2) {
                startingPosition = new int[2]; ;
                startingPosition[0] = desiredX;
                startingPosition[1] = desiredY;
            }
            //se è la seconda volta che mi muovo allora controllo che non sto tornando dove ero prima
            else if (playedBy.getBehaviour().getMovementsRemaining() == 1) {
                if (desiredX == startingPosition[0] && desiredY == startingPosition[1]) {
                    throw new IllegalArgumentException("ARTEMIS: on the second movement you can't come back to starting position ");
                }
            }

            //se posso allora mi sposto
            island.moveWorker(worker, desiredX, desiredY);

            if (!checkWorkerPosition(island, worker, desiredX, desiredY)) {
                throw new InvalidMovementException("The move is valid but there was an error applying desired changes");
            } else {
                //Memorizzo l'altitudine del worker per poi controllare se è effettivamente salito
                int oldAltitudeOfPlayer = island.getCellCluster(actualX, actualY).getCostructionHeight();
                checkWin(island, desiredX, desiredY, oldAltitudeOfPlayer);
            }

        }
    }

    //ARTEMIS can move two times in normal conditions
    @Override
    public void resetBehaviour() {
        BehaviourManager b = playedBy.getBehaviour();
        b.setBlockPlacementLeft(1);
        b.setCanBuildDomeEverywhere(false);
        b.setCanClimb(true);
        b.setMovementsRemaining(2);
        //convenzione
        startingPosition[0] = -1;
        startingPosition[1] = -1;

    }



}
