package it.polimi.ingsw.psp58.event.gameEvents.match;

import it.polimi.ingsw.psp58.auxiliary.IslandData;
import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;

public class CV_IslandUpdateEvent extends ViewGameEvent {
    private final IslandData newIsland;
    private final String recipient;

    public CV_IslandUpdateEvent(String description, IslandData newIsland) {
        super(description);
        this.newIsland = newIsland;
        recipient = "";
    }

    public CV_IslandUpdateEvent(String description, IslandData newIsland, String recipient) {
        super(description);
        this.newIsland = newIsland;
        this.recipient = recipient;
    }

    @Override
    public void notifyHandler(ViewListener listener) {
        listener.handleEvent(this);
    }

    public IslandData getNewIsland() {
        return newIsland;
    }

    public String getRecipient() {
        return recipient;
    }

    public boolean isRecipientSet (){
        return !recipient.equals("");
    }
}
