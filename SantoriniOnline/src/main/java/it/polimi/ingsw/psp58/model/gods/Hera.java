package it.polimi.ingsw.psp58.model.gods;

import it.polimi.ingsw.psp58.model.Card;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.model.Player;

import java.util.List;

/**
 * Hera Card implementation.
 */
public class Hera extends Card {

    public Hera(Player p) {
        super(p);
        name = CardEnum.HERA;
    }

    /**
     * Reset the {@code behaviour} of the {@link Player} to the default value of the card and set to false the {@code setCanWinOnPerimeterCell} behaviour for other players.
     */
    @Override
    public void resetBehaviour() {
        super.resetBehaviour();
        List<Player> allPlayers = playedBy.getPlayers();

        for (Player actual : allPlayers) {
            if (!actual.equals(playedBy)) {
                actual.getBehaviour().setCanWinOnPerimeterCell(false);
            }
        }
    }
}
