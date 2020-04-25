package view.UI.CLI.utility;

import org.junit.Test;

public class RoomUtilityTest {

    @Test
    public void printPlayersInRoom() {

        String [] players = new String []{"marco38", "s5utosk99"};
        RoomUtility.printPlayersInRoom(players, 3);
    }
}