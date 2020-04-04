package networking;
import model.Player;

public class ServerRoom {

    public final int SIZE;
    private static int lastOccupiedPosition;
    private static String[] partecipants;


    public ServerRoom(int size) {
        this.SIZE = size;
        partecipants = new String[SIZE];
        lastOccupiedPosition = 0;
    }


    public static void  addPlayer(String username) {
        ServerRoom.partecipants[lastOccupiedPosition] = username;
        ServerRoom.lastOccupiedPosition++;
    }

    public static void logAllUsers () {
        System.out.println("IN THIS ROOM: ");
        for (int i = 0; i < partecipants.length; i++) {
            System.out.println(i + " | " + partecipants[i]);
        }
    }

}
