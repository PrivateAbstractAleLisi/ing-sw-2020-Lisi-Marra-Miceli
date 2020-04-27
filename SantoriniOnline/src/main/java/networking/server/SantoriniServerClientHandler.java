package networking.server;

import controller.Lobby;
import event.core.EventSource;
import event.gameEvents.GameEvent;
import event.gameEvents.PingEvent;
import view.UI.CLI.utility.MessageUtility;
import view.VirtualView;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import static event.core.ListenerType.VIEW;

public class SantoriniServerClientHandler extends EventSource implements Runnable {

    private Socket client;
    private String localUsername;
    private InetAddress localUserIP;
    private int localUserPort;
    private ObjectInputStream input;
    private String threadID;
    private Thread ping;

    VirtualView clientVV;

    public SantoriniServerClientHandler(Socket client, String threadID) {
        this.client = client;
        this.clientVV = new VirtualView(client);
        makeConnections();
        input = null;
        this.threadID = threadID;
    }

    @Override
    public void run() {
        try {

            ping = new Thread() {
                public void run() {

                    try {
                        while (true) {
                            Thread.sleep(1000);
                            if (!client.isClosed()) {
                                try {
                                    clientVV.handleEvent(new PingEvent("is there anybody in there?"));
                                }
                                catch (Exception e) {
                                    Thread.sleep(2000);
                                }
                            }
                        }
                    } catch (InterruptedException e) {

                    } finally {
                        System.out.println("thread di ping interrotto");
                        Thread.currentThread().interrupt();
                    }

                }
            };



            ping.start();

            handleClientConnection();

        } catch (IOException | ClassNotFoundException e) { //TODO Disconnects?
            staccaStacca("client disconnected, socket error");
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
    private void staccaStacca(String reason) {
        try {
            closeSocketConnection();
            Thread disco = new Thread() {
                public void run() {
                    Lobby.instance().handleClientDisconnected(clientVV.getUsername());
                }
            };
            disco.start();

            System.out.println("client socket closed: " + client.getInetAddress() + " port: " + client.getPort());
            stopPing();
            Thread.currentThread().interrupt();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void handleClientConnection() throws IOException, ClassNotFoundException {

        //Client is connected via socket, creating a virtual view for it
        ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));


        GameEvent eventReceived;

        //read further events


        GameEvent received = null;
        try {
            while (true) {

                received = (GameEvent) input.readObject();
                System.out.println("received correctly");

                    if (received != null) {
                        if (received instanceof PingEvent) {
                        } else {
                            notifyAllObserverByType(VIEW, received);
                        }
                    }


            }
        } catch (SocketTimeoutException | SocketException | EOFException to) { //No message from the client
            System.out.println(client.isClosed());
            System.out.println("This client is AFK, Disconnecting");
            System.out.println(client.getInetAddress().toString() + client.getLocalPort());

            staccaStacca("this client has crashed");
        }
        finally
        {
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
