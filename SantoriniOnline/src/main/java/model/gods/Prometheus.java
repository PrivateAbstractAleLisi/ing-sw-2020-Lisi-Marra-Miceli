package model.gods;

import model.BehaviourManager;
import model.Card;
import model.CardEnum;
import model.Player;
import exceptions.InvalidBuildException;
import exceptions.InvalidMovementException;
import exceptions.WinningException;
import model.gamemap.BlockTypeEnum;
import model.gamemap.Island;
import model.gamemap.Worker;

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
