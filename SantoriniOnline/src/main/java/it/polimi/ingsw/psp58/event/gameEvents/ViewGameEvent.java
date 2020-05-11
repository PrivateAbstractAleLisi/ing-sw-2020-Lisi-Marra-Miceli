package it.polimi.ingsw.psp58.event.gameEvents;

import it.polimi.ingsw.psp58.event.core.ViewListener;

public abstract class ViewGameEvent extends GameEvent {
    private String decription;

    public ViewGameEvent(String description) {
        super(description);

    }

    public String getEventDecription() {
        return decription;
    }

    public abstract void notifyHandler(ViewListener listener);

}
