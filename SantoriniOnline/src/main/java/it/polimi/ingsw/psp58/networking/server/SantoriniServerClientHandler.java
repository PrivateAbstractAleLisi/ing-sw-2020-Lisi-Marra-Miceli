package it.polimi.ingsw.psp58.networking.server;

import it.polimi.ingsw.psp58.controller.Lobby;
import it.polimi.ingsw.psp58.event.core.EventSource;
import it.polimi.ingsw.psp58.event.gameEvents.GameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.PingEvent;
import it.polimi.ingsw.psp58.view.VirtualView;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import static it.polimi.ingsw.psp58.event.core.ListenerType.VIEW;

public class SantoriniServerClientHandler extends EventSource implements Runnable {

    private Socket client;
    private String localUsername;
    private InetAddress localUserIP;
    private int localUserPort;
    private ObjectInputStream input;
    private String threadID;
    private Thread ping;

    private final boolean pingStamp;

    VirtualView clientVV;

    public SantoriniServerClientHandler(Socket client, String threadID, boolean pingStamp) {
        this.client = client;
        this.clientVV = new VirtualView(client);
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
        this.attachListenerByType(VIEW, clientVV);
    }

    private void stopPing() {
        ping.interrupt();
    }

    /**
     * called when this client disconnects because socket timeout has expired
     */
    private void connectionLost(String reason) {
        System.out.println("Thread: " + threadID + "- Method connectionLost called: " + client.getInetAddress().toString() + " port: " + client.getPort());

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
                    clientVV.detachListenerByType(VIEW, Lobby.instance());

                    System.out.println("client socket closed: " + client.getInetAddress() + " port: " + client.getPort());
                    Thread.currentThread().interrupt();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //Even if Lobby doesn't already saved my username, I detach myself
                Lobby.instance().detachListenerByType(VIEW, clientVV);
                clientVV.detachListenerByType(VIEW, Lobby.instance());
                Thread.currentThread().interrupt();
            }
        }

    }

    private void handleClientConnection() throws IOException, ClassNotFoundException {

        //Client is connected via socket, creating a virtual it.polimi.ingsw.sp58.view for it
        ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));


        GameEvent eventReceived;

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
                        notifyAllObserverByType(VIEW, received);
                    }
                }


            }
        } catch (SocketTimeoutException | SocketException | EOFException to) { //No message from the client
            System.out.println(client.isClosed());
            System.out.println("This client is AFK, Disconnecting");
            System.out.println(client.getInetAddress().toString() + " port: " + client.getPort());

            connectionLost("this client has crashed");
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    public String getThreadID() {
        return threadID;
    }

    public void closeSocketConnection() throws IOException {

        client.close();
    }

}
