package model;

import model.gamemap.Worker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PlayerTest {
    private Player player;

    @Before
    public void setup(){
        player = new Player("matteo");
    }

    @After
    public void tearDown(){
        player = null;
    }

    @Test
    public void getUsername_normalUsername_shouldReturnNormally() {
        Player player = new Player("matteo");
        assertEquals("matteo", player.getUsername());
    }

    @Test
    public void setCard_normalCard_shouldReturnNormally() {
        Card card= new Card(player);
        player.setCard(card);
        assertEquals(card, player.getCard());
    }

    @Test
    public void setWorker_normalWorker_shouldReturnNormally() {
        Player player = new Player("matteo");
        Worker worker = new Worker(Worker.IDs.A, "matteo");
        player.setWorker(worker);
        assertEquals(worker, player.getWorker(Worker.IDs.A));
    }

    @Test
    public void setWorker_withTwoWorker_shouldReturnNormally() {

        Worker worker = new Worker(Worker.IDs.A, "matteo");
        player.setWorker(worker);
        assertEquals(worker, player.getWorker(Worker.IDs.A));
    }


    @Test
    public void setColor_normalColor_shouldReturnNormally() {
        WorkerColors color = WorkerColors.BIANCO;
        Player player = new Player("matteo");
        player.setColor(color);
        assertEquals(color, player.getColor());
    }




}