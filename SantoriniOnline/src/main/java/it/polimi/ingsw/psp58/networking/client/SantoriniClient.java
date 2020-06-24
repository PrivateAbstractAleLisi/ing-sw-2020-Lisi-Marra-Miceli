package it.polimi.ingsw.psp58.networking.client;

import it.polimi.ingsw.psp58.event.core.EventListener;
import it.polimi.ingsw.psp58.event.core.EventSource;
import it.polimi.ingsw.psp58.event.gameEvents.ControllerGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.connection.PingEvent;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;
import it.polimi.ingsw.psp58.view.UI.CLI.utility.MessageUtility;
import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import javafx.application.Platform;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import static it.polimi.ingsw.psp58.event.core.ListenerType.VIEW;

/**
 * This class is the process that reads and writes using the socket, sending and receiving events from the server. Incoming events are notified to the View
 */
public class SantoriniClient extends EventSource implements Runnable {

    /**
     * This client view, can be either CLI or GUI.
     */
    private EventListener userInterface;

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String IP;
    private Socket serverSocket;
    public static final int SERVER_PORT = 7557;
    public final int SOCKET_TIMEOUT_S = 20;

    private boolean connectionOpen = false;
    /**
     * if it's true the client will ping the server
     */
    private boolean enablePing;

    private GUI guiIstance;


    public SantoriniClient(EventListener userInterface, String ipAddress, boolean enablePing) {
        this.userInterface = userInterface;
        this.IP = ipAddress;
        this.enablePing = enablePing;
        attachListenerByType(VIEW, userInterface);
    }

    public SantoriniClient(EventListener userInterface, String ipAddress, boolean enablePing, GUI guiIstance) {
        this.userInterface = userInterface;
        this.IP = ipAddress;
        this.enablePing = enablePing;
        this.guiIstance = guiIstance;
        attachListenerByType(VIEW, userInterface);
    }

    /**
     * tries to open a socket with the server, the socket has a timeout. This function starts the pinging process too
     *
     * @throws IOException when it's unable to open a socket
     */
    public void begin() throws IOException {

        serverSocket = null;
        //Open a connection with the server
        try {
            serverSocket = new Socket(IP, SERVER_PORT);
            connectionOpen = true;
            if (enablePing) {
                serverSocket.setSoTimeout(SOCKET_TIMEOUT_S * 1000);
            }
        } catch (IOException e) {
            System.err.println("Client: Unable to open a socket");
            throw e;

        }
        if (enablePing) {


            Thread ping = new Thread(() -> {

                try {
                    int counter = 0;
                    while (true) {
                        Thread.sleep(5000);
                        out.writeObject(new PingEvent("Ping #" + counter));
                        counter++;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    System.out.println("Unable to send it.polimi.ingsw.sp58.event to server");
                } finally {
                    Thread.currentThread().interrupt();
                }
            });
            ping.start();


        }

        //open the in/out stream from the server
        try {

            in = new ObjectInputStream(new BufferedInputStream(serverSocket.getInputStream()));
            out = new ObjectOutputStream(serverSocket.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * sends an event to the server
     *
     * @param event the event that will be send to the server
     */
    public void sendEvent(ControllerGameEvent event) {
        if (connectionOpen) {
            try {
                out.writeObject(event); //event is serializable
                out.flush();
            } catch (IOException e) {
                System.out.println("Unable to send it.polimi.ingsw.sp58.event to server");
            }
        }
    }

    /**
     * this function reads an event from the socket and notifies all the listeners with this event.
     */
    @Override
    public void run() {
        while (true) {
            try {
                Object received = in.readObject();
                if (!(received instanceof PingEvent)) {
                    ViewGameEvent event = (ViewGameEvent) received;
                    notifyAllObserverByType(VIEW, event);
                }
            } catch (SocketTimeoutException e) {
                MessageUtility.displayErrorMessage("Lost connection with the server");
                connectionOpen = false;
                closeConnection();
                break;
            } catch (IOException | ClassNotFoundException e) {
                MessageUtility.displayErrorMessage("Client: Disconnected from the server");
                connectionOpen = false;
                closeConnection();
                break;
            }
        }
    }

    public boolean isConnectionOpen() {
        return connectionOpen;
    }

    /*
    Notifies a closed socket error to the client if it's using GUI, exit if using CLI;
     */
    public void closeConnection() {

        try {
            serverSocket.close();
            if (guiIstance != null) {
                Platform.runLater(() -> guiIstance.disconnectionHandle("Server socket has been closed."));
            } else {
                System.exit(0);
            }
        } catch (IOException ex) {
            System.out.println("Error closing the socket and streams.");
        }
    }
}
