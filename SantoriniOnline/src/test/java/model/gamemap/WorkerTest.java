package model.gamemap;

import static org.junit.Assert.*;

import org.junit.*;

public class WorkerTest {

    private Worker wo;
    private String playerUsername01 = "Mast3rXForever05";
    @org.junit.Before
    public void setUp() throws Exception {
        wo = new Worker(Worker.IDs.A, "Mast3rXForever05");
    }

    @org.junit.After
    public void tearDown() throws Exception {
        wo = null;
    }


    @Test
    public void setPosition_CorrectInput() {
        wo.setPosition(3, 4);
        int[] position = wo.getPosition();
        Assert.assertEquals(position[0], 3);
        Assert.assertEquals(position[1], 4);
    }
//TODO wrong input

    @org.junit.Test
    public void getPosition() {
        wo.setPosition(3, 4);
        int[] result = wo.getPosition();
        Assert.assertEquals(result[0], 3);
        Assert.assertEquals(result[1], 4);

    }

    @Test
    public void getPosition_NotSet () {
        Assert.assertNull(wo.getPosition());

    }
    @org.junit.Test
    public void getWorkerID() {
        Assert.assertEquals(wo.getWorkerID(), Worker.IDs.A);
    }
    @Test
    public void getPlayerName () {
        Assert.assertEquals(wo.getPlayerUsername(), playerUsername01);
    }

}