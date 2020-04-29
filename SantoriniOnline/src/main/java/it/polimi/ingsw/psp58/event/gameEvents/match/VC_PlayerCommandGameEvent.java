package it.polimi.ingsw.psp58.event.gameEvents.match;

import it.polimi.ingsw.psp58.event.core.EventListener;
import it.polimi.ingsw.psp58.event.gameEvents.GameEvent;
import it.polimi.ingsw.psp58.model.TurnAction;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;
import it.polimi.ingsw.psp58.model.gamemap.Worker;

import java.util.Arrays;

public class VC_PlayerCommandGameEvent extends GameEvent {

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
    public void notifyHandler(EventListener listener) {
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
}
