package event.gameEvents.match;

import event.core.EventListener;
import event.gameEvents.GameEvent;

import java.util.List;

public class CV_GameOverEvent extends GameEvent {
    private final String winner;
    private final List<String> losers;

    public CV_GameOverEvent(String description, String winner, List<String> losers) {
        super(description);
        this.winner = winner;
        this.losers = losers;
    }

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }

    public String getWinner() {
        return winner;
    }

    public List<String> getLosers() {
        return losers;
    }
}
