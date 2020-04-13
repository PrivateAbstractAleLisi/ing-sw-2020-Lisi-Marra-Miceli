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
    String userIP;
    int userPort;

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
        notifyAllObserverByType(ListenerType.VIEW, event);
    }

    @Override
    public void handleEvent(RoomUpdateGameEvent event) {
        virtualServer.handleEvent(event);
    }

    @Override
    public void handleEvent(ConnectionRequestGameEvent event) {
        //todo mofidicare con Server/socket

        this.userIP = "192.168.1.1";
        this.userPort = 12345;
        ConnectionRequestServerGameEvent newServerRequest = new ConnectionRequestServerGameEvent(event.getEventDescription(), userIP, userPort,this, event.getUsername());
        notifyAllObserverByType(ListenerType.VIEW, newServerRequest);

    }

    @Override
    public void handleEvent(ConnectionRequestServerGameEvent event) {

    }

    @Override
    public void handleEvent(RoomSizeRequestGameEvent event) {
        virtualServer.handleEvent(event);
    }

    @Override
    public void handleEvent(ConnectionRejectedErrorGameEvent event) {
        if (event.getUserIP() == userIP && event.getUserPort() == userPort) {
            virtualServer.handleEvent(event);
        }
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
}
