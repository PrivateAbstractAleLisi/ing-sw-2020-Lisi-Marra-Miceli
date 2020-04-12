package model.Gods;

import model.Card;
import model.CardEnum;
import model.Player;
import model.exception.InvalidMovementException;
import model.exception.WinningException;
import model.gamemap.Island;
import model.gamemap.Worker;

import java.util.List;

public class Athena extends Card {
    public Athena(Player player) {
        super(player);
        name = CardEnum.ATHENA;
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
