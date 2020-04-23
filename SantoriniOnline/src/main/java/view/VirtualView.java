package view;

import controller.Lobby;
import event.core.EventListener;
import event.core.EventSource;
import event.core.ListenerType;
import event.gameEvents.CV_GameErrorGameEvent;
import event.gameEvents.prematch.CV_WaitPreMatchGameEvent;
import event.gameEvents.GameEvent;
import event.gameEvents.lobby.*;
import event.gameEvents.match.*;
import event.gameEvents.prematch.*;
import model.gamemap.Worker;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class VirtualView extends EventSource implements EventListener {


    private final Lobby lobby;

    //todo AFTER DEBUG: make private
    public InetAddress userIP;
    public int userPort;
    public String username;
    private ObjectOutputStream output;

    Socket client;

    public VirtualView(Socket clientSocket) {
        this.lobby = Lobby.instance();
        //listening to each other
        attachListenerByType(ListenerType.VIEW, lobby);
        lobby.attachListenerByType(ListenerType.VIEW, this);
        client = clientSocket;
        try {
            output = new ObjectOutputStream(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendEventToClient(GameEvent event) {
        try {
            output.writeObject(event);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        if (lobby.canStartPreRoom0()) {
            detachListenerByType(ListenerType.VIEW, lobby);
            Lobby.instance().startPreGameForRoom0();
        }
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

    @Override
    public void handleEvent(VC_PlayerPlacedWorkerEvent event) {
        notifyAllObserverByType(ListenerType.VIEW, event);
        if (event.getId() == Worker.IDs.B) {
            if (lobby.canStartGameForThisUser(username)) {
                lobby.startGameForThisUser(username);
            }
        }
    }

    @Override
    public void handleEvent(VC_PlayerCommandGameEvent event) {
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
    public void handleEvent(CV_GameStartedGameEvent event) {
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
    public void handleEvent(CV_WaitPreMatchGameEvent event) {
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
    public void handleEvent(CV_PlayerPlaceWorkerRequestEvent event) {
        if (event.getActingPlayer().equals(this.username)) {
            sendEventToClient(event);
        }
    }

    @Override
    public void handleEvent(CV_ConnectionRejectedErrorGameEvent event) {
        if (event.getUserIP().equals(userIP) && event.getUserPort() == userPort) {
            sendEventToClient(event);
        }
    }

    @Override
    public void handleEvent(CV_NewTurnEvent event) {
        sendEventToClient(event);
    }

    @Override
    public void handleEvent(CV_CommandRequestEvent event) {
        if (event.getActingPlayer().equals(this.username)) {
            sendEventToClient(event);
        }
    }

    @Override
    public void handleEvent(CV_GameOverEvent event) {
        sendEventToClient(event);
    }

    @Override
    public void handleEvent(CV_GameErrorGameEvent event) {
        if (event.getToUsername().equals(this.username)) {
            sendEventToClient(event);
        }
    }

    @Override
    public void handleEvent(CV_IslandUpdateEvent event) {
        if (event.isRecipientSet()) {
            if (event.getRecipient().equals(this.username)) {
                sendEventToClient(event);
            }
        } else {
            sendEventToClient(event);
        }
    }

    @Override
    public void handleEvent(CV_WaitMatchGameEvent event) {
        sendEventToClient(event);
    }


    //    NOT IMPLEMENTED
    @Override
    public void handleEvent(GameEvent event) {

    }

    @Override
    public void handleEvent(CC_ConnectionRequestGameEvent event) {

    }
}
