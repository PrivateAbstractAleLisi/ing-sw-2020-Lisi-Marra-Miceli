package it.polimi.ingsw.psp58.event.gameEvents;

/**
 * Generic event sent by the client to the Controller
 */
public abstract class ControllerGameEvent extends GameEvent {

    public ControllerGameEvent(String description) {
        super(description);
    }

}
