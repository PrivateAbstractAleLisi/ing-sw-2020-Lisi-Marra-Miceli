package networking;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.Buffer;

import auxiliary.ANSIColors;
import com.google.gson.Gson;
import controller.Lobby;
import event.core.EventListener;
import event.core.EventSource;
import event.core.ListenerType;
import event.gameEvents.GameEvent;
import event.gameEvents.lobby.*;
import event.gameEvents.prematch.*;
import view.VirtualView;

import static event.core.ListenerType.CONTROLLER;
import static event.core.ListenerType.VIEW;

public class SantoriniServerClientHandler extends EventSource implements Runnable {

    private Socket client;
    private String localUsername;
    private InetAddress localUserIP;
    private int localUserPort;
    private ObjectInputStream input;

    VirtualView clientVV;

    public SantoriniServerClientHandler(Socket client) {
        this.client = client;
        this.clientVV = new VirtualView(client);
        makeConnections();
        input=null;
    }

    @Override
    public void run() {
        try {
            handleClientConnection();
        } catch (IOException | ClassNotFoundException e) {
            Thread.currentThread().interrupt();
            //e.printStackTrace();
        }
    }

    private void makeConnections() {
        this.attachListenerByType(VIEW, clientVV);
    }


    private void staccaStacca() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClientConnection() throws IOException, ClassNotFoundException {

        //Client is connected via socket, creating a virtual view for it
        ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));


        GameEvent eventReceived;

        //read further events
        while (true) {

            GameEvent received = null;
            received = (GameEvent) input.readObject();


            try {
                if (received!= null)
                notifyAllObserverByType(VIEW, received);
            } catch (Exception e) {

                e.printStackTrace();
            }
            //keaps reading

        }
        //staccaStacca();
    }



}
