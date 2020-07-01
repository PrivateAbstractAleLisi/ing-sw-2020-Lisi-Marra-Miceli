package it.polimi.ingsw.psp58.controller;

import it.polimi.ingsw.psp58.view.VirtualView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class RoomTest {

    Room room, room3;
    @Before
    public void setUp() throws Exception {

        room = new Room(2, "test");
        room3 = new Room(3, "test3");

    }

    @After
    public void tearDown() throws Exception {
    }



    @Test
    public void disconnectAllUsers() {

        room.addUser("melon", mock(VirtualView.class));
        assertNotNull(room.getActiveUsers());
        room.disconnectAllUsers("melon");
        assertNull(room.getActiveUsers());
    }

    @Test
    public void addUser() {
        room.addUser("melon", mock(VirtualView.class));
        assertEquals( room.getActiveUsers().get(0), "melon");
        assertEquals(room.getLastOccupiedPosition(),1);
        room.addUser("musk", mock(VirtualView.class));
        assertEquals( room.getActiveUsers().get(0), "melon");
        assertEquals( room.getActiveUsers().get(1), "musk");
        assertEquals(room.getLastOccupiedPosition(),2);

        room3.addUser("3melon", mock(VirtualView.class));
        assertEquals( room3.getActiveUsers().get(0), "3melon");
        assertEquals(room3.getLastOccupiedPosition(),1);
        room3.addUser("3musk", mock(VirtualView.class));
        assertEquals( room3.getActiveUsers().get(0), "3melon");
        assertEquals( room3.getActiveUsers().get(1), "3musk");
        assertEquals(room3.getLastOccupiedPosition(),2);
        room3.addUser("3nasa", mock(VirtualView.class));
        assertEquals(room3.getLastOccupiedPosition(),3);

    }

    @Test
    public void addUser_alreadyIn() {
        room.addUser("melon", mock(VirtualView.class));
        assertEquals( room.getActiveUsers().get(0), "melon");
        assertEquals(room.getLastOccupiedPosition(),1);
        room.addUser("melon", mock(VirtualView.class));
        assertEquals( room.getActiveUsers().get(0), "melon");
        assertEquals(room.getLastOccupiedPosition(),1);



    }

    @Test
    public void addUser_limit() {
        room.addUser("melon", mock(VirtualView.class));
        assertEquals( room.getActiveUsers().get(0), "melon");
        assertEquals(room.getLastOccupiedPosition(),1);
        room.addUser("musk", mock(VirtualView.class));
        assertEquals( room.getActiveUsers().get(0), "melon");
        assertEquals( room.getActiveUsers().get(1), "musk");
        assertEquals(room.getLastOccupiedPosition(),2);
        room.addUser("limitless", mock(VirtualView.class));
        room.addUser("limitless3", mock(VirtualView.class));
        room.addUser("limitless4", mock(VirtualView.class));
        assertEquals( room.getActiveUsers().get(0), "melon");
        assertEquals( room.getActiveUsers().get(1), "musk");
        assertArrayEquals(new String[]{"melon", "musk"}, room.getActiveUsersCopy());


    }


    @Test
    public void logAllUsers() {
        room.logAllUsers();
        room.addUser("melon", mock(VirtualView.class));
        assertEquals( room.getActiveUsers().get(0), "melon");
        assertEquals(room.getLastOccupiedPosition(),1);
        room.logAllUsers();
    }

    @Test
    public void isFull() {
        assertEquals(room.getSIZE(),2);
        room.addUser("melon", mock(VirtualView.class));
        assertEquals( room.getActiveUsers().get(0), "melon");
        assertEquals(room.getLastOccupiedPosition(),1);
        assertTrue(!room.isFull());
        assertEquals(room.getSIZE(),2);
        room.addUser("musk", mock(VirtualView.class));

        assertEquals( room.getActiveUsers().get(0), "melon");
        assertEquals( room.getActiveUsers().get(1), "musk");
        assertEquals(room.getLastOccupiedPosition(),2);
        assertTrue(room.isFull());
        assertEquals(room.getSIZE(),2);



    }

    @Test
    public void isFull_3() {

        assertTrue(!room3.isFull());
        room3.addUser("3melon", mock(VirtualView.class));
        assertEquals( room3.getActiveUsers().get(0), "3melon");
        assertEquals(room3.getLastOccupiedPosition(),1);
        room3.addUser("3musk", mock(VirtualView.class));
        assertTrue(!room3.isFull());
        assertEquals( room3.getActiveUsers().get(0), "3melon");
        assertEquals( room3.getActiveUsers().get(1), "3musk");
        assertEquals(room3.getLastOccupiedPosition(),2);
        room3.addUser("3nasa", mock(VirtualView.class));
        assertEquals(room3.getLastOccupiedPosition(),3);
        assertTrue(room3.isFull());


    }


    @Test
    public void beginPreGame() {
        addUser();
        room.beginPreGame();
    }



    @Test
    public void getSIZE() {
        assertEquals(room.getSIZE(), 2);
        assertEquals(room3.getSIZE(), 3);
    }

    @Test
    public void isGameCanStart() {
        room.setGameCanStartTrue();
        assertTrue(room.canGameStart());
    }

    @Test
    public void getRoomID() {
        assertEquals(room.getRoomID(), "test");
        assertEquals(room3.getRoomID(), "test3");
    }
}