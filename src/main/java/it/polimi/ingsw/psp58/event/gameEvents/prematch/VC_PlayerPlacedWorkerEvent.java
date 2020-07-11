package it.polimi.ingsw.psp58.event.gameEvents.prematch;

import it.polimi.ingsw.psp58.event.core.ControllerListener;
import it.polimi.ingsw.psp58.event.gameEvents.ControllerGameEvent;
import it.polimi.ingsw.psp58.model.gamemap.Worker;

/**
 * Event sent by the client to the server responding to a {@link CV_PlayerPlaceWorkerRequestEvent} when the player has to place a worker.
 * Contains the id of the worker to place and the x and y position.
 */
public class VC_PlayerPlacedWorkerEvent extends ControllerGameEvent {


    private final String actingPlayer;
    private final int posX;
    private final int posY;
    private final Worker.IDs id;

    public VC_PlayerPlacedWorkerEvent(String description, String actingPlayer, int posX, int posY, Worker.IDs id) {
        super(description);
        this.actingPlayer = actingPlayer;
        this.posX = posX;
        this.posY = posY;
        this.id = id;
    }

    public Worker.IDs getId() {
        return id;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public String getActingPlayer() {
        return actingPlayer;
    }

    @Override
    public void notifyHandler(ControllerListener listener) {
        listener.handleEvent(this);
    }
}
