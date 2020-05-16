package it.polimi.ingsw.psp58.event.gameEvents;

public abstract class ControllerGameEvent extends GameEvent {


    public ControllerGameEvent(String description) {
        super(description);
    }

    public String getEventDescription() {
        return super.getEventDescription();
    }

}
