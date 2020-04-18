package view;

import controller.Lobby;
import event.core.EventListener;
import event.core.EventSource;
import event.core.ListenerType;
import event.gameEvents.*;
import event.gameEvents.lobby.*;
import event.gameEvents.prematch.*;
import networking.SantoriniServerSender;
import placeholders.VirtualServer;

import java.net.InetAddress;
import java.net.Socket;

public class VirtualView extends EventSource implements EventListener {


    private Lobby lobby;

    //todo AFTER DEBUG: make private
    public InetAddress userIP;
    public int userPort;
    public String username;

    Socket client;

    public VirtualView(Socket clientSocket) {
        this.lobby = Lobby.instance();
        //listening to each other
        attachListenerByType(ListenerType.VIEW, lobby);
        lobby.attachListenerByType(ListenerType.VIEW, this);
        client = clientSocket;
    }

    private void sendEventToClient(GameEvent event) {
        SantoriniServerSender sender = new SantoriniServerSender(client, event);
        Thread senderThread = new Thread(sender, "TH_send_event");
        senderThread.start();

    }
    //TO CONTROLLER

    @Override
    public void handleEvent(VC_ConnectionRequestGameEvent event) {

        //todo modificare con Server/socket
        System.out.println("virtual view ha ricevuto");
        this.userIP = client.getInetAddress();
        this.userPort = client.getPort();
        this.username = event.getUsername();
        CC_ConnectionRequestGameEvent newServerRequest = new CC_ConnectionRequestGameEvent(event.getEventDescription(), userIP, userPort, this, event.getUsername());
        notifyAllObserverByType(ListenerType.VIEW, newServerRequest);

    }

    @Override
    public void handleEvent(VC_RoomSizeResponseGameEvent event) {
        notifyAllObserverByType(ListenerType.VIEW, event);
    }

    @Override
    public void handleEvent(VC_ChallengerCardsChosenEvent event) {
        notifyAllObserverByType(ListenerType.VIEW, event);
    }

    @Override
    public void handleEvent(VC_PlayerCardChosenEvent event) {
        notifyAllObserverByType(ListenerType.VIEW, event);
    }

    @Override
    public void handleEvent(VC_ChallengerChosenFirstPlayerEvent event) {
        notifyAllObserverByType(ListenerType.VIEW, event);
    }



    //TO VIEW
    @Override
    public void handleEvent(CV_RoomSizeRequestGameEvent event) {
        if (event.getUsername().equals(this.username)) {
            sendEventToClient(event);
        }
    }

    @Override
    public void handleEvent(CV_RoomUpdateGameEvent event) {
        sendEventToClient(event);
    }

    @Override
    public void handleEvent(CV_ChallengerChosenEvent event) {
        if (event.getUsername().equals(this.username)) {
            sendEventToClient(event);
        }
    }

    @Override
    public void handleEvent(CV_CardChoiceRequestGameEvent event) {
        if (event.getUsername().equals(this.username)) {
            sendEventToClient(event);
        }
    }

    @Override
    public void handleEvent(CV_WaitGameEvent event) {
        if (event.getRecipient().equals(this.username)) {
            sendEventToClient(event);
        }
    }

    @Override
    public void handleEvent(CV_ChallengerChooseFirstPlayerRequestEvent event) {
        if (event.getChallenger().equals(this.username)) {
            sendEventToClient(event);
        }
    }

    @Override
    public void handleEvent(CV_ConnectionRejectedErrorGameEvent event) {
        if (event.getUserIP().equals(userIP) && event.getUserPort() == userPort) {
            sendEventToClient(event);
        }
    }



//    NOT IMPLEMENTED
    @Override
    public void handleEvent(GameEvent event) {

    }

    @Override
    public void handleEvent(CC_ConnectionRequestGameEvent event) {

    }
}
