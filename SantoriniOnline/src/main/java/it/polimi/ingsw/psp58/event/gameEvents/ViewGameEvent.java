package it.polimi.ingsw.psp58.event.gameEvents;

public abstract class ViewGameEvent extends GameEvent {

    public ViewGameEvent(String description) {
        super(description);
    }

    public String getEventDescription() {
        return super.getEventDescription();
    }

}
