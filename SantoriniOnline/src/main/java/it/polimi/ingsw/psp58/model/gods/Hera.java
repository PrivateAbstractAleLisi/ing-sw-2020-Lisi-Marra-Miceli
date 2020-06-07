package it.polimi.ingsw.psp58.model.gods;

import it.polimi.ingsw.psp58.model.Card;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.model.Player;

import java.util.List;

public class Hera extends Card {

    public Hera(Player p) {
        super(p);
        name = CardEnum.HERA;
    }

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
