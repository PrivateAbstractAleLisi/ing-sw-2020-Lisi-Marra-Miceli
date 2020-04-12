
package event.events.temporary;

import event.events.GameEvent;

/**
 * @author Alessandro Lisi
 *
 */
public class WelcomeGameEvent extends GameEvent {

    private final String welcomeMessage;

    public WelcomeGameEvent(String welcomeMessage, String description) {
        super(description);
        this.welcomeMessage = welcomeMessage;
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public String getWelcomeMessageUpperCase() {
        return welcomeMessage.toUpperCase();
    }
}
