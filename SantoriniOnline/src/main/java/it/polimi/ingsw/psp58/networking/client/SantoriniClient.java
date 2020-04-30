package it.polimi.ingsw.psp58.networking.client;

import it.polimi.ingsw.psp58.auxiliary.ANSIColors;
import it.polimi.ingsw.psp58.event.core.EventListener;
import it.polimi.ingsw.psp58.event.core.EventSource;
import it.polimi.ingsw.psp58.event.gameEvents.GameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.PingEvent;
import it.polimi.ingsw.psp58.view.UI.CLI.CLIView;
import it.polimi.ingsw.psp58.view.UI.CLI.utility.MessageUtility;
import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import javafx.application.Application;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static it.polimi.ingsw.psp58.event.core.ListenerType.VIEW;


public class SantoriniClient extends EventSource implements Runnable {

    private EventListener userInterface;

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String IP;
    private Socket serverSocket;
    public static final int SERVER_PORT=7557;

    private boolean connectionClosed = false;

    public SantoriniClient(EventListener userInterface, String ipAddress) {
        this.userInterface = userInterface;
        this.IP= IP;
        attachListenerByType(VIEW, userInterface);
    }

    public void begin() {

        serverSocket = null;
        //Open a connection with the server
        try {
            serverSocket = new Socket(IP, SERVER_PORT);
        } catch (IOException e) {
            System.err.println("Client: Unable to open a socket");
            e.printStackTrace();
        }

        try {
            InetAddress serverInetAddress = InetAddress.getByName(IP);
          /*  Thread pinger = new Thread(new ClientPing(serverInetAddress), "pinger");
            pinger.start(); */

            Thread ping = new Thread() {
                public void run() {

                    try {
                        int counter=0;
                        while (true) {
                            Thread.sleep(5000);
                            out.writeObject(new PingEvent("Ping #"+counter));
                            counter++;
                        }
                    } catch (InterruptedException  e) {
                        e.printStackTrace();
                    }catch (IOException e){
                        System.out.println("Unable to send it.polimi.ingsw.sp58.event to server");
                    }
                    finally {
                        Thread.currentThread().interrupt();
                    }
                }
            };

            ping.start();

        } catch (UnknownHostException e) {
            System.out.println("Unable to convert IP address to InetAddress");
        }

        //open the in/out stream from the server
        try {

            in = new ObjectInputStream(new BufferedInputStream(serverSocket.getInputStream()));
            out = new ObjectOutputStream(serverSocket.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendEvent(GameEvent event) {

        if (!connectionClosed) {
            try {
                out.writeObject(event); //it.polimi.ingsw.sp58.event is serializable
                out.flush();
            } catch (IOException e) {
                System.out.println("Unable to send it.polimi.ingsw.sp58.event to server");
            }
        }
    }

    //READS FROM SOCKET
    @Override
    public void run() {
        while (true) {
            try {
                Object object = in.readObject();
                if (object instanceof PingEvent) {

                } else {
                    GameEvent event = (GameEvent) object;

                    notifyAllObserverByType(VIEW, event);
                }

            } catch (IOException | ClassNotFoundException e) {

                MessageUtility.displayErrorMessage("Client: Disconnected from the server");
                connectionClosed = true;
                closeConnection();
            }
        }

    }

    public void closeConnection() {

        try {
            serverSocket.close();
        } catch (IOException ex) {
            System.out.println("Error closing the socket and streams.");
        } finally {
            System.exit(0);
        }
    }
}
