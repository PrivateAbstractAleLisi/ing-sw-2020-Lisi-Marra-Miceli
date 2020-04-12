package event.core;

import event.events.*;

/**
 * @author Alessandro Lisi
 * <p>
 * A listener that gets notified when an EventSource object status is changed or when it generates a GameEvent
 */
public interface EventListener {

    /* one handler for each event class */
    public void handleEvent(GameEvent event);

    void handleEvent(RoomSizeResponseGameEvent event);

    void handleEvent(ConnectionRequestGameEvent event);

    public void handleEvent(RoomSizeRequestGameEvent event);

    public void handleEvent(ConnectionRejectedErrorGameEvent event);

}

