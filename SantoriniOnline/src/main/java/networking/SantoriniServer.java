package networking;

import controller.Lobby;

import java.io.IOException;
import java.io.*;
import java.net.*;

public class SantoriniServer {

    public final static int SOCKET_PORT = 7557;

    public static void main(String[] args) throws IOException {

        Lobby lobby = Lobby.instance();
        ServerSocket socket;

        try {
            //Creates a socket on the server
            socket = new ServerSocket(SOCKET_PORT);
        } catch (IOException e) {
            System.err.println("Unable to open the server socket.");
            System.exit(1);
            return;
        }
        System.out.println("server is running");
        while(true) { //waiting for a client to connect


                Socket clientIncoming = socket.accept(); //get the client socket
            System.out.println("accepted");
                SantoriniServerClientHandler handler = new SantoriniServerClientHandler((clientIncoming));

                //Starts a new thread to handle this client
                String threadID = clientIncoming.getInetAddress() + "@" + clientIncoming.getLocalPort();
                Thread t = new Thread(handler, "santorini_server_" + threadID);
                t.start();

        }


    }




}
