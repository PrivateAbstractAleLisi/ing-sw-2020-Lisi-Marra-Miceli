package it.polimi.ingsw.psp58.model.gods;

import it.polimi.ingsw.psp58.model.Card;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.model.Player;
import it.polimi.ingsw.psp58.exceptions.WinningException;
import it.polimi.ingsw.psp58.model.gamemap.CellCluster;
import it.polimi.ingsw.psp58.model.gamemap.Island;

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
