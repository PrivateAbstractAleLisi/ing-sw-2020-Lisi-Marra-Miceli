package model.Gods;

import model.Card;
import model.CardEnum;
import model.Player;
import model.exception.WinningException;
import model.gamemap.CellCluster;
import model.gamemap.Island;

/**
 * @author alelisi
 */
public class Pan extends Card {

    public Pan(Player p) {
        super(p);
        name = CardEnum.PAN;

    }

    @Override
    protected void checkWin(Island island, int x, int y, int oldAltitudeOfPlayer) throws WinningException {
        super.checkWin(island, x, y, oldAltitudeOfPlayer);
        CellCluster cellCluster = island.getCellCluster(x, y);
        int deltaH = oldAltitudeOfPlayer - cellCluster.getCostructionHeight();
        System.out.println("DEBUG: PAN: worker is going down from " + oldAltitudeOfPlayer + " to " + cellCluster.getCostructionHeight() + ", delta is " + deltaH);
        if (deltaH >= 2) {
            throw new WinningException("Worker went down of two levels of more");
        }
    }
}
