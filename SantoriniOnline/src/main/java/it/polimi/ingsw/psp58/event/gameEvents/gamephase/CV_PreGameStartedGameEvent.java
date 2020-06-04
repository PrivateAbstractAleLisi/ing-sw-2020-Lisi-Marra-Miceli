package it.polimi.ingsw.psp58.event.gameEvents.gamephase;

import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;

import java.util.List;

public class CV_PreGameStartedGameEvent extends ViewGameEvent {
    private final String challenger;

    private final List<String> playersList;

    public CV_PreGameStartedGameEvent(String description, String challenger, List<String> playersList) {
        super(description);
        this.challenger = challenger;
        this.playersList = playersList;
    }

    public String getChallenger() {
        return challenger;
    }

    public List<String> getPlayersList() {
        return playersList;
    }

    @Override
    public void notifyHandler(ViewListener listener) {
        listener.handleEvent(this);
    }
}