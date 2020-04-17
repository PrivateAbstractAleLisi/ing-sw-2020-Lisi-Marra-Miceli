package networking.client;

import com.google.gson.Gson;
import event.core.EventListener;
import event.core.EventSource;
import event.gameEvents.GameEvent;
import event.gameEvents.lobby.*;
import event.gameEvents.prematch.*;
import networking.SantoriniServer;
import view.CLIView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;


public class SantoriniClient extends EventSource implements EventListener {

    CLIView cli;

    ObjectInputStream in;
    ObjectOutputStream out;
    public void begin() {

        cli = new CLIView(this);
        Scanner systemIn = new Scanner(System.in);
        System.out.println("WELCOME, Client Started.");
        System.out.println("Insert server IP Address: ");
        String IP = systemIn.nextLine();

        Socket serverSocket = null;
        //Open a connection with the server
        try {
            serverSocket = new Socket(IP, SantoriniServer.SOCKET_PORT);
        } catch (IOException e) {
            System.err.println("Client: Unable to open a socket");
            e.printStackTrace();
        }

        System.out.println("CLIENT: connected ");

        try {

            in = new ObjectInputStream(serverSocket.getInputStream());
            out = new ObjectOutputStream(serverSocket.getOutputStream());
            cli.start();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleEvent(VC_ConnectionRequestGameEvent event) {
        try {



            //non usato
            Gson gson = new Gson();
            String jsonString = gson.toJson(event);
            System.out.println(jsonString);

            out.writeObject(jsonString);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    @Override
    public void handleEvent(GameEvent event) {

    }

    @Override
    public void handleEvent(VC_RoomSizeResponseGameEvent event) {

    }

    @Override
    public void handleEvent(CV_RoomUpdateGameEvent event) {

    }



    @Override
    public void handleEvent(CC_ConnectionRequestGameEvent event) {

    }

    @Override
    public void handleEvent(CV_RoomSizeRequestGameEvent event) {

    }

    @Override
    public void handleEvent(CV_ConnectionRejectedErrorGameEvent event) {

    }

    @Override
    public void handleEvent(VC_ChallengerCardsChosenEvent event) {

    }

    @Override
    public void handleEvent(VC_PlayerCardChosenEvent event) {

    }

    @Override
    public void handleEvent(VC_ChallengerChosenFirstPlayerEvent event) {

    }

    @Override
    public void handleEvent(CV_ChallengerChosenEvent event) {

    }

    @Override
    public void handleEvent(CV_CardChoiceRequestGameEvent event) {

    }

    @Override
    public void handleEvent(CV_WaitGameEvent event) {

    }

    @Override
    public void handleEvent(CV_ChallengerChooseFirstPlayerRequestEvent event) {

    }
}
