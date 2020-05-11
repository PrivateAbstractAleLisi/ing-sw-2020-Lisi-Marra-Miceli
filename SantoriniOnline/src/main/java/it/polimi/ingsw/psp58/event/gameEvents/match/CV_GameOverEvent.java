package it.polimi.ingsw.psp58.event.gameEvents.match;

import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;

import java.util.List;

public class CV_GameOverEvent extends ViewGameEvent {
    private final String winner;
    private final List<String> losers;

    public CV_GameOverEvent(String description, String winner, List<String> losers) {
        super(description);
        this.winner = winner;
        this.losers = losers;
    }

    @Override
    public void notifyHandler(ViewListener listener) {
        listener.handleEvent(this);
    }

    public String getWinner() {
        return winner;
    }

    public List<String> getLosers() {
        return losers;
    }
}
