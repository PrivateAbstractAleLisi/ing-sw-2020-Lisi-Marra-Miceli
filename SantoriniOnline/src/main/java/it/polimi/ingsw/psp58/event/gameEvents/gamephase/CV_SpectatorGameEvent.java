package it.polimi.ingsw.psp58.event.gameEvents.gamephase;

import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;

public class CV_SpectatorGameEvent extends ViewGameEvent {
    private final String spectatorPlayer;

    public CV_SpectatorGameEvent(String description, String spectatorPlayer) {
        super(description);
        this.spectatorPlayer = spectatorPlayer;
    }

    public String getSpectatorPlayer() {
        return spectatorPlayer;
    }

    @Override
    public void notifyHandler(ViewListener listener) {
        listener.handleEvent(this);
    }
}