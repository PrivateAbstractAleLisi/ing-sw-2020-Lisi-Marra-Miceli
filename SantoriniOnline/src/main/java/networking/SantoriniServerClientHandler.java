package networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import auxiliary.ANSIColors;

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

        System.out.println("SERVER_CLIENT_HANDLER: Connected to client " + client.getInetAddress());
        //Opens the streams

        ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream input = new ObjectInputStream(client.getInputStream());
        System.out.println("SERVER_CLIENT_HANDLER: In and Out streams now running fine");


        try {

            String welcome = ANSIColors.ANSI_YELLOW +  "Welcome... to SANTORINI" + ANSIColors.ANSI_BLUE +" ♦ O-N-L-I-N-E ♦︎";
            output.writeObject(welcome);

            if (!ServerLobby.isRoomAlreadyCreated()) { //First user logging
                System.out.println("FIRST USER");
                ServerLobby.setIsRoomAlreadyCreated(true);
                //First user creates new match
                output.writeObject(ANSIColors.ANSI_RESET + "You're the first user, let's create a new match");
                output.writeObject("Type 2 or 3 to set the room size");

                Integer sizeSent = -111;
                System.out.println("Ready to read room size");
                while (sizeSent < 2 || sizeSent>3) {
                    try {
                        sizeSent = (Integer) input.readObject();
                        System.out.println("reading..");
                    }
                    catch (ClassCastException e) {

                    }

                }
                System.out.println("Room size has been read.");

                switch(sizeSent) {
                    case 2:
                        new ServerRoom(2);
                        System.out.println("Created a 2 player match");
                        break;
                    case 3:
                        new ServerRoom(3 );
                        System.out.println("Created a 3 player match");
                        break;

                }

            }
            else {
                System.out.println("NOT FIRST USER");
            }



            output.writeObject("Please insert your username: ");
//
            String user = null;
            user = (String) input.readObject();
            user = user.toLowerCase();
            System.out.println("DEBUG: username read before while");
            while (!isUsernameValid(user)) {
                user = (String) input.readObject();
                user = user.toLowerCase();
                System.out.println("read: " + user);
            }
            System.out.println("USERNAME OK:  " + user);



            ServerRoom.addPlayer(user);
            ServerRoom.logAllUsers();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("IO Exception while reading object from a client");
        } /*catch (ClassNotFoundException e) {
            System.err.println("Class not found while reading object from a client");
        } */

        client.close();
    }

    private boolean isUsernameValid(String user) {
        if (user.length() > 4) {
            return true;
        }
        else {
            return false;
        }
    }
}
