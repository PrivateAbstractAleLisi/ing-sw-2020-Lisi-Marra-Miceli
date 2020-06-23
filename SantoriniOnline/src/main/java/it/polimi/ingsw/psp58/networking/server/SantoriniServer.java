package it.polimi.ingsw.psp58.networking.server;

import it.polimi.ingsw.psp58.controller.Lobby;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class SantoriniServer {

    public static final int SOCKET_PORT = 7557;
    public static final int SOCKET_TIMEOUT_S = 20;
    public static final String SERVER_VERSION = "1.6.1";

    public static void main(String[] args) throws IOException {

        boolean pingStamp = false;
        boolean enablePing = true;

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
                    case "-ping-stamp":
                        pingStamp = true;
                        System.out.println("SERVER SETTINGS: <Ping Stamp> ON");
                        break;
                    case "-pingOff":
                        enablePing = false;
                        System.out.println("SERVER SETTINGS: <Ping System> OFF");
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

    private static void printLogMessage(String message) {
        String timestamp = ZonedDateTime.now(ZoneId.of("Europe/Rome")).format(DateTimeFormatter.ofPattern("yyyy/MM/dd-HH:mm:ss"));
        System.out.println("SERVER (" + timestamp + "): " + message);
    }


}
