package networking;

import controller.Lobby;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
        while (true) { //waiting for a client to connect


            Socket clientIncoming = socket.accept(); //get the client socket
            String clientIpAddress = clientIncoming.getInetAddress().toString();
            System.out.println("New client accepted:\tIPAddress: " + clientIpAddress + "\tPort: " + clientIncoming.getPort());
            SantoriniServerClientHandler handler = new SantoriniServerClientHandler((clientIncoming));

            //Starts a new thread to handle this client
            String threadID = clientIncoming.getInetAddress() + "@" + clientIncoming.getPort();
            Thread t = new Thread(handler, "santorini_server_" + threadID);
            t.start();
            System.out.println("Client " + clientIpAddress + " in thread: " + threadID);
        }


    }


}
