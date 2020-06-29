package it.polimi.ingsw.psp58.model.gamemap;

import org.junit.*;

public class WorkerTest {

    private Worker worker;
    private final String playerUsername01 = "Mast3rXForever05";
    @org.junit.Before
    public void setUp() throws Exception {
        worker = new Worker(Worker.IDs.A, "Mast3rXForever05");
    }

    @org.junit.After
    public void tearDown() throws Exception {
        worker = null;
    }


    @Test
    public void setPosition_CorrectInput() {
        worker.setPosition(3, 4);
        int[] position = worker.getPosition();
        Assert.assertEquals(3, position[0]);
        Assert.assertEquals(4, position[1]);
    }
//TODO wrong input

    @org.junit.Test
    public void getPosition() {
        worker.setPosition(3, 4);
        int[] result = worker.getPosition();
        Assert.assertEquals(3, result[0]);
        Assert.assertEquals(4, result[1]);

    }

    @Test
    public void getPosition_NotSet () {
        Assert.assertNull(worker.getPosition());

    }
    @org.junit.Test
    public void getWorkerID() {
        Assert.assertEquals(Worker.IDs.A, worker.getWorkerID());
    }
    @Test
    public void getPlayerName () {
        Assert.assertEquals(worker.getPlayerUsername(), playerUsername01);
    }

}