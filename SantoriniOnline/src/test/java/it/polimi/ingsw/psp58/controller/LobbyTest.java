package it.polimi.ingsw.psp58.controller;

import it.polimi.ingsw.psp58.event.gameEvents.lobby.CC_ConnectionRequestGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.lobby.VC_ConnectionRequestGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.lobby.VC_RoomSizeResponseGameEvent;
import it.polimi.ingsw.psp58.networking.server.SantoriniServer;
import it.polimi.ingsw.psp58.view.VirtualView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class LobbyTest {

    SantoriniServer server;
    Lobby lobby;
    @Before
    public void setUp() throws IOException {
        lobby = Lobby.instance();
        //server.main(null);
    }

    @Test
    public void lobbySingleton() {
        Lobby l1 = Lobby.instance();
        Lobby l2 = Lobby.instance();
        assertEquals(l1, l2);

    }

    @Test
    public void connectionTest_srn() throws UnknownHostException {


        lobby.handleEvent(new CC_ConnectionRequestGameEvent("test",
                InetAddress.getByName("127.0.0.1"),
                8775,
                mock(VirtualView.class), "first"

        ));

        lobby.handleEvent(new VC_RoomSizeResponseGameEvent("test", 2));
        assertFalse(lobby.canStartPreGameForThisUser("first"));

        lobby.handleEvent(new CC_ConnectionRequestGameEvent("test",
                InetAddress.getByName("127.0.0.2"),
                8775,
                mock(VirtualView.class), "second"

        ));

        assertTrue(lobby.canStartPreGameForThisUser("second"));


    }
    @After
  public void tearDown()
    {
        lobby = null;
    }


}
