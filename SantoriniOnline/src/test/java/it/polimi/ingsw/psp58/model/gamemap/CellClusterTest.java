package it.polimi.ingsw.psp58.model.gamemap;

import it.polimi.ingsw.psp58.model.WorkerColors;
import it.polimi.ingsw.psp58.exceptions.InvalidBuildException;
import it.polimi.ingsw.psp58.exceptions.InvalidMovementException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum.LEVEL2;
import static org.junit.Assert.*;

public class CellClusterTest {

    CellCluster istance, istance2;
    @Before
    public void setUp() throws Exception {
        istance = new CellCluster();
        istance2 = new CellCluster();
    }

    @After
    public void tearDown() throws Exception {
        istance = null;
        istance2 = null;
    }

    @Test
    public void checkBuildingBlockOrder() throws InvalidBuildException {

        istance.build(BlockTypeEnum.LEVEL1);
        assertTrue(istance.checkBuildingBlockOrder(LEVEL2));
        assertTrue(istance.checkBuildingBlockOrder(BlockTypeEnum.LEVEL3));
        assertTrue(istance.checkBuildingBlockOrder(BlockTypeEnum.DOME));
        assertFalse(istance.checkBuildingBlockOrder(BlockTypeEnum.LEVEL1));
        assertFalse(istance.isComplete());

        istance.build(LEVEL2);
        assertTrue(istance.checkBuildingBlockOrder(BlockTypeEnum.LEVEL3));
        assertFalse(istance.checkBuildingBlockOrder(BlockTypeEnum.LEVEL1));
        assertTrue(istance.checkBuildingBlockOrder(BlockTypeEnum.DOME));
        assertFalse(istance.checkBuildingBlockOrder(LEVEL2));
        assertFalse(istance.isComplete());

        istance.build(BlockTypeEnum.LEVEL3);
        assertTrue(istance.checkBuildingBlockOrder(BlockTypeEnum.DOME));
        assertFalse(istance.checkBuildingBlockOrder(BlockTypeEnum.LEVEL1));
        assertFalse(istance.checkBuildingBlockOrder(LEVEL2));
        assertFalse(istance.checkBuildingBlockOrder(BlockTypeEnum.LEVEL3));
        assertFalse(istance.isComplete());

        istance.build(BlockTypeEnum.DOME);
        assertFalse(istance.checkBuildingBlockOrder(BlockTypeEnum.DOME));
        assertFalse(istance.checkBuildingBlockOrder(BlockTypeEnum.LEVEL1));
        assertFalse(istance.checkBuildingBlockOrder(LEVEL2));
        assertFalse(istance.checkBuildingBlockOrder(BlockTypeEnum.LEVEL3));
        assertTrue(istance.isComplete());
    }

    @Test
    public void toIntArray() throws InvalidBuildException {
        istance.build(BlockTypeEnum.LEVEL1);
        int[] expected = new int[]{1};
        assertArrayEquals(expected, istance.toIntArray());
        istance.build(LEVEL2);
        expected = new int[]{1,2};
        assertArrayEquals(expected, istance.toIntArray());
        istance.build(BlockTypeEnum.LEVEL3);
        expected = new int[]{1,2,3};
        assertArrayEquals(expected, istance.toIntArray());
        istance.build(BlockTypeEnum.DOME);
        expected = new int[]{1,2,3,4};
        assertArrayEquals(expected, istance.toIntArray());

    }



    @Test
    public void build_Correct() {
        try {
            istance.build(BlockTypeEnum.LEVEL1);
            assertEquals(istance.getCostructionHeight(), 1);
            istance.build(LEVEL2);
            assertEquals(istance.getCostructionHeight(), 2);
            istance.build(BlockTypeEnum.LEVEL3);
            assertEquals(istance.getCostructionHeight(), 3);
            istance.build(BlockTypeEnum.DOME);
            assertEquals(istance.getCostructionHeight(), 4);
        }
        catch(InvalidBuildException e) {

        }
    }
//BUILD WITH EXCEPTION
    @Test (expected = InvalidBuildException.class)
    public void build_WrongInput() throws InvalidBuildException {
            istance.build(BlockTypeEnum.DOME);
            istance.build(BlockTypeEnum.LEVEL1);


    }

    @Test
    public void getCostructionHeight() {
        try{

            istance.build(BlockTypeEnum.DOME);
            assertEquals(istance.getCostructionHeight(), 1);
            istance2.build(BlockTypeEnum.LEVEL1);
            assertEquals(istance2.getCostructionHeight(), 1);
            istance2.build(LEVEL2);
            assertEquals(istance2.getCostructionHeight(), 2);


            istance2.build(BlockTypeEnum.LEVEL3);
            assertEquals(istance2.getCostructionHeight(), 3);

            istance2.build(BlockTypeEnum.DOME);
            assertEquals(istance2.getCostructionHeight(), 4);
        }
        catch (InvalidBuildException b) {
            //..
        }


    }

    @Test
    public void getCostructionHeight_Empty() {
        assertEquals(istance.getCostructionHeight(), 0);
    }

    @Test
    public void isComplete() throws InvalidBuildException {
        assertFalse(istance.isComplete());
        istance.build(BlockTypeEnum.DOME);
        assertTrue(istance.isComplete());
        istance2.build(BlockTypeEnum.LEVEL1);
        assertFalse(istance2.isComplete());
        istance2.build(LEVEL2);
        assertFalse(istance2.isComplete());
        istance2.build(BlockTypeEnum.LEVEL3);
        assertFalse(istance2.isComplete());
        istance2.build(BlockTypeEnum.DOME);

        assertTrue(istance2.isComplete());

    }

    @Test
    public void isFree() throws InvalidBuildException {
        assertTrue(istance.isFree());
        assertTrue(istance2.isFree());
        istance2.build(BlockTypeEnum.DOME);
        istance.build(BlockTypeEnum.LEVEL1);
        assertFalse(istance.isFree());
        assertFalse(istance2.isFree());


    }

    @Test
    public void addWorker() throws InvalidMovementException {
        Worker w1 = new Worker(Worker.IDs.A, "elettra");
        Worker w2 = new Worker(Worker.IDs.B, "elettra");
        istance.addWorker(w1);
        istance2.addWorker(w2);

    }

    @Test (expected = InvalidMovementException.class)
    public void addWorker_AlreadyOnTop() throws InvalidMovementException {
        Worker w1 = new Worker(Worker.IDs.A, "elettra");
        Worker w2 = new Worker(Worker.IDs.B, "elettra");
        istance.addWorker(w1);
        istance.addWorker(w2); //same istance

    }

    @Test (expected = InvalidMovementException.class)
    public void addWorker_OnDome() throws InvalidMovementException, InvalidBuildException {
        Worker w1 = new Worker(Worker.IDs.A, "elettra");
        istance.build(BlockTypeEnum.LEVEL1);
        istance.build(BlockTypeEnum.DOME);
        istance.addWorker(w1); //tries to place a worker on a dome

    }

    @Test
    public void removeWorker() throws InvalidBuildException, InvalidMovementException {
        Worker w1 = new Worker(Worker.IDs.A, "elettra");
        Worker w2 = new Worker(Worker.IDs.A, "filippo");
        istance.build(BlockTypeEnum.LEVEL1);
        istance.addWorker(w1);
        istance.removeWorker();
        istance.addWorker(w2);
        istance.removeWorker();
    }


    @Test
    public void hasWorkerOnTop() throws InvalidBuildException, InvalidMovementException {
        Worker w1 = new Worker(Worker.IDs.A, "elettra");
        Worker w2 = new Worker(Worker.IDs.A, "filippo");
        istance.build(BlockTypeEnum.LEVEL1);
        assertFalse(istance.hasWorkerOnTop());
        istance.removeWorker();
        assertFalse(istance.hasWorkerOnTop());
        istance.addWorker(w1);
        assertTrue(istance.hasWorkerOnTop());
        istance.removeWorker();
        assertFalse(istance.hasWorkerOnTop());
        istance.addWorker(w2);
        assertTrue(istance.hasWorkerOnTop());
    }

    @Test
    public void testClone() throws InvalidMovementException, InvalidBuildException {
        CellCluster original = istance;
        original.addWorker(new Worker (Worker.IDs.B, "lucia"));
        original.build(BlockTypeEnum.LEVEL1);
        original.build(LEVEL2);
        CellCluster cloned = null;
        try {
            cloned = (CellCluster) istance.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertEquals(original.hasWorkerOnTop(), cloned.hasWorkerOnTop());
        assertEquals(original.getCostructionHeight(), cloned.getCostructionHeight());
        assertEquals(original.isComplete(), cloned.isComplete());
        assertEquals(original.isFree(), cloned.isFree());
        assertEquals(original.checkBuildingBlockOrder(BlockTypeEnum.LEVEL3), cloned.checkBuildingBlockOrder(BlockTypeEnum.LEVEL3));

    }

    @Test
    public void toIntArrayWithHypo() throws InvalidBuildException {
        int[] res = istance.toIntArrayWithHypo(BlockTypeEnum.LEVEL1);
        int [] resExpected = {1};
        assertArrayEquals(resExpected, res);
        assertEquals(istance.getCostructionHeight(), 0);
        istance.build(BlockTypeEnum.LEVEL1);

        res = istance.toIntArrayWithHypo(LEVEL2);
        resExpected = new int[]{1, 2};
        assertArrayEquals(resExpected, res);
        assertEquals(istance.getCostructionHeight(), 1);
        istance.build(LEVEL2);

        res = istance.toIntArrayWithHypo(BlockTypeEnum.LEVEL3);
        resExpected = new int[]{1, 2, 3};
        assertArrayEquals(resExpected, res);
        assertEquals(istance.getCostructionHeight(), 2);
        istance.build(BlockTypeEnum.LEVEL3);

        res = istance.toIntArrayWithHypo(BlockTypeEnum.DOME);
        resExpected = new int[]{1, 2, 3, 4};
        assertArrayEquals(resExpected, res);
        assertEquals(istance.getCostructionHeight(), 3);
        istance.build(BlockTypeEnum.DOME);

       assertEquals(istance.getCostructionHeight(), 4);

    }

    @Test
    public void getWorkerOwnerUsername() throws InvalidBuildException, InvalidMovementException {
        Worker w1 = new Worker(Worker.IDs.A, "elettra");
        Worker w2 = new Worker(Worker.IDs.A, "filippo");
        istance.build(BlockTypeEnum.LEVEL1);
        assertNull(istance.getWorkerOwnerUsername());
        istance.addWorker(w1);
        assertEquals("elettra",istance.getWorkerOwnerUsername());
    }

    @Test
    public void getWorkerID() throws InvalidBuildException, InvalidMovementException {
        Worker w1 = new Worker(Worker.IDs.A, "elettra");
        Worker w2 = new Worker(Worker.IDs.A, "filippo");
        istance.build(BlockTypeEnum.LEVEL1);
        assertNull(istance.getWorkerID());
        istance.addWorker(w1);
        assertEquals(Worker.IDs.A,istance.getWorkerID());
    }

    @Test
    public void getWorkerColor() throws InvalidBuildException, InvalidMovementException {
        Worker w1 = new Worker(Worker.IDs.A, "elettra");
        Worker w2 = new Worker(Worker.IDs.A, "filippo");
        w1.setColor(WorkerColors.WHITE);
        istance.build(BlockTypeEnum.LEVEL1);
        assertNull(istance.getWorkerColor());
        istance.addWorker(w1);
        assertEquals(WorkerColors.WHITE,istance.getWorkerColor());
    }

    @Test
    public void nextBlockToBuild() throws InvalidBuildException {
        assertEquals(BlockTypeEnum.LEVEL1, istance.nextBlockToBuild());
        istance.build(BlockTypeEnum.LEVEL1);
        assertEquals(BlockTypeEnum.LEVEL2, istance.nextBlockToBuild());
        istance.build(BlockTypeEnum.LEVEL2);
        assertEquals(BlockTypeEnum.LEVEL3, istance.nextBlockToBuild());
        istance.build(BlockTypeEnum.LEVEL3);
        assertEquals(BlockTypeEnum.DOME, istance.nextBlockToBuild());
    }

    @Test (expected = InvalidBuildException.class)
    public void nextBlockToBuild_DOME_error() throws InvalidBuildException {
        istance.build(BlockTypeEnum.LEVEL1);
        istance.build(BlockTypeEnum.LEVEL2);
        istance.build(BlockTypeEnum.LEVEL3);
        istance.build(BlockTypeEnum.DOME);
        istance.nextBlockToBuild();
    }

    @Test (expected = InvalidBuildException.class)
    public void nextBlockToBuild_error() throws InvalidBuildException {
        istance.build(BlockTypeEnum.LEVEL1);
        istance.build(BlockTypeEnum.DOME);
        istance.nextBlockToBuild();
    }

}