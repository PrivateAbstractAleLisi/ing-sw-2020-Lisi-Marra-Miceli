package it.polimi.ingsw.psp58.networking.server;

import it.polimi.ingsw.psp58.controller.Lobby;
import it.polimi.ingsw.psp58.event.core.EventSource;
import it.polimi.ingsw.psp58.event.gameEvents.ControllerGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.GameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.PingEvent;
import it.polimi.ingsw.psp58.view.VirtualView;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import static it.polimi.ingsw.psp58.event.core.ListenerType.CONTROLLER;
import static it.polimi.ingsw.psp58.event.core.ListenerType.VIEW;

public class SantoriniServerClientHandler extends EventSource implements Runnable {

    private Socket clientSocket;
    private String localUsername;
    private String threadID;
    private Thread ping;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    private final boolean pingStamp;

    VirtualView clientVV;

    public SantoriniServerClientHandler(Socket clientSocket, String threadID, boolean pingStamp) {
        this.clientSocket = clientSocket;
        this.clientVV = new VirtualView(this);
        makeConnections();
        input = null;
        this.threadID = threadID;
        this.pingStamp = pingStamp;
    }

    @Override
    public void run() {
        try {
            handleClientConnection();
        } catch (IOException | ClassNotFoundException e) { //TODO Disconnects?
            connectionLost("client disconnected, socket error");
            e.printStackTrace();
        }
    }

    private void makeConnections() {
        this.attachListenerByType(CONTROLLER, clientVV);
    }

    private void stopPing() {
        ping.interrupt();
    }

    /**
     * called when this client disconnects because socket timeout has expired
     */
    private void connectionLost(String reason) {
        System.out.println("Thread: " + threadID + "- Method connectionLost called: " + clientSocket.getInetAddress().toString() + " port: " + clientSocket.getPort());

        //if the connection is not lost caused by another player crashed
        if (!clientVV.isAnotherPlayerInRoomCrashed()) {

            //if the Lobby has my username saved in usernameList I should clean
            if (clientVV.isUserInLobbyList()) {
                try {
                    closeSocketConnection();
                    Thread disco = new Thread() {
                        public void run() {
                            Lobby.instance().handleClientDisconnected(clientVV.getUsername());
                        }
                    };
                    disco.start();

                    //detach Lobby and the user that crashed
                    Lobby.instance().detachListenerByType(VIEW, clientVV);
                    clientVV.detachListenerByType(CONTROLLER,Lobby.instance());

                    System.out.println("client socket closed: " + clientSocket.getInetAddress() + " port: " + clientSocket.getPort());
                    Thread.currentThread().interrupt();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //Even if Lobby doesn't already saved my username, I detach myself
                Lobby.instance().detachListenerByType(VIEW, clientVV);
                clientVV.detachListenerByType(CONTROLLER,Lobby.instance());
                Thread.currentThread().interrupt();
            }
        }

    }

    private void handleClientConnection() throws IOException, ClassNotFoundException {

        //Client is connected via socket, creating a virtual it.polimi.ingsw.sp58.view for it

        try {
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            input = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }


        //read further events
        GameEvent received = null;
        try {
            while (true) {


                received = (GameEvent) input.readObject();
//                System.out.println("received correctly");

                if (received != null) {
                    if (received instanceof PingEvent) {
                        if (pingStamp) {
                            System.out.println("SERVER: " + received.getEventDescription() + " from " + threadID);
                        }
                    } else {
                        notifyAllObserverByType(CONTROLLER,(ControllerGameEvent) received);
                    }
                }


            }
        } catch (SocketTimeoutException | SocketException | EOFException to) { //No message from the client
            System.out.println("This client is AFK, Disconnecting");
            System.out.println(clientSocket.getInetAddress().toString() + " port: " + clientSocket.getPort());

            connectionLost("this client has crashed");
        } finally {
            if (clientSocket != null) {
                clientSocket.close();
            }
        }
    }

    public void sendEvent(GameEvent event){
        try {
            output.writeObject(event);
            output.flush();
        } catch (IOException e) {
            System.out.println("virtual it.polimi.ingsw.sp58.view: unable to send socket to client");
        }
    }

    public void disconnect(){
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error closing socket after NewGameResponseEvent");
        }
    }

    public String getThreadID() {
        return threadID;
    }

    public void closeSocketConnection() throws IOException {

        clientSocket.close();
    }

    public InetAddress getUserIP() {
        return clientSocket.getInetAddress();
    }

    public int getUserPort() {
        return clientSocket.getPort();
    }
}
