package view.CLI.utility;

import org.junit.Test;

import static org.junit.Assert.*;

public class RoomUtilityTest {

    @Test
    public void printPlayersInRoom() {

        String [] players = new String []{"marco38", "s5utosk99"};
        RoomUtility.printPlayersInRoom(players, 3);
    }
}