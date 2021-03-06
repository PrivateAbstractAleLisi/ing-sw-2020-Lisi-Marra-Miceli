package it.polimi.ingsw.psp58.model.gods;

import it.polimi.ingsw.psp58.model.Card;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.model.Player;
import it.polimi.ingsw.psp58.exceptions.InvalidMovementException;
import it.polimi.ingsw.psp58.exceptions.WinningException;
import it.polimi.ingsw.psp58.model.gamemap.Island;
import it.polimi.ingsw.psp58.model.gamemap.Worker;

import java.util.List;

/**
 * Athena Card implementation.
 */
public class Athena extends Card {
    public Athena(Player player) {
        super(player);
        name = CardEnum.ATHENA;
    }

    /**
     * Move a {@link Worker} from his actual position to the desired coordinates and, if the worker move up, set to false the {@code canClimb} behaviour for other players.
     *
     * @param worker   A worker of the actual player
     * @param desiredX X Position where the player wants to move the worker
     * @param desiredY Y Position where the player wants to move the worker
     * @param island   The current board of game
     * @throws InvalidMovementException Exception thrown when the coordinates are not valid
     * @throws WinningException If the player won, throw a WinningException
     */
    @Override
    public void move(Worker worker, int desiredX, int desiredY, Island island) throws InvalidMovementException, WinningException {
        int actualX = worker.getPosition()[0];
        int actualY = worker.getPosition()[1];

        super.move(worker, desiredX, desiredY, island);

        int oldAltitudeOfPlayer = island.getCellCluster(actualX, actualY).getCostructionHeight();
        int newAltitudeOfPlayer = island.getCellCluster(desiredX, desiredY).getCostructionHeight();

        if (newAltitudeOfPlayer - oldAltitudeOfPlayer > 0) {
            List<Player> allPlayers = playedBy.getPlayers();

            for (Player actual : allPlayers) {
                if (!actual.equals(playedBy)) {
                    actual.getBehaviour().setCanClimb(false);
                }
            }
        }
    }
}
