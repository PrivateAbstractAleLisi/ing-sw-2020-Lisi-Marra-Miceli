package it.polimi.ingsw.psp58.event.gameEvents.match;

import it.polimi.ingsw.psp58.event.core.EventListener;
import it.polimi.ingsw.psp58.event.gameEvents.GameEvent;

public class CV_IslandUpdateEvent extends GameEvent {
    private final String newIsland;
    private final String recipient;

    public CV_IslandUpdateEvent(String description, String newIsland) {
        super(description);
        this.newIsland = newIsland;
        recipient = "";
    }

    public CV_IslandUpdateEvent(String description, String newIsland, String recipient) {
        super(description);
        this.newIsland = newIsland;
        this.recipient = recipient;
    }

    @Override
    public void notifyHandler(EventListener listener) {
        listener.handleEvent(this);
    }

    public String getNewIsland() {
        return newIsland;
    }

    public String getRecipient() {
        return recipient;
    }

    public boolean isRecipientSet (){
        return !recipient.equals("");
    }
}
