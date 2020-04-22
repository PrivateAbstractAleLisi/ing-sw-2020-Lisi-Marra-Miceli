package event.gameEvents.match;

import event.core.EventListener;
import event.gameEvents.GameEvent;

public class CV_IslandUpdateEvent extends GameEvent {
    private final String newIsland;

    public CV_IslandUpdateEvent(String description, String newIsland) {
        super(description);
        this.newIsland = newIsland;
    }

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }

    public String getNewIsland() {
        return newIsland;
    }
}
