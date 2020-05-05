package it.polimi.ingsw.psp58.model.gods;

import it.polimi.ingsw.psp58.auxiliary.Range;
import it.polimi.ingsw.psp58.exceptions.InvalidBuildException;
import it.polimi.ingsw.psp58.exceptions.InvalidMovementException;
import it.polimi.ingsw.psp58.exceptions.NoRemainingBlockException;
import it.polimi.ingsw.psp58.exceptions.WinningException;
import it.polimi.ingsw.psp58.model.*;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;
import it.polimi.ingsw.psp58.model.gamemap.CellCluster;
import it.polimi.ingsw.psp58.model.gamemap.Island;
import it.polimi.ingsw.psp58.model.gamemap.Worker;

import static java.lang.StrictMath.sqrt;

public class Superuser extends Card {
    public Superuser(Player p) {
        super(p);
    }

    @Override
    public void placeWorker(Worker worker, int desiredX, int desiredY, Island island) throws InvalidMovementException {
        island.placeWorker(worker, desiredX, desiredY);
    }

    @Override
    public void resetBehaviour() {
        BehaviourManager behaviour = playedBy.getBehaviour();
        behaviour.setBlockPlacementLeft(1024);
        behaviour.setMovementsRemaining(1024);
        behaviour.setCanClimb(true);
        behaviour.setCanBuildDomeEverywhere(true);
    }

    @Override
    public void move(Worker worker, int desiredX, int desiredY, Island island) throws InvalidMovementException, WinningException {
        int actualX = worker.getPosition()[0];
        int actualY = worker.getPosition()[1];
        island.moveWorker(worker, desiredX, desiredY);
        int oldAltitudeOfPlayer = island.getCellCluster(actualX, actualY).getCostructionHeight();
        super.checkWin(island, desiredX, desiredY, oldAltitudeOfPlayer);

    }

    @Override
    public void build(Worker worker, BlockTypeEnum block, int desiredX, int desiredY, Island island) throws InvalidBuildException {
        island.buildBlock(block, desiredX, desiredY);
    }



}
