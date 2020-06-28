package it.polimi.ingsw.psp58.networking.server;

import it.polimi.ingsw.psp58.controller.Lobby;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Starting class for the Server that create the instance of Lobby and accept each client creating a new Thread to handle the requests.
 */
public class SantoriniServer {

    /**
     * Socket Port to listen.
     */
    public static final int SOCKET_PORT = 7557;
    /**
     * Timeout for the socket.
     */
    public static final int SOCKET_TIMEOUT_S = 20;
    /**
     * Version of the server.
     */
    public static final String SERVER_VERSION = "1.6.2";

    /**
     * Main method that creates the instance of Lobby, waits for new client and creates a Thread running a SantoriniServerClientHandler for each new client.
     * This method also accept console arguments to enable or disable some features:
     * - "-ping-stamp" to print each Ping Message received
     * - "-pingOff" to disable the Ping Service and the TimeOut on the socket.
     * @param args Args to be  (see the description for more info).
     * @throws IOException A IOException is thrown if is not possible to accept new clients.
     */
    public static void main(String[] args) throws IOException{

        boolean pingStamp = false;
        boolean enablePing = true;

//        Create the Instance of Lobby Class
        Lobby.instance();
        ServerSocket socket;

        try {
            //Creates a socket on the server
            socket = new ServerSocket(SOCKET_PORT);
        } catch (IOException e) {
            printLogMessage("Unable to open the server socket.");
            System.exit(1);
            return;
        }

        if (args != null) {
            for (String currentArgument : args) {
                switch (currentArgument) {
                    case "-ping-stamp": //enable the print of all Ping message
                        pingStamp = true;
                        System.out.println("SERVER SETTINGS: <Ping Stamp> ON");
                        break;
                    case "-pingOff": //Disable the Ping Service and timout
                        enablePing = false;
                        System.out.println("SERVER SETTINGS: <Ping Service> OFF");
                        break;
                }
            }
        }
        printLogMessage("The server is running - version " + SERVER_VERSION);
        while (true) { //waiting for a client to connect
            Socket clientIncoming = socket.accept(); //get the client socket
            if (enablePing) {
                //If ping is enable, set timeout
                clientIncoming.setSoTimeout(SOCKET_TIMEOUT_S * 1000);
            }
            String clientIpAddress = clientIncoming.getInetAddress().toString().substring(1);
            printLogMessage("New client accepted:\tIPAddress: " + clientIpAddress + "\tPort: " + clientIncoming.getPort());
            String threadID = clientIpAddress + "@" + clientIncoming.getPort();
            SantoriniServerClientHandler handler = new SantoriniServerClientHandler(clientIncoming, threadID, pingStamp, enablePing);

            //Starts a new thread to handle this client
            Thread t = new Thread(handler, "santorini_server_" + threadID);
            t.start();
            printLogMessage("Client " + clientIpAddress + " in thread: " + threadID);
        }
    }

    /**
     * Print a formatted message with TimeStamp and other info.
     * @param message Message to print.
     */
    private static void printLogMessage(String message) {
        String timestamp = ZonedDateTime.now(ZoneId.of("Europe/Rome")).format(DateTimeFormatter.ofPattern("yyyy/MM/dd-HH:mm:ss"));
        System.out.println("SERVER (" + timestamp + "): " + message);
    }


}
