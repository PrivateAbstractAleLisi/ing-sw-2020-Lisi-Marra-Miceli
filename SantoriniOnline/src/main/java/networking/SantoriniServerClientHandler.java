package networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import auxiliary.ANSIColors;
import com.google.gson.Gson;
import controller.Lobby;
import event.gameEvents.GameEvent;
import event.gameEvents.lobby.VC_ConnectionRequestGameEvent;

public class SantoriniServerClientHandler implements Runnable {

    private Socket client;
    private SantoriniServer server;

    public SantoriniServerClientHandler(Socket client) {
        this.client = client;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            handleClientConnection();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void handleClientConnection() throws IOException, ClassNotFoundException {


        //Opens the streams
        ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream input = new ObjectInputStream(client.getInputStream());

        String json = (String) input.readObject();
        GameEvent req = new Gson().fromJson(json, GameEvent.class);

        /*

                VC_ConnectionRequestGameEvent req = (VC_ConnectionRequestGameEvent) input.readObject();

        Lobby.instance().handleEvent(req);
        Lobby.instance().debug();
        System.out.println("debug, richiesta inviata a lobby");




        if (req instanceof VC_ConnectionRequestGameEvent) {
            System.out.println("ci siamo");
        }
        while(!json.equals("end")) {
            json = (String) input.readObject();
        } */
/*
        try {

 */
        client.close();
    }



}
