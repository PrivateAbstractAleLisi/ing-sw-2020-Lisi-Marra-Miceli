package it.polimi.ingsw.psp58.event.gameEvents.connection;

import it.polimi.ingsw.psp58.event.gameEvents.GameEvent;

/**
 * An event sent by both the client and the server. It's needed to keep the connection alive and not let
 * the socket timeout expire
 */
public class PingEvent extends GameEvent {

    public PingEvent(String description) {
        super(description);
    }

}
