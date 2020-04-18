package event.gameEvents.prematch;

import event.core.EventListener;
import event.gameEvents.GameEvent;

public class CV_PlayerPlaceWorkersRequestEvent extends GameEvent {
    private String actingPlayer;

    public CV_PlayerPlaceWorkersRequestEvent(String description, String actingPlayer) {
        super(description);
        this.actingPlayer = actingPlayer;
    }

    public String getActingPlayer() {
        return actingPlayer;
    }

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }
}
