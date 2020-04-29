package it.polimi.ingsw.psp58.event.gameEvents.prematch;

import it.polimi.ingsw.psp58.event.core.EventListener;
import it.polimi.ingsw.psp58.event.gameEvents.GameEvent;
import it.polimi.ingsw.psp58.model.gamemap.Worker;

public class VC_PlayerPlacedWorkerEvent extends GameEvent {


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
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }
}
