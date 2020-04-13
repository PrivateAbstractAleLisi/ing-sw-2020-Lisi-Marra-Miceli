package event.core;

import event.events.*;
import event.events.prematch.ToViewCardChoiceRequestGameEvent;
import event.events.prematch.ToViewWaitGameEvent;

/**
 * @author Alessandro Lisi
 * <p>
 * A listener that gets notified when an EventSource object status is changed or when it generates a GameEvent
 */
public interface EventListener {

    /* one handler for each event class */
    public void handleEvent(GameEvent event);

    void handleEvent(RoomSizeResponseGameEvent event);

    void handleEvent(RoomUpdateGameEvent event);

    void handleEvent(ConnectionRequestGameEvent event);

    void handleEvent(ConnectionRequestServerGameEvent event);

    public void handleEvent(RoomSizeRequestGameEvent event);

    public void handleEvent(ConnectionRejectedErrorGameEvent event);

    public void handleEvent(ChallengerCardsChosenEvent event);

    public void handleEvent(PlayerCardChosenEvent event);

    public void handleEvent(ChallengerChosenFirstPlayerEvent event);
    public void handleEvent(ChallengerChosenEvent event);

    public void handleEvent(ToViewCardChoiceRequestGameEvent event);
    public void handleEvent(ToViewWaitGameEvent event);

}

