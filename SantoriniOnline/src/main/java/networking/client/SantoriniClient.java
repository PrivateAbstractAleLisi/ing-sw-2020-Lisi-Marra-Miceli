package networking.client;

import auxiliary.ANSIColors;
import event.core.EventSource;
import event.gameEvents.GameEvent;
import event.gameEvents.PingEvent;
import networking.server.SantoriniServer;
import view.UI.CLI.utility.MessageUtility;
import view.UI.CLI.CLIView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static event.core.ListenerType.VIEW;


public class SantoriniClient extends EventSource implements Runnable {

    private CLIView cli;

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket serverSocket;

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
            serverSocket = new Socket(IP, SantoriniServer.SOCKET_PORT);
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
                        while (true) {
                            Thread.sleep(1000);
                            sendEvent(new PingEvent("is there anybody in there?"));
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
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

    public void sendEvent(GameEvent event) {

        if (!connectionClosed) {
            try {
                out.writeObject(event); //event is serializable
                out.flush();
            } catch (IOException e) {
                System.out.println("Unable to send event to server");
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
