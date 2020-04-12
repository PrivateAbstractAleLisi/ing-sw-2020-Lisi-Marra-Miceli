package view;

import controller.Lobby;
import event.core.EventListener;
import event.core.EventSource;
import event.core.ListenerType;
import event.events.*;
import placeholders.VirtualServer;

public class VirtualView extends EventSource implements EventListener {
    Lobby lobby;
    VirtualServer virtualServer;

    public VirtualView(Lobby lobby, VirtualServer virtualServer) {
        this.lobby = lobby;
        this.virtualServer = virtualServer;
        attachListenerByType(ListenerType.VIEW, lobby);
        lobby.attachListenerByType(ListenerType.VIEW, this);
    }

    @Override
    public void handleEvent(GameEvent event) {

    }

    @Override
    public void handleEvent(RoomSizeResponseGameEvent event) {

    }

    @Override
    public void handleEvent(ConnectionRequestGameEvent event) {
        notifyAllObserverByType(ListenerType.VIEW, event);
    }

    @Override
    public void handleEvent(RoomSizeRequestGameEvent event) {
        virtualServer.handleEvent(event);
    }

    @Override
    public void handleEvent(ConnectionRejectedErrorGameEvent event) {

    }
}
