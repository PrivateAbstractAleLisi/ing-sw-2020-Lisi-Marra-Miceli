package view;

import controller.Lobby;
import event.core.EventListener;
import event.core.EventSource;
import event.core.ListenerType;
import event.gameEvents.*;
import event.gameEvents.lobby.*;
import event.gameEvents.prematch.*;
import placeholders.VirtualServer;

public class VirtualView extends EventSource implements EventListener {
    Lobby lobby;
    VirtualServer virtualServer;

    //todo AFTER DEBUG: make private
    public String userIP;
   public  int userPort;

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
    public void handleEvent(VC_RoomSizeResponseGameEvent event) {
        notifyAllObserverByType(ListenerType.VIEW, event);
    }

    @Override
    public void handleEvent(CV_RoomUpdateGameEvent event) {
        virtualServer.handleEvent(event);
    }

    @Override
    public void handleEvent(VC_ConnectionRequestGameEvent event) {
        //todo mofidicare con Server/socket

        this.userIP = "192.168.1.1";
        this.userPort = 12345;
        CC_ConnectionRequestGameEvent newServerRequest = new CC_ConnectionRequestGameEvent(event.getEventDescription(), userIP, userPort,this, event.getUsername());
        notifyAllObserverByType(ListenerType.VIEW, newServerRequest);

    }

    @Override
    public void handleEvent(CC_ConnectionRequestGameEvent event) {

    }

    @Override
    public void handleEvent(CV_RoomSizeRequestGameEvent event) {
        virtualServer.handleEvent(event);
    }

    @Override
    public void handleEvent(CV_ConnectionRejectedErrorGameEvent event) {
        if (event.getUserIP() == userIP && event.getUserPort() == userPort) {
            virtualServer.handleEvent(event);
        }
    }

    @Override
    public void handleEvent(VC_ChallengerCardsChosenEvent event) {

    }

    @Override
    public void handleEvent(VC_PlayerCardChosenEvent event) {

    }

    @Override
    public void handleEvent(VC_ChallengerChosenFirstPlayerEvent event) {

    }

    @Override
    public void handleEvent(CV_ChallengerChosenEvent event) {

    }

    @Override
    public void handleEvent(CV_CardChoiceRequestGameEvent event) {

    }

    @Override
    public void handleEvent(CV_WaitGameEvent event) {

    }
}
