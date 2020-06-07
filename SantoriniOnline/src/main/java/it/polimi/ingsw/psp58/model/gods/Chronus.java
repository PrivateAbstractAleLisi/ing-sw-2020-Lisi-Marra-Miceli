package it.polimi.ingsw.psp58.model.gods;

import it.polimi.ingsw.psp58.model.Card;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.model.Player;

public class Chronus extends Card {
    public Chronus(Player p) {
        super(p);
        name = CardEnum.CHRONUS;
    }
}
