package it.polimi.ingsw.psp58.networking.client;

import it.polimi.ingsw.psp58.auxiliary.ANSIColors;
import it.polimi.ingsw.psp58.event.core.EventSource;
import it.polimi.ingsw.psp58.event.gameEvents.ControllerGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.PingEvent;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;
import it.polimi.ingsw.psp58.view.UI.CLI.CLIView;
import it.polimi.ingsw.psp58.view.UI.CLI.utility.MessageUtility;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static it.polimi.ingsw.psp58.event.core.ListenerType.VIEW;


public class SantoriniClient extends EventSource implements Runnable {

    private CLIView cli;

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket serverSocket;
    public static final int SERVER_PORT=7557;
    public final static int SOCKET_TIMEOUT_S = 20;

    private boolean connectionClosed = false;

    public void begin() {

        cli = new CLIView(this);
        Scanner systemIn = new Scanner(System.in);

        MessageUtility.bigTitle();

        try {
            TimeUnit.SECONDS.sleep(1);
            MessageUtility.online();
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(ANSIColors.YELLOW_UNDERLINED + "Press Enter to Start:" + ANSIColors.ANSI_RESET + " ");
        systemIn.nextLine();

        //System.out.println("WELCOME, Client Started.");
        CLIView.clearScreen();
        System.out.println("Insert server IP Address (press ENTER for localhost): ");
        String IP = systemIn.nextLine();
        if (IP.equals("")) {
            IP = "127.0.0.1";
        }

        serverSocket = null;
        //Open a connection with the server
        try {
            serverSocket = new Socket(IP, SERVER_PORT);
            serverSocket.setSoTimeout(SOCKET_TIMEOUT_S * 1000);
        } catch (IOException e) {
            System.err.println("Client: Unable to open a socket");
            e.printStackTrace();
        }

        System.out.println("CLIENT: connected ");

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
            cli.start(); //starts


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendEvent(ControllerGameEvent event) {

        if (!connectionClosed) {
            try {
                out.writeObject(event); //event is serializable
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
                Object received = in.readObject();
                if (!(received instanceof PingEvent)) {
                    ViewGameEvent event = (ViewGameEvent) received;
                    notifyAllObserverByType(VIEW, event);
                }
            } catch (SocketTimeoutException e){
                MessageUtility.displayErrorMessage("Lost connection with the server");
                connectionClosed = true;
                closeConnection();
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
