package it.polimi.ingsw.psp58.event.gameEvents.match;

import it.polimi.ingsw.psp58.event.core.ControllerListener;
import it.polimi.ingsw.psp58.event.gameEvents.ControllerGameEvent;
import it.polimi.ingsw.psp58.model.TurnAction;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;
import it.polimi.ingsw.psp58.model.gamemap.Worker;

import java.util.Arrays;

/**
 * Event sent by the client to the server reponding to a {@link CV_CommandRequestEvent}.
 * Contains all the information about the command that the player wants to perform
 * such as the worker, the action, the position and the optional block to build.
 */
public class VC_PlayerCommandGameEvent extends ControllerGameEvent {

    public final TurnAction command;
    public final String fromPlayer;
    private final int[] position;
    private final Worker.IDs workerID;
    private final BlockTypeEnum blockToBuild;

    public VC_PlayerCommandGameEvent(String description, TurnAction command, String fromPlayer, int[] position, Worker.IDs workerID, BlockTypeEnum blockToBuild) {
        super(description);
        this.command = command;
        this.fromPlayer = fromPlayer;
        this.position = position;
        this.workerID = workerID;
        this.blockToBuild = blockToBuild;
    }

    @Override
    public void notifyHandler(ControllerListener listener) {
        listener.handleEvent(this);
    }

    public TurnAction getCommand() {
        return command;
    }

    public String getFromPlayer() {
        return fromPlayer;
    }

    public Worker.IDs getWorkerID() {
        return workerID;
    }

    public int[] getPosition() {
        return position;
    }

    public boolean isBlockSet() {
        return blockToBuild != null;
    }

    public BlockTypeEnum getBlockToBuild() {
        return blockToBuild;
    }

    @Override
    public String toString() {
        String block;
        if (blockToBuild == null) {
            block = "Not defined";
        } else {
            block = blockToBuild.toString();
        }

        return "Command=" + command +
                ", from Player='" + fromPlayer + '\'' +
                ", Position=" + Arrays.toString(position) +
                ", workerID=" + workerID +
                ", BlockToBuild=" + block;
    }

    public String toStringSmall() {
        String block;
        if (blockToBuild == null) {
            block = "Not defined";
        } else {
            block = blockToBuild.toString();
        }

        return "Command=" + command +
                ", Position=" + Arrays.toString(position) +
                ", workerID=" + workerID +
                ", BlockToBuild=" + block;
    }

    public boolean isCommandEventValid() {
        boolean isUsernameValid = getFromPlayer() != null ;
        switch (getCommand()) {
            case MOVE:
            case BUILD:
                boolean isWorkerValid = getWorkerID() != null;
                boolean isXPositionValid = getPosition()[0] >= 0 && getPosition()[0] < 5;
                boolean isYPositionValid = getPosition()[1] >= 0 && getPosition()[1] < 5;
                return isUsernameValid && isWorkerValid && isXPositionValid && isYPositionValid;
            case PASS:
                return isUsernameValid;
            default:
                return false;
        }
    }
}
