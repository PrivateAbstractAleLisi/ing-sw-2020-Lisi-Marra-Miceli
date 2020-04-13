package placeholders;

import controller.Lobby;
import event.core.EventListener;
import event.core.EventSource;
import event.core.ListenerType;
import event.events.*;
import view.VirtualView;

public class VirtualServer extends EventSource implements EventListener {
    VirtualView virtualView;

    public VirtualServer() {
        virtualView = new VirtualView(new Lobby(), this);
    }

    @Override
    public void handleEvent(RoomSizeRequestGameEvent event) {
        notifyAllObserverByType(ListenerType.VIEW, event);
    }

    @Override
    public void handleEvent(ConnectionRejectedErrorGameEvent event) {
        notifyAllObserverByType(ListenerType.VIEW, event);
    }

    @Override
    public void handleEvent(ChallengerCardsChosenEvent event) {

    }

    @Override
    public void handleEvent(PlayerCardChosenEvent event) {

    }

    @Override
    public void handleEvent(ChallengerChosenFirstPlayerEvent event) {

    }

    @Override
    public void handleEvent(ChallengerChosenEvent event) {

    }

    @Override
    public void handleEvent(RoomSizeResponseGameEvent event) {
        virtualView.handleEvent(event);
    }

    @Override
    public void handleEvent(RoomUpdateGameEvent event) {
        notifyAllObserverByType(ListenerType.VIEW, event);
    }

    @Override
    public void handleEvent(ConnectionRequestGameEvent event) {
        virtualView.handleEvent(event);
    }

    @Override
    public void handleEvent(ConnectionRequestServerGameEvent event) {

    }


    @Override
    public void handleEvent(GameEvent event) {
        System.out.println("FIGAAA NON");
    }
}
