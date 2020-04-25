package view.UI.CLI.utility;

public class RoomUtility {

    public static void printPlayersInRoom(String [] players, int roomSize) {

        System.out.println(players.length + " PLAYERS (" + roomSize + " Max)");
        breaker();
        for (int i = 0; i<3; i++) {
            if (i<players.length) {
                System.out.print(i+1);
                System.out.println(" | " + players[i].toUpperCase());
            }

            else {
                System.out.print(" ");
                System.out.println(" ");
            }

        }
        breaker();

    }
        private static void breaker() {
            System.out.println("---------------------------------------");
        }




}
