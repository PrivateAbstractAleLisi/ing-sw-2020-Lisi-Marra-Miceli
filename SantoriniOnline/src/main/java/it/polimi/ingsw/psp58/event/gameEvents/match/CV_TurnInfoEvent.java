package it.polimi.ingsw.psp58.event.gameEvents.match;

import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;

public class CV_TurnInfoEvent extends ViewGameEvent {
    private final int movementsRemaining;
    private final int buildRemaining;
    private final boolean canClimb;
    private final String actingPlayer;

    public CV_TurnInfoEvent(String description, int movementsRemaining, int buildRemaining, boolean canClimb, String actingPlayer) {
        super(description);
        this.movementsRemaining=movementsRemaining;
        this.buildRemaining= buildRemaining;
        this.canClimb=canClimb;
        this.actingPlayer = actingPlayer;
    }

    public String getActingPlayer() {
        return actingPlayer;
    }

    public int getMovementsRemaining() {
        return movementsRemaining;
    }

    public int getBuildRemaining() {
        return buildRemaining;
    }

    public String getCanClimb() {
        if(canClimb) return "yes";
        else return "no";
    }

    @Override
    public void notifyHandler(ViewListener viewListener) {
        viewListener.handleEvent(this);
    }
}
