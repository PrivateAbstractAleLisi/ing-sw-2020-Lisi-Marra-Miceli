package event.core;

import event.gameEvents.*;
import event.gameEvents.lobby.*;
import event.gameEvents.match.CV_GameStartedGameEvent;
import event.gameEvents.prematch.*;

/**
 * @author Alessandro Lisi
 * <p>
 * A listener that gets notified when an EventSource object status is changed or when it generates a GameEvent
 */
public interface EventListener {

    /* one handler for each event class */
    public void handleEvent(GameEvent event);

    void handleEvent(VC_RoomSizeResponseGameEvent event);

    void handleEvent(CV_RoomUpdateGameEvent event);

    void handleEvent(CV_GameStartedGameEvent event);

    void handleEvent(VC_ConnectionRequestGameEvent event);

    void handleEvent(CC_ConnectionRequestGameEvent event);

    public void handleEvent(CV_RoomSizeRequestGameEvent event);

    public void handleEvent(CV_ConnectionRejectedErrorGameEvent event);

    public void handleEvent(VC_ChallengerCardsChosenEvent event);

    public void handleEvent(VC_PlayerCardChosenEvent event);

    public void handleEvent(VC_ChallengerChosenFirstPlayerEvent event);
    public void handleEvent(CV_ChallengerChosenEvent event);

    public void handleEvent(CV_CardChoiceRequestGameEvent event);
    public void handleEvent(CV_WaitGameEvent event);

    public void handleEvent(CV_ChallengerChooseFirstPlayerRequestEvent event);
    public void handleEvent(VC_PlayerPlacedWorkerEvent event);
    public void handleEvent(CV_PlayerPlaceWorkerRequestEvent event);

//    public void handleEvent(CV)
}

