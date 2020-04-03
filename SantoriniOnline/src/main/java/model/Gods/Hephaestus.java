package model.Gods;

import model.BehaviourManager;
import model.Card;
import model.Player;
import model.exception.InvalidBuildException;
import model.gamemap.BlockTypeEnum;
import model.gamemap.Island;
import model.gamemap.Worker;

public class Hephaestus extends Card {

    private int[] lastBuiltPosition;

    public Hephaestus(Player player) {
        super(player);
        name="Hephaestus";
        description="Your Worker may build one additional block (not dome) on top of your first block.";
        lastBuiltPosition = new int[]{-1, -1};
    }

    /**
     * It doesn't update the lastBuiltPosition, it just checks if you have already built here or not in the turn
     *
     * @param desiredX where you want to build
     * @param desiredY where you want to build
     * @return false never built in this turn, true he built here
     */
    private boolean hasAlreadyBuiltHereInThisTurn(int desiredX, int desiredY) {
        if ( lastBuiltPosition[1] == -1 && lastBuiltPosition[0] == -1 ) { //he never built in this turn
            return false;
        } else { //he build at least one block
            return lastBuiltPosition[0] == desiredX && lastBuiltPosition[1] == desiredY;
        }
    }

    @Override
    public void build(Worker worker, BlockTypeEnum block, int desiredX, int desiredY, Island island) throws InvalidBuildException, CloneNotSupportedException {
        if(!hasAlreadyBuiltHereInThisTurn(desiredX, desiredY)) {
            super.build(worker, block, desiredX, desiredY, island);
            lastBuiltPosition[0]=desiredX;
            lastBuiltPosition[1]=desiredY;
        }
        else {
            if(block == BlockTypeEnum.DOME) throw new InvalidBuildException("Cannot build a DOME");
            else {
                super.build(worker, block, desiredX, desiredY, island);
                lastBuiltPosition[0]=desiredX;
                lastBuiltPosition[1]=desiredY;
            }
        }
    }

    @Override
    public void resetBehaviour() {
        BehaviourManager behaviour = playedBy.getBehaviour();
        behaviour.setBlockPlacementLeft(2); //the worker may build one additional time
        behaviour.setMovementsRemaining(1);
        behaviour.setCanClimb(true);
        behaviour.setCanBuildDomeEverywhere(false);
        lastBuiltPosition = new int[]{-1, -1};
    }
}
