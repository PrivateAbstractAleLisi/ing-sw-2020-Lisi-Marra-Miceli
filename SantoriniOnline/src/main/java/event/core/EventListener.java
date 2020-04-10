package event.core;
import event.events.*;

/**
 * @author Alessandro Lisi
 *
 * A listener that gets notified when an EventSource object status is changed or when it generates a GameEvent
 */
public interface EventListener {

    /* one handler for each event class */
    public void handleEvent(GameEvent event);
    public void handleEvent(WelcomeGameEvent event);


}

