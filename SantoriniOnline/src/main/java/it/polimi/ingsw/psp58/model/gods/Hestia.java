package it.polimi.ingsw.psp58.model.gods;

import it.polimi.ingsw.psp58.exceptions.InvalidBuildException;
import it.polimi.ingsw.psp58.model.Card;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.model.Player;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;
import it.polimi.ingsw.psp58.model.gamemap.Island;
import it.polimi.ingsw.psp58.model.gamemap.Worker;

public class Hestia extends Card {
    private boolean hasAlreadyBuilt;

    public Hestia(Player p) {
        super(p);
        name = CardEnum.HESTIA;
        hasAlreadyBuilt=false;
    }

    @Override
    public void build(Worker worker, BlockTypeEnum block, int desiredX, int desiredY, Island island) throws InvalidBuildException, CloneNotSupportedException {
        super.build(worker, block, desiredX, desiredY, island);
        if(!hasAlreadyBuilt) hasAlreadyBuilt=true;
    }

    @Override
    protected boolean isValidConstruction(BlockTypeEnum block, int actualX, int actualY, int desiredX, int desiredY, Island island) throws IndexOutOfBoundsException {
        if(hasAlreadyBuilt) { //if has already build she cannot build in a perimeter space
            if(desiredX==0 || desiredX == 4 || desiredY==0 || desiredY == 4){ //it's a perimeter cell
                return false;
            }
        }
        return super.isValidConstruction(block, actualX, actualY, desiredX, desiredY, island);
    }

    @Override
    public void resetBehaviour() {
        super.resetBehaviour();
        playedBy.getBehaviour().setBlockPlacementLeft(2);
        hasAlreadyBuilt=false;
    }
}
