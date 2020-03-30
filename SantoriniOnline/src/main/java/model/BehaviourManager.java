package model;

/**
 * The {@code BehaviourManager class} store the information about what actions the {@link Player} can do during his turn.
 * <p>
 *     The {@code attributes} could be modified by every {@link Player} because some God Powers can change behaviour of enemies workers.
 * </p>
 * The {@code class} has default value for all the information stored based on standard rules shared with most of Gods.
 */
public class BehaviourManager {
    private int movementsRemaining;
    private int blockPlacementLeft;
    private boolean canClimb;
    private boolean canBuildDomeEverywhere;

    /**
     * Standard {@code Creator method} that set each attribute to the default value.
     */
    public BehaviourManager() {
        movementsRemaining = 1;
        blockPlacementLeft = 1;
        canClimb=true;
        canBuildDomeEverywhere=false;
    }

//    public void resetDefaultBehaviour() {
//        movementsRemaining = 1;
//        blockPlacementLeft = 1;
//        canClimb=true;
//        canBuildDomeEverywhere=false;
//    }

    public int getBlockPlacementLeft() {
        return blockPlacementLeft;
    }

    public int getMovementsRemaining() {
        return movementsRemaining;
    }

    public boolean isCanBuildDomeEverywhere() {
        return canBuildDomeEverywhere;
    }

    public boolean isCanClimb() {
        return canClimb;
    }

    /**
     * The {@code setMovementsRemaining method} set the value of {@code movementsRemaining}
     * @param movementsRemaining Value of remaining moves to do for the {@link Player}
     */
    public void setMovementsRemaining(int movementsRemaining) {
        this.movementsRemaining = movementsRemaining;
    }

    /**
     * The {@code setBlockPlacementLeft method} set the value of {@code blockPlacementLeft}
     * @param blockPlacementLeft Value of remaining blocks to Place for each player
     */
    public void setBlockPlacementLeft(int blockPlacementLeft) {
        this.blockPlacementLeft = blockPlacementLeft;
    }

    public void setCanBuildDomeEverywhere(boolean canBuildDomeEverywhere) {
        this.canBuildDomeEverywhere = canBuildDomeEverywhere;
    }

    public void setCanClimb(boolean canClimb) {
        this.canClimb = canClimb;
    }


}


