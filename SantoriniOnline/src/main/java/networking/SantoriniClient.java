package networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class SantoriniClient {

    public static void main(String[] args) throws ClassNotFoundException {
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
            ObjectInputStream in = new ObjectInputStream(serverSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(serverSocket.getOutputStream());

            Integer size = -1;
            String incomingStr = "";

            //Send username
            while (!incomingStr.equals("Type 2 or 3 to set the room size")) {
                incomingStr = (String) in.readObject();
                System.out.println(incomingStr);
            }

            size = systemIn.nextInt();
            System.out.println("Client: size  read ");
            if (size == 2 || size == 3) {
                out.writeObject(size);
                System.out.println("Client: size if valid, sent to the server");
            }

            String str = "";

            incomingStr = (String) in.readObject();
            System.out.println(incomingStr);

            //Send username
            while (!"end".equals(str)) {

                str = systemIn.nextLine();
                if (str.length() > 2) {
                    out.writeObject(str);
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
