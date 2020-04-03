package model;

import model.exception.AlreadyExistingPlayer;
import model.exception.InvalidWorkerException;
import model.gamemap.Worker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.naming.LimitExceededException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class PlayerTest {
    private Player player;
    private BoardManager boardManager;

    @Before
    public void setup(){
        boardManager = new BoardManager();
        player = new Player("matteo", boardManager);
    }

    @After
    public void tearDown(){
        player = null;
    }


    @Test
    public void getUsername_normalUsername_shouldReturnNormally() {
        Player player = new Player("matteo");
        player.setBoardManager(new BoardManager());
        assertEquals("matteo", player.getUsername());
    }

    @Test
    public void setCard_normalCard_shouldReturnNormally() {
        player.setCard("Apollo");
        assertEquals("Apollo", player.getCard());
    }

    @Test
    public void setAndRemoveWorker_normalWorker_shouldReturnNormally() throws InvalidWorkerException {
        Player player = new Player("matteo", new BoardManager());
        player.removeWorker(player.getWorker(Worker.IDs.A).getWorkerID());
        assertNull(player.getWorker(Worker.IDs.A));
    }


    @Test
    public void setColor_normalColor_shouldReturnNormally() {
        WorkerColors color = WorkerColors.BIANCO;
        Player player = new Player("matteo");
        player.setColor(color);
        assertEquals(color, player.getColor());
    }

    @Test
    public void getPlayers_normalPlayers_shouldReturnNormally() throws AlreadyExistingPlayer, LimitExceededException {
        Player p1 = new Player("gabriele");
        Player p2= new Player ("alessandro");
        boardManager.addPlayer(p1);
        boardManager.addPlayer(p2);
        boardManager.addPlayer(player);
        List<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        players.add(player);
        assertEquals(players, player.getPlayers());
    }

}