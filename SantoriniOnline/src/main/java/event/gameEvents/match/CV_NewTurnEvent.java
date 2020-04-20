package event.gameEvents.match;

import event.core.EventListener;
import event.gameEvents.GameEvent;

public class CV_NewTurnEvent extends GameEvent {
    private String player;

    public CV_NewTurnEvent(String description, String player) {
        super(description);
        this.player = player;
    }

    public String getPlayer() {
        return player;
    }

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }
}
