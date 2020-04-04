package networking;

import java.io.IOException;
import java.io.*;
import java.net.*;

public class SantoriniServer {

    public final static int SOCKET_PORT = 7557;

    public static void main(String[] args) {

        ServerSocket socket;

        try {
            //Creates a socket on the server
            socket = new ServerSocket(SOCKET_PORT);
            System.out.println("Socket created");
        } catch (IOException e) {
            System.err.println("Unable to open the server socket.");
            System.exit(1);
            return;
        }

        while(true) { //waiting for a client to connect
            try {

                Socket clientIncoming = socket.accept();
                System.out.println("SERVER: 01.Client Accepted");
                SantoriniServerClientHandler handler = new SantoriniServerClientHandler((clientIncoming));
                //Starts a new thread to handle this client
                String threadID = clientIncoming.getInetAddress() + "@" + clientIncoming.getLocalPort();
                System.out.println("SERVER: 02.Creating a thread with:" + threadID);
                Thread t = new Thread(handler, "santorini_server_" + threadID);
                System.out.println("SERVER: 03.Thread created with:" + threadID);
                t.start();
                System.out.println("SERVER: 04.Thread Started:" + threadID);
            } catch (IOException e) {
                System.err.println("Connection dropped");;
            }
        }


    }




}
