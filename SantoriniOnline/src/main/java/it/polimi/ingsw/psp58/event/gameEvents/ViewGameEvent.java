package it.polimi.ingsw.psp58.event.gameEvents;

/**
 * Generic event sent by the Controller to the view
 */
public abstract class ViewGameEvent extends GameEvent {

    public ViewGameEvent(String description) {
        super(description);
    }

}
