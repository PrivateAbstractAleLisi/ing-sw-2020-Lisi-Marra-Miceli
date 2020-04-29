package it.polimi.ingsw.psp58.model.gods;

import it.polimi.ingsw.psp58.model.BehaviourManager;
import it.polimi.ingsw.psp58.model.Card;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.model.Player;
import it.polimi.ingsw.psp58.exceptions.InvalidBuildException;
import it.polimi.ingsw.psp58.exceptions.InvalidMovementException;
import it.polimi.ingsw.psp58.exceptions.WinningException;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;
import it.polimi.ingsw.psp58.model.gamemap.Island;
import it.polimi.ingsw.psp58.model.gamemap.Worker;

public class Prometheus extends Card {

    private boolean hasAlreadyBuilt;

    public Prometheus(Player player) {
        super(player);
        name = CardEnum.PROMETHEUS;

        hasAlreadyBuilt = false;
    }

    @Override
    public void move(Worker worker, int desiredX, int desiredY, Island island) throws InvalidMovementException, WinningException {
        if (hasAlreadyBuilt) {
            BehaviourManager behaviour = playedBy.getBehaviour();
            behaviour.setCanClimb(false);
            behaviour.setBlockPlacementLeft(1);
        }
        super.move(worker, desiredX, desiredY, island);
    }

    @Override
    public void build(Worker worker, BlockTypeEnum block, int desiredX, int desiredY, Island island) throws InvalidBuildException, CloneNotSupportedException {
        super.build(worker, block, desiredX, desiredY, island);
        hasAlreadyBuilt = true;
    }

    @Override
    public void resetBehaviour() {
        BehaviourManager behaviour = playedBy.getBehaviour();
        behaviour.setBlockPlacementLeft(1);
        behaviour.setMovementsRemaining(1);
        behaviour.setCanClimb(true);
        behaviour.setCanBuildDomeEverywhere(false);
        hasAlreadyBuilt = false;
    }
}
