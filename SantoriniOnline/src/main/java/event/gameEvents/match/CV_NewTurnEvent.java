package event.gameEvents.match;

import event.core.EventListener;
import event.gameEvents.GameEvent;

import java.util.List;

public class CV_NewTurnEvent extends GameEvent {
    private String player;
    private final List<String> turnRotation;
    private final String username;

    public CV_NewTurnEvent(String description, String player, List<String> turnRotation, String username) {
        super(description);
        this.player = player;
        this.turnRotation = turnRotation;
        this.username = username;
    }

    public String getPlayer() {
        return player;
    }

    public List<String> getTurnRotation() {
        return turnRotation;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }
}
