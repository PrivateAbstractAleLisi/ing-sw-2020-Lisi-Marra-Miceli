package event.gameEvents.lobby;

import event.core.EventListener;
import event.gameEvents.GameEvent;

public class VC_RoomSizeResponseGameEvent extends GameEvent {

    private final int size;

    public int getSize() {
        return size;
    }

    @Override
    public String getEventDescription() {
        return super.getEventDescription();
    }

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }

    public VC_RoomSizeResponseGameEvent(String description, int size) {
        super(description);
        this.size = size;
    }
}
