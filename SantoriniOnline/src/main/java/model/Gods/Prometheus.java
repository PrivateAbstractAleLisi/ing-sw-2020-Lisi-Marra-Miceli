package model.Gods;

import model.BehaviourManager;
import model.Card;
import model.Player;
import model.exception.InvalidBuildException;
import model.exception.InvalidMovementException;
import model.exception.WinningException;
import model.gamemap.BlockTypeEnum;
import model.gamemap.Island;
import model.gamemap.Worker;

public class Prometheus extends Card {

    private boolean hasAlreadyBuilt;

    public Prometheus(Player player) {
        super(player);
        name = "Prometheus";
        description = "If your Worker does not move up, it may build both before and after moving.";
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
