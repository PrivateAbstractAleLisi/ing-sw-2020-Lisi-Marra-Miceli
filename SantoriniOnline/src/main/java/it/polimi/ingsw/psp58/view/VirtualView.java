package it.polimi.ingsw.psp58.view;

import it.polimi.ingsw.psp58.controller.Lobby;
import it.polimi.ingsw.psp58.event.core.ControllerListener;
import it.polimi.ingsw.psp58.event.core.EventSource;
import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.CV_GameErrorGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.GameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.PingEvent;
import it.polimi.ingsw.psp58.event.gameEvents.PlayerDisconnectedViewEvent;
import it.polimi.ingsw.psp58.event.gameEvents.lobby.*;
import it.polimi.ingsw.psp58.event.gameEvents.match.*;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.*;
import it.polimi.ingsw.psp58.model.gamemap.Worker;
import it.polimi.ingsw.psp58.networking.server.SantoriniServerClientHandler;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import static it.polimi.ingsw.psp58.event.core.ListenerType.CONTROLLER;
import static it.polimi.ingsw.psp58.event.core.ListenerType.VIEW;

public class VirtualView extends EventSource implements ViewListener, ControllerListener {


    private final Lobby lobby;
    private String username;

    //boolean for first connection
    private AtomicBoolean userConnectionAccepted;
    //lock for first connection
    private ReentrantLock userConnectionAcceptedLock = new ReentrantLock();


    //Boolean for disconnections
    private boolean userInLobbyList;
    private boolean anotherPlayerInRoomCrashed;

    private SantoriniServerClientHandler client;

    public VirtualView(SantoriniServerClientHandler client) {
        this.lobby = Lobby.instance();
        //listening to each other
        attachListenerByType(CONTROLLER,lobby);
        lobby.attachListenerByType(VIEW, this);
        this.client = client;

        anotherPlayerInRoomCrashed = false;
        userInLobbyList = false;
        userConnectionAccepted = new AtomicBoolean(false);
    }


    private void sendEventToClient(GameEvent event) {
        client.sendEvent(event);
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
        this.username = event.getUsername();
        CC_ConnectionRequestGameEvent newServerRequest = new CC_ConnectionRequestGameEvent(event.getEventDescription(), client.getUserIP(), client.getUserPort(), this, username);
        notifyAllObserverByType(CONTROLLER, newServerRequest);

        tryStartPreGame();
    }

    @Override
    public void handleEvent(VC_RoomSizeResponseGameEvent event) {
        notifyAllObserverByType(CONTROLLER,event);
    }

    @Override
    public void handleEvent(VC_ChallengerCardsChosenEvent event) {
        notifyAllObserverByType(CONTROLLER,event);
    }

    @Override
    public void handleEvent(VC_PlayerCardChosenEvent event) {
        notifyAllObserverByType(CONTROLLER, event);
    }

    @Override
    public void handleEvent(VC_ChallengerChosenFirstPlayerEvent event) {
        notifyAllObserverByType(CONTROLLER, event);
    }

    @Override
    public void handleEvent(VC_PlayerPlacedWorkerEvent event) {
        notifyAllObserverByType(CONTROLLER, event);
        if (event.getId() == Worker.IDs.B) {
            if (lobby.canStartGameForThisUser(username)) {
                lobby.startGameForThisUser(username);
            }
        }
    }

    @Override
    public void handleEvent(VC_PlayerCommandGameEvent event) {
        notifyAllObserverByType(CONTROLLER, event);

        if (lobby.canCleanRoomForThisUser(username)) {
            lobby.cleanRoomForThisUser(username);
        }
    }

    @Override
    public void handleEvent(VC_NewGameResponseEvent event) {
        System.out.println("È arrivata la request: la risposta è " + event.createNewGame());

        if (event.createNewGame()) {
            userConnectionAcceptedLock.lock();
            userConnectionAccepted.set(true);
            userConnectionAcceptedLock.unlock();

            CC_NewGameResponseEvent newGameResponseEvent=new CC_NewGameResponseEvent("", this.username, this);
            notifyAllObserverByType(CONTROLLER, newGameResponseEvent);

            tryStartPreGame();
        } else {
            //if the player doesn't want to play i close the connection
            client.disconnect();
        }
    }

    private void tryStartPreGame() {
        userConnectionAcceptedLock.lock();
        try {
            //if the connection is not accepted, don't ask for start pregame
            if (userConnectionAccepted.get()) {
                if (lobby.canStartPreGameForThisUser(this.username)) {
                    try {
                        //Sleep 2 second to show the Room
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    lobby.startPreGameForThisUser(username);
                }
            }
        } finally {
            userConnectionAcceptedLock.unlock();
        }
    }

    @Override
    public void handleEvent(PlayerDisconnectedViewEvent event) {
        if (!event.getDisconnectedUsername().equals(this.username)) {
            sendEventToClient(event);
            anotherPlayerInRoomCrashed = true;
            client.disconnect();
            //detach for all user in room
            lobby.detachListenerByType(VIEW,this);
            this.detachListenerByType(CONTROLLER, lobby);
        }
    }


    private void removeListener() {
        Lobby.instance().detachListenerByType(VIEW, this);
        detachListenerByType(CONTROLLER, lobby);

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
        if (event.getUserIP().equals(client.getUserIP()) && event.getUserPort() == client.getUserPort()) {
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
    public void handleEvent(CV_ReconnectionRejectedErrorGameEvent event) {
        if (event.getUsername().equals(this.username)) {
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

    @Override
    public void handleEvent(CV_NewGameRequestEvent event) {
        sendEventToClient(event);
    }


    //    NOT IMPLEMENTED


    @Override
    public void handleEvent(CC_ConnectionRequestGameEvent event) {

    }

    @Override
    public void handleEvent(CC_NewGameResponseEvent event) {

    }

    public void handleEvent(PingEvent event) {
        sendEventToClient(event);
    }
}
