package it.polimi.ingsw.psp58.networking.server;

import it.polimi.ingsw.psp58.controller.Lobby;
import it.polimi.ingsw.psp58.event.core.EventSource;
import it.polimi.ingsw.psp58.event.gameEvents.ControllerGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.GameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.connection.PingEvent;
import it.polimi.ingsw.psp58.view.VirtualView;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static it.polimi.ingsw.psp58.event.core.ListenerType.CONTROLLER;
import static it.polimi.ingsw.psp58.event.core.ListenerType.VIEW;

/**
 * server thread that handles the client connection and socket read/write methods
 */
public class ClientHandler extends EventSource implements Runnable {

    private final Socket clientSocket;
    private final String threadID;
    private Thread ping;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    private final boolean pingStamp;
    private final boolean enablePing;

    /**
     * virtual view which represents client's View in MVC
     */
    final VirtualView clientVV;

    public ClientHandler(Socket clientSocket, String threadID, boolean pingStamp, boolean enablePing) {
        this.clientSocket = clientSocket;
        this.clientVV = new VirtualView(this);
        makeConnections();
        input = null;
        this.threadID = threadID;
        this.pingStamp = pingStamp;
        this.enablePing = enablePing;
    }

    @Override
    public void run() {
        try {
            handleClientConnection();

        } catch (IOException | ClassNotFoundException e) {
            connectionLost();
            e.printStackTrace();
        }
    }

    /**
     * attach the virtual view as a listener (Observer pattern) to this
     */
    private void makeConnections() {
        this.attachListenerByType(CONTROLLER, clientVV);
    }

    private void stopPing() {
        ping.interrupt();
    }

    /**
     * called when this client disconnects because socket timeout has expired, it starts the disconnection process and detaches the virtual view from the listeners' list
     */
    private void connectionLost() {
        printLogMessage("Method connectionLost called: " + clientSocket.getInetAddress().toString() + " port: " + clientSocket.getPort());
        if (enablePing && ping.isAlive()) {
            stopPing();
        }
        //if the connection is not lost caused by another player crashed
        if (!clientVV.isAnotherPlayerInRoomCrashed()) {

            //if the Lobby has my username saved in usernameList I should clean
            if (clientVV.isUserInLobbyList()) {
                try {
                    closeSocketConnection();
                    Thread disco = new Thread(() -> Lobby.instance().handleClientDisconnected(clientVV.getUsername()));
                    disco.start();

                    //detach Lobby and the user that crashed
                    Lobby.instance().detachListenerByType(VIEW, clientVV);
                    clientVV.detachListenerByType(CONTROLLER, Lobby.instance());

                    printLogMessage("Client socket closed: " + clientSocket.getInetAddress() + " port: " + clientSocket.getPort());
                    Thread.currentThread().interrupt();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //Even if Lobby doesn't already saved my username, I detach myself
                Lobby.instance().detachListenerByType(VIEW, clientVV);
                clientVV.detachListenerByType(CONTROLLER, Lobby.instance());
                Thread.currentThread().interrupt();
            }
        }

    }

    /**
     * called when the tread is started
     * @throws IOException error opening the object input/output stream
     * @throws ClassNotFoundException throw when there is an error casting the received event
     */
    private void handleClientConnection() throws IOException, ClassNotFoundException {

        //Client is connected via socket, creating a virtual view for it

        try {
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            input = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //For testing purpose it's possible to disable ping
        if (enablePing) {
            startPing();
        }

        //read further events
        GameEvent received;
        try {
            while (true) {
                received = (GameEvent) input.readObject();

                if (received != null) {
                    if (received instanceof PingEvent) {
                        if (pingStamp) {
                            printLogMessage(received.getEventDescription() + " from " + threadID);
                        }
                    } else {
                        notifyAllObserverByType(CONTROLLER, (ControllerGameEvent) received);
                    }
                }


            }
        } catch (SocketTimeoutException | SocketException | EOFException to) { //No message from the client
            printLogMessage("This client is AFK, Disconnecting");
            System.out.println(clientSocket.getInetAddress().toString() + " port: " + clientSocket.getPort());

            connectionLost();
        } finally {
            clientSocket.close();
        }
    }

    /**
     * it starts a pinging service from the server to the client writing a ping event to the socket every 5000 millis
     */
    public void startPing() {
        ping = new Thread(() -> {

            try {
                int counter = 0;
                while (true) {
                    Thread.sleep(5000);
                    output.writeObject(new PingEvent("Ping #" + counter));
                    counter++;
                }
            } catch (InterruptedException e) {
               printLogMessage("Ping Interrupted");
            } catch (IOException e) {
                printLogMessage("Unable to send event to client");
            } finally {
                Thread.currentThread().interrupt();
            }
        });

        ping.start();
    }

    /**
     * sends an event through the socket and flushed the output stream
     * @param event the event that would be sent through the socket
     */
    public void sendEvent(GameEvent event) {
        try {
            output.writeObject(event);
            output.flush();
        } catch (IOException e) {
            printLogMessage("virtual view: unable to send socket to client");
        }
    }

    /**
     * closes the clientSocket
     */
    public void disconnect() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            printLogMessage("Error closing socket after NewGameResponseEvent");
        }
    }

    /**
     * closes the clientSocket but propagates the exception
     * @throws IOException IO Exception
     */
    public void closeSocketConnection() throws IOException {

        clientSocket.close();
    }


    public InetAddress getUserIP() {
        return clientSocket.getInetAddress();
    }

    public int getUserPort() {
        return clientSocket.getPort();
    }

    /**
     * displays a message using a logger on the server
     * @param message message to print
     */
    private void printLogMessage(String message) {
        String timestamp = ZonedDateTime.now(ZoneId.of("Europe/Rome")).format(DateTimeFormatter.ofPattern("yyyy/MM/dd-HH:mm:ss"));
        System.out.println("SERVER (" + timestamp + ") at " + threadID + ": " + message);
    }
}
