package networking.client;

import auxiliary.ANSIColors;
import event.core.EventSource;
import event.gameEvents.GameEvent;
import networking.server.SantoriniServer;
import view.UI.CLI.utility.MessageUtility;
import view.UI.CLI.CLIView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static event.core.ListenerType.VIEW;


public class SantoriniClient extends EventSource implements Runnable {

    private CLIView cli;

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket serverSocket;


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
        try {
            out.writeObject(event); //event is serializable
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        while (true) {
            try {
                GameEvent event = (GameEvent) in.readObject();
                notifyAllObserverByType(VIEW, event);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
}
