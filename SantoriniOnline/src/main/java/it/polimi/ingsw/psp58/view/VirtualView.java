package it.polimi.ingsw.psp58.view;

import it.polimi.ingsw.psp58.controller.Lobby;
import it.polimi.ingsw.psp58.event.PlayerDisconnectedGameEvent;
import it.polimi.ingsw.psp58.event.core.EventListener;
import it.polimi.ingsw.psp58.event.core.EventSource;
import it.polimi.ingsw.psp58.event.gameEvents.CV_GameErrorGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.GameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.PingEvent;
import it.polimi.ingsw.psp58.event.gameEvents.lobby.*;
import it.polimi.ingsw.psp58.event.gameEvents.match.*;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.*;
import it.polimi.ingsw.psp58.model.gamemap.Worker;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import static it.polimi.ingsw.psp58.event.core.ListenerType.VIEW;

public class VirtualView extends EventSource implements EventListener {


    private final Lobby lobby;

    //todo AFTER DEBUG: make private
    private InetAddress userIP;
    private int userPort;
    private String username;
    private ObjectOutputStream output;

    //boolean for first connection
    private AtomicBoolean userConnectionAccepted;
    //lock for first connection
    private ReentrantLock userConnectionAcceptedLock = new ReentrantLock();


    //Boolean for disconnections
    private boolean userInLobbyList;
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
        userInLobbyList = false;
        userConnectionAccepted = new AtomicBoolean(false);
    }


    private void sendEventToClient(GameEvent event) {
        try {
            output.writeObject(event);
            output.flush();
        } catch (IOException e) {
            System.out.println("virtual it.polimi.ingsw.sp58.view: unable to send socket to client");
        }

    }

    public boolean isAnotherPlayerInRoomCrashed() {
        return anotherPlayerInRoomCrashed;
    }

    public String getUsername() {
        return username;
    }

    public boolean isUserInLobbyList() {
        return userInLobbyList;
    }

    //TO CONTROLLER

    @Override
    public void handleEvent(VC_ConnectionRequestGameEvent event) {
        //I suppose user will be added in the list of active users with the current username
        userInLobbyList = true;

        userConnectionAcceptedLock.lock();
        userConnectionAccepted.set(true);
        userConnectionAcceptedLock.unlock();

        this.userIP = client.getInetAddress();
        this.userPort = client.getPort();
        this.username = event.getUsername();
        CC_ConnectionRequestGameEvent newServerRequest = new CC_ConnectionRequestGameEvent(event.getEventDescription(), userIP, userPort, this, username);
        notifyAllObserverByType(VIEW, newServerRequest);

        userConnectionAcceptedLock.lock();
        try{
            //if the connection is not accepted, don't ask for start pregame
            if (userConnectionAccepted.get()) {
                if (lobby.canStartPreGameForThisUser(this.username)) {
                    detachListenerByType(VIEW, lobby);
                    try {
                        //Sleep 2 second to show the Room
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    lobby.startPreGameForThisUser(username);
                }
            }
        }finally {
            userConnectionAcceptedLock.unlock();
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
                System.err.println("Error when handling quit connection it.polimi.ingsw.sp58.event");
                e.printStackTrace();
            } finally {
                //deatch for all user in room
                lobby.detachListenerByType(VIEW, this);
                this.detachListenerByType(VIEW, lobby);
            }
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
            //set to false the connection accepted
            userConnectionAcceptedLock.lock();
            userConnectionAccepted.set(false);
            userConnectionAcceptedLock.unlock();

            sendEventToClient(event);
        }
    }

    @Override
    public void handleEvent(CV_RoomUpdateGameEvent event) {

        //set to false the connection accepted
        userConnectionAcceptedLock.lock();
        userConnectionAccepted.set(true);
        userConnectionAcceptedLock.unlock();

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
            //I set to false the boolean
            userInLobbyList = false;

            //set to false the connection accepted
            userConnectionAcceptedLock.lock();
            userConnectionAccepted.set(false);
            userConnectionAcceptedLock.unlock();

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
