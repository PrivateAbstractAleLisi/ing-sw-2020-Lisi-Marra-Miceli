package event.events;

public class RoomSizeResponseGameEvent extends GameEvent {

    private final int size;

    public int getSize() {
        return size;
    }

    @Override
    public String getEventDescription() {
        return super.getEventDescription();
    }

    public RoomSizeResponseGameEvent(String description, int size) {
        super(description);
        this.size = size;
    }
}
