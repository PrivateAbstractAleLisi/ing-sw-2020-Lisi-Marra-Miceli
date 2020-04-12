package event.events;

public class ConnectionSuccessfulGameEvent extends GameEvent {

    private final String username;

    public ConnectionSuccessfulGameEvent(String description, String username) {
        super(description);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }


}
