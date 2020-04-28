package view;

import controller.Lobby;
import event.PlayerDisconnectedGameEvent;
import event.core.EventListener;
import event.core.EventSource;
import event.gameEvents.CV_GameErrorGameEvent;
import event.gameEvents.GameEvent;
import event.gameEvents.PingEvent;
import event.gameEvents.lobby.*;
import event.gameEvents.match.*;
import event.gameEvents.prematch.*;
import model.gamemap.Worker;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import static event.core.ListenerType.VIEW;

public class VirtualView extends EventSource implements EventListener {


    private final Lobby lobby;

    //todo AFTER DEBUG: make private
    private InetAddress userIP;
    private int userPort;
    private String username;
    private ObjectOutputStream output;
    private boolean anotherPlayerInRoomCrashed;

    Socket client;

    public VirtualView(Socket clientSocket) {
        this.lobby = Lobby.instance();
        //listening to each other
        attachListenerByType(VIEW, lobby);
        lobby.attachListenerByType(VIEW, this);
        client = clientSocket;
        try {
            output = new ObjectOutputStream(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        anotherPlayerInRoomCrashed = false;
    }


    private void sendEventToClient(GameEvent event) {
        try {
            output.writeObject(event);
            output.flush();
        } catch (IOException e) {
            System.out.println("virtual view: unable to send socket to client");
        }

    }

    public boolean isAnotherPlayerInRoomCrashed() {
        return anotherPlayerInRoomCrashed;
    }

    public String getUsername() {
        return username;
    }

    //TO CONTROLLER

    @Override
    public void handleEvent(VC_ConnectionRequestGameEvent event) {
        this.userIP = client.getInetAddress();
        this.userPort = client.getPort();
        this.username = event.getUsername();
        CC_ConnectionRequestGameEvent newServerRequest = new CC_ConnectionRequestGameEvent(event.getEventDescription(), userIP, userPort, this, event.getUsername());
        notifyAllObserverByType(VIEW, newServerRequest);
        if (lobby.canStartPreRoom0()) {
            detachListenerByType(VIEW, lobby);
            try {
                //Sleep 2 second to show the Room
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Lobby.instance().startPreGameForRoom0();
        }
    }

    @Override
    public void handleEvent(VC_RoomSizeResponseGameEvent event) {
        notifyAllObserverByType(VIEW, event);
    }

    @Override
    public void handleEvent(VC_ChallengerCardsChosenEvent event) {
        notifyAllObserverByType(VIEW, event);
    }

    @Override
    public void handleEvent(VC_PlayerCardChosenEvent event) {
        notifyAllObserverByType(VIEW, event);
    }

    @Override
    public void handleEvent(VC_ChallengerChosenFirstPlayerEvent event) {
        notifyAllObserverByType(VIEW, event);
    }

    @Override
    public void handleEvent(VC_PlayerPlacedWorkerEvent event) {
        notifyAllObserverByType(VIEW, event);
        if (event.getId() == Worker.IDs.B) {
            if (lobby.canStartGameForThisUser(username)) {
                lobby.startGameForThisUser(username);
            }
        }
    }

    @Override
    public void handleEvent(VC_PlayerCommandGameEvent event) {
        notifyAllObserverByType(VIEW, event);
    }

    @Override
    public void handleEvent(PlayerDisconnectedGameEvent event) {
        if (!event.getDisconnectedUsername().equals(this.username)) {
            sendEventToClient(event);
            anotherPlayerInRoomCrashed = true;
            try {
                client.close();
                System.out.println("closing connection for client " + username);
            } catch (IOException e) {
                System.err.println("Error when handling quit connection event");
                e.printStackTrace();
            } finally {
                //deatch for all user in room
                lobby.detachListenerByType(VIEW, this);
                this.detachListenerByType(VIEW, lobby);
            }
        } else {
            //for the user that crashed
            lobby.detachListenerByType(VIEW, this);
            this.detachListenerByType(VIEW, lobby);
        }
    }

    private void removeListener() {
        Lobby.instance().detachListenerByType(VIEW, this);
        detachListenerByType(VIEW, lobby);

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

    public void handleEvent(PingEvent event) {
        sendEventToClient(event);
    }
}
