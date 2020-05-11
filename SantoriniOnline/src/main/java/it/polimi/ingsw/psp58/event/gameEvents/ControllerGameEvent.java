package it.polimi.ingsw.psp58.event.gameEvents;

import it.polimi.ingsw.psp58.event.core.ControllerListener;

public abstract class ControllerGameEvent extends GameEvent {


    public ControllerGameEvent(String description) {
        super(description);
    }

    public String getEventDescription() {
        return super.getEventDescription();
    }

    public abstract void notifyHandler(ControllerListener listener);

}
