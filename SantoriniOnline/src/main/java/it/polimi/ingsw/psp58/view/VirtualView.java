package it.polimi.ingsw.psp58.view;

import it.polimi.ingsw.psp58.controller.Lobby;
import it.polimi.ingsw.psp58.event.core.ControllerListener;
import it.polimi.ingsw.psp58.event.core.EventSource;
import it.polimi.ingsw.psp58.event.core.ViewListener;
import it.polimi.ingsw.psp58.event.gameEvents.*;
import it.polimi.ingsw.psp58.event.gameEvents.connection.PingEvent;
import it.polimi.ingsw.psp58.event.gameEvents.connection.PlayerDisconnectedViewEvent;
import it.polimi.ingsw.psp58.event.gameEvents.gamephase.CV_GameStartedGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.gamephase.CV_PreGameStartedGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.gamephase.CV_SpectatorGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.lobby.*;
import it.polimi.ingsw.psp58.event.gameEvents.match.*;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.*;
import it.polimi.ingsw.psp58.event.gameEvents.gamephase.CV_WorkerPlacementGameEvent;
import it.polimi.ingsw.psp58.model.gamemap.Worker;
import it.polimi.ingsw.psp58.networking.server.SantoriniServerClientHandler;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import static it.polimi.ingsw.psp58.event.core.ListenerType.CONTROLLER;
import static it.polimi.ingsw.psp58.event.core.ListenerType.VIEW;

/**
 * Virtual View class, it simulates the View in the Server side and each client has associated an unique instance of this class.
 * The class decides which events need to be send to the client and which to the server.
 */
public class VirtualView extends EventSource implements ViewListener, ControllerListener {
    /**
     * Lobby of this server.
     */
    private final Lobby lobby;
    /**
     * My username.
     */
    private String username;

    /**
     * {@link AtomicBoolean} for the first connection, is true if the connection has been accepted and, after the disconnection, Lobby has to be clean.
     */
    private final AtomicBoolean userConnectionAccepted;
    /**
     * {@link ReentrantLock} lock for first connection
     */
    private final ReentrantLock userConnectionAcceptedLock = new ReentrantLock();

    //Boolean for disconnections
    /**
     * Boolean value, is true when the Lobby accepted the username and added in the Users List.
     */
    private boolean userInLobbyList;
    /**
     * Boolean value, is true when another Player, in the same room of this player, caused the end of the game.
     */
    private boolean anotherPlayerInRoomCrashed;

    /**
     * Where the socket is handled.
     */
    private final SantoriniServerClientHandler client;

    /**
     * Create the VirtualView and prepare to receive the first event.
     *
     * @param client {@code SantoriniServerClientHandler} with the ref to the Socket.
     */
    public VirtualView(SantoriniServerClientHandler client) {
        this.lobby = Lobby.instance();
        //listening to each other
        attachListenerByType(CONTROLLER, lobby);
        lobby.attachListenerByType(VIEW, this);
        this.client = client;

        anotherPlayerInRoomCrashed = false;
        userInLobbyList = false;
        userConnectionAccepted = new AtomicBoolean(false);
    }

    /**
     * Send an event to the client throw the socket.
     *
     * @param event event to send
     */
    private void sendEventToClient(GameEvent event) {
        client.sendEvent(event);
    }

    /**
     * Check if another Player, in the same room of this player, caused the end of the game.
     *
     * @return true when another Player, in the same room of this player, caused the end of the game.
     */
    public boolean isAnotherPlayerInRoomCrashed() {
        return anotherPlayerInRoomCrashed;
    }

    /**
     * Obtain the username of the player
     *
     * @return The username of the player.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Check if the player username has been memorized into the Users List in lobby.
     *
     * @return true when the Lobby accepted the username and added in the Users List
     */
    public boolean isUserInLobbyList() {
        return userInLobbyList;
    }

    //TO CONTROLLER

    /**
     * Handle the {@link VC_ConnectionRequestGameEvent} event, save some data like username and forward the event creating a new {@link CC_ConnectionRequestGameEvent} event.
     * This method also try to start the PreGame phase after the forwarding of the event.
     *
     * @param event Event received from the client.
     */
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

    /**
     * Handle the received event from the client and forward it to the controller.
     *
     * @param event Event received from the client.
     */
    @Override
    public void handleEvent(VC_RoomSizeResponseGameEvent event) {
        notifyAllObserverByType(CONTROLLER, event);
    }

    /**
     * Handle the received event from the client and forward it to the controller.
     *
     * @param event Event received from the client.
     */
    @Override
    public void handleEvent(VC_ChallengerCardsChosenEvent event) {
        notifyAllObserverByType(CONTROLLER, event);
    }

    /**
     * Handle the received event from the client and forward it to the controller.
     *
     * @param event Event received from the client.
     */
    @Override
    public void handleEvent(VC_PlayerCardChosenEvent event) {
        notifyAllObserverByType(CONTROLLER, event);
    }

    /**
     * Handle the received event from the client and forward it to the controller.
     *
     * @param event Event received from the client.
     */
    @Override
    public void handleEvent(VC_ChallengerChosenFirstPlayerEvent event) {
        notifyAllObserverByType(CONTROLLER, event);
    }

    /**
     * Handle the received event from the client and forward it to the controller.
     * If this event is the last of the PreGame, try to start the Game Phase on the server.
     *
     * @param event Event received from the client.
     */
    @Override
    public void handleEvent(VC_PlayerPlacedWorkerEvent event) {
        notifyAllObserverByType(CONTROLLER, event);
        if (event.getId() == Worker.IDs.B && lobby.canStartGameForThisUser(username)) {
            lobby.startGameForThisUser(username);
        }
    }

    /**
     * Handle the received event from the client and forward it to the controller.
     * Try to clean the room after this event to see if the match is ended.
     *
     * @param event Event received from the client.
     */
    @Override
    public void handleEvent(VC_PlayerCommandGameEvent event) {
        notifyAllObserverByType(CONTROLLER, event);

        if (lobby.canCleanRoomForThisUser(username)) {
            lobby.cleanRoomForThisUser(username);
        }
    }

    /**
     * Handle the {@link VC_NewGameResponseEvent} event, save some data like username and forward the event creating a new {@link CC_ConnectionRequestGameEvent} event.
     * This method also try to start the PreGame phase after the forwarding of the event.
     *
     * @param event Event received from the client.
     */
    @Override
    public void handleEvent(VC_NewGameResponseEvent event) {
        System.out.println("User desire to play again? Response: " + event.createNewGame());

        if (event.createNewGame()) {
            userConnectionAcceptedLock.lock();
            userConnectionAccepted.set(true);
            userConnectionAcceptedLock.unlock();

            CC_NewGameResponseEvent newGameResponseEvent = new CC_NewGameResponseEvent("", this.username, this);
            notifyAllObserverByType(CONTROLLER, newGameResponseEvent);

            tryStartPreGame();
        } else {
            //if the player doesn't want to play i close the connection
            client.disconnect();
        }
    }

    /**
     * Check if the PreGame can start and, if yes, start it.
     */
    private void tryStartPreGame() {
        userConnectionAcceptedLock.lock();
        try {
            //if the connection is not accepted, don't ask for start pregame
            if (userConnectionAccepted.get() && lobby.canStartPreGameForThisUser(this.username)) {
                lobby.startPreGameForThisUser(username);
            }
        } finally {
            userConnectionAcceptedLock.unlock();
        }
    }

    //TO VIEW

    /**
     * If the username is mine, forward the event to the client.
     *
     * @param event Event received from the server.
     */
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

    /**
     * Forward the event to the client and set {@code userConnectionAccepted} to true.
     *
     * @param event Event received from the server.
     */
    @Override
    public void handleEvent(CV_RoomUpdateGameEvent event) {

        //set to false the connection accepted
        userConnectionAcceptedLock.lock();
        userConnectionAccepted.set(true);
        userConnectionAcceptedLock.unlock();

        sendEventToClient(event);
    }

    /**
     * Forward the event to the client.
     *
     * @param event Event received from the server.
     */
    @Override
    public void handleEvent(CV_PreGameStartedGameEvent event) {
        sendEventToClient(event);
    }

    /**
     * If the username is mine, forward the event to the client.
     *
     * @param event Event received from the server.
     */
    @Override
    public void handleEvent(CV_PreGameErrorGameEvent event) {
        if (event.getToUsername().equals(this.username)) {
            sendEventToClient(event);
        }
    }

    /**
     * Forward the event to the client.
     *
     * @param event Event received from the server.
     */
    @Override
    public void handleEvent(CV_GameStartedGameEvent event) {
        sendEventToClient(event);
    }

    /**
     * If the username is mine, forward the event to the client.
     *
     * @param event Event received from the server.
     */
    @Override
    public void handleEvent(CV_ChallengerChosenEvent event) {
        if (event.getUsername().equals(this.username)) {
            sendEventToClient(event);
        }
    }

    /**
     * If the username is mine, forward the event to the client.
     *
     * @param event Event received from the server.
     */
    @Override
    public void handleEvent(CV_CardChoiceRequestGameEvent event) {
        if (event.getUsername().equals(this.username)) {
            sendEventToClient(event);
        }
    }

    /**
     * If the username is mine, forward the event to the client.
     *
     * @param event Event received from the server.
     */
    @Override
    public void handleEvent(CV_WaitPreMatchGameEvent event) {
        if (event.getRecipient().equals(this.username)) {
            sendEventToClient(event);
        }
    }

    /**
     * Forward the event to the client.
     *
     * @param event Event received from the server.
     */
    @Override
    public void handleEvent(CV_WorkerPlacementGameEvent event) {
        sendEventToClient(event);
    }

    /**
     * If the username is mine, forward the event to the client.
     *
     * @param event Event received from the server.
     */
    @Override
    public void handleEvent(CV_CommandExecutedGameEvent event) {
        if (event.getRecipient().equals(this.username)) {
            sendEventToClient(event);
        }
    }

    /**
     * If the username is mine, forward the event to the client.
     *
     * @param event Event received from the server.
     */
    @Override
    public void handleEvent(CV_ChallengerChooseFirstPlayerRequestEvent event) {
        if (event.getChallenger().equals(this.username)) {
            sendEventToClient(event);
        }
    }

    /**
     * If the username is mine, forward the event to the client.
     *
     * @param event Event received from the server.
     */
    @Override
    public void handleEvent(CV_PlayerPlaceWorkerRequestEvent event) {
        if (event.getActingPlayer().equals(this.username)) {
            sendEventToClient(event);
        }
    }

    /**
     * If the IP is mine and the Port is mine, set userInLobbyList and userConnectionAccepted to false and forward the event to the client.
     *
     * @param event Event received from the server.
     */
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

    /**
     * If the username is mine, set userConnectionAccepted to false and forward the event to the client.
     *
     * @param event Event received from the server.
     */
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

    /**
     * Forward the event to the client.
     *
     * @param event Event received from the server.
     */
    @Override
    public void handleEvent(CV_NewTurnEvent event) {
        sendEventToClient(event);
    }

    /**
     * If the username is mine, forward the event to the client.
     *
     * @param event Event received from the server.
     */
    @Override
    public void handleEvent(CV_CommandRequestEvent event) {
        if (event.getActingPlayer().equals(this.username)) {
            sendEventToClient(event);
        }
    }

    /**
     * Forward the event to the client.
     *
     * @param event Event received from the server.
     */
    @Override
    public void handleEvent(CV_GameOverEvent event) {
        sendEventToClient(event);
    }

    /**
     * If the username is mine, forward the event to the client.
     *
     * @param event Event received from the server.
     */
    @Override
    public void handleEvent(CV_GameErrorGameEvent event) {
        if (event.getToUsername().equals(this.username)) {
            sendEventToClient(event);
        }
    }

    /**
     * Forward the event to the client.
     *
     * @param event Event received from the server.
     */
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

    /**
     * Forward the event to the client.
     *
     * @param event Event received from the server.
     */
    @Override
    public void handleEvent(CV_WaitMatchGameEvent event) {
        sendEventToClient(event);
    }

    /**
     * If the username is mine, forward the event to the client.
     *
     * @param event Event received from the server.
     */
    @Override
    public void handleEvent(CV_TurnInfoEvent event) {
        if (event.getActingPlayer().equals(this.username)) {
            sendEventToClient(event);
        }
    }

    /**
     * Forward the event to the client.
     *
     * @param event Event received from the server.
     */
    @Override
    public void handleEvent(CV_SpectatorGameEvent event) {
        sendEventToClient(event);
    }

    /**
     * Forward the event to the client.
     *
     * @param event Event received from the server.
     */
    @Override
    public void handleEvent(CV_NewGameRequestEvent event) {
        sendEventToClient(event);
    }

    /**
     * Handle the received event from the server and forward it to the client and after disconnect this user.
     *
     * @param event Event received from the client.
     */
    @Override
    public void handleEvent(PlayerDisconnectedViewEvent event) {
        if (!event.getDisconnectedUsername().equals(this.username)) {
            sendEventToClient(event);
            anotherPlayerInRoomCrashed = true;
            client.disconnect();
            //detach for all user in room
            lobby.detachListenerByType(VIEW, this);
            this.detachListenerByType(CONTROLLER, lobby);
        }
    }

    //    NOT IMPLEMENTED
    @Override
    public void handleEvent(CC_ConnectionRequestGameEvent event) {
        //NOT IMPLEMENTED IN THIS CLASS
    }

    @Override
    public void handleEvent(CC_NewGameResponseEvent event) {
        //NOT IMPLEMENTED IN THIS CLASS
    }

    public void handleEvent(PingEvent event) {
        sendEventToClient(event);
    }
}
