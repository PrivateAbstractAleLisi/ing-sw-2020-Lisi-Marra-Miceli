package it.polimi.ingsw.psp58.model.gods;

import it.polimi.ingsw.psp58.model.Card;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.model.Player;
import it.polimi.ingsw.psp58.exceptions.WinningException;
import it.polimi.ingsw.psp58.model.gamemap.CellCluster;
import it.polimi.ingsw.psp58.model.gamemap.Island;

/**
 * Pan Card implementation.
 *
 * @author alelisi
 */
public class Pan extends Card {

    public Pan(Player p) {
        super(p);
        name = CardEnum.PAN;

    }

    /**
     * Check if the player reached the third level during his turn or if Pan moved down by 2 levels.
     *
     * @param island              The current board of game
     * @param x                   The X Coordinate where the Worker is
     * @param y                   The y Coordinate where the Worker is
     * @param oldAltitudeOfPlayer The altitude of worker before the move
     * @throws WinningException The exception for the winning
     */
    @Override
    protected void checkWin(Island island, int x, int y, int oldAltitudeOfPlayer) throws WinningException {
        super.checkWin(island, x, y, oldAltitudeOfPlayer);
        CellCluster cellCluster = island.getCellCluster(x, y);
        boolean isAPerimeterCell = (x == 0 || x == 4 || y == 0 || y == 4);

        int deltaH = oldAltitudeOfPlayer - cellCluster.getCostructionHeight();

        System.out.println("DEBUG: PAN: worker is going down from " + oldAltitudeOfPlayer + " to " + cellCluster.getCostructionHeight() + ", delta is " + deltaH);
        if ((playedBy.getBehaviour().canWinOnPerimeterCell() || !isAPerimeterCell) && deltaH >= 2) {
            throw new WinningException("Worker went down of two levels of more");
        }

    }
}
