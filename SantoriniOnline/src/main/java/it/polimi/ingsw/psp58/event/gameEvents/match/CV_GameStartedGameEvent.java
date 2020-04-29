package it.polimi.ingsw.psp58.event.gameEvents.match;

import it.polimi.ingsw.psp58.event.core.EventListener;
import it.polimi.ingsw.psp58.event.gameEvents.GameEvent;

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