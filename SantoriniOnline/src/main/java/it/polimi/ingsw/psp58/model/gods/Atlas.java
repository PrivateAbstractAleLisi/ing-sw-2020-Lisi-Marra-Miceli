package it.polimi.ingsw.psp58.model.gods;

import it.polimi.ingsw.psp58.model.Card;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.model.Player;

public class Atlas extends Card {
    public Atlas(Player player) {
        super(player);
        name = CardEnum.ATLAS;
    }

    /**
     * Reset the {@code behaviour} of the {@link Player} to the default value of the card, some Gods need to override this method
     */
    @Override
    public void resetBehaviour() {
        super.resetBehaviour();
        playedBy.getBehaviour().setCanBuildDomeEverywhere(true); //<-- Set to True for this God
    }
}
