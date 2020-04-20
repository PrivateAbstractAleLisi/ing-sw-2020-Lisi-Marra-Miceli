package event.gameEvents.match;

import event.core.EventListener;
import event.gameEvents.GameEvent;

public class CV_GameStartedGameEvent extends GameEvent {
    String firstPlayer;

    public CV_GameStartedGameEvent(String description, String firstPlayer) {
        super(description);
        this.firstPlayer = firstPlayer;
    }

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }
}