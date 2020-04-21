package event.gameEvents.match;

import event.core.EventListener;
import event.gameEvents.GameEvent;
import model.TurnAction;
import model.gamemap.Worker;

public class VC_PlayerCommandGameEvent extends GameEvent {

    public final TurnAction command;
    public final String fromPlayer;
    private final int[] position;
    private final Worker.IDs workerID;

    public VC_PlayerCommandGameEvent(String description, TurnAction command, String fromPlayer, int[] position, Worker.IDs workerID) {
        super(description);
        this.command = command;
        this.fromPlayer = fromPlayer;
        this.position = position;
        this.workerID = workerID;
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
}
