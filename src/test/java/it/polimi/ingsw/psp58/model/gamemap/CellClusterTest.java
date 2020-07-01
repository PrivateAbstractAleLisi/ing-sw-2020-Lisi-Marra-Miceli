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

    CellCluster cellCluster1, cellCluster2;
    @Before
    public void setUp() throws Exception {
        cellCluster1 = new CellCluster();
        cellCluster2 = new CellCluster();
    }

    @After
    public void tearDown() throws Exception {
        cellCluster1 = null;
        cellCluster2 = null;
    }

    @Test
    public void checkBuildingBlockOrder() throws InvalidBuildException {

        cellCluster1.build(BlockTypeEnum.LEVEL1);
        assertTrue(cellCluster1.checkBuildingBlockOrder(LEVEL2));
        assertTrue(cellCluster1.checkBuildingBlockOrder(BlockTypeEnum.LEVEL3));
        assertTrue(cellCluster1.checkBuildingBlockOrder(BlockTypeEnum.DOME));
        assertFalse(cellCluster1.checkBuildingBlockOrder(BlockTypeEnum.LEVEL1));
        assertFalse(cellCluster1.isComplete());

        cellCluster1.build(LEVEL2);
        assertTrue(cellCluster1.checkBuildingBlockOrder(BlockTypeEnum.LEVEL3));
        assertFalse(cellCluster1.checkBuildingBlockOrder(BlockTypeEnum.LEVEL1));
        assertTrue(cellCluster1.checkBuildingBlockOrder(BlockTypeEnum.DOME));
        assertFalse(cellCluster1.checkBuildingBlockOrder(LEVEL2));
        assertFalse(cellCluster1.isComplete());

        cellCluster1.build(BlockTypeEnum.LEVEL3);
        assertTrue(cellCluster1.checkBuildingBlockOrder(BlockTypeEnum.DOME));
        assertFalse(cellCluster1.checkBuildingBlockOrder(BlockTypeEnum.LEVEL1));
        assertFalse(cellCluster1.checkBuildingBlockOrder(LEVEL2));
        assertFalse(cellCluster1.checkBuildingBlockOrder(BlockTypeEnum.LEVEL3));
        assertFalse(cellCluster1.isComplete());

        cellCluster1.build(BlockTypeEnum.DOME);
        assertFalse(cellCluster1.checkBuildingBlockOrder(BlockTypeEnum.DOME));
        assertFalse(cellCluster1.checkBuildingBlockOrder(BlockTypeEnum.LEVEL1));
        assertFalse(cellCluster1.checkBuildingBlockOrder(LEVEL2));
        assertFalse(cellCluster1.checkBuildingBlockOrder(BlockTypeEnum.LEVEL3));
        assertTrue(cellCluster1.isComplete());
    }

    @Test
    public void toIntArray() throws InvalidBuildException {
        cellCluster1.build(BlockTypeEnum.LEVEL1);
        int[] expected = new int[]{1};
        assertArrayEquals(expected, cellCluster1.toIntArray());
        cellCluster1.build(LEVEL2);
        expected = new int[]{1,2};
        assertArrayEquals(expected, cellCluster1.toIntArray());
        cellCluster1.build(BlockTypeEnum.LEVEL3);
        expected = new int[]{1,2,3};
        assertArrayEquals(expected, cellCluster1.toIntArray());
        cellCluster1.build(BlockTypeEnum.DOME);
        expected = new int[]{1,2,3,4};
        assertArrayEquals(expected, cellCluster1.toIntArray());

    }



    @Test
    public void build_Correct() throws Exception {
            cellCluster1.build(BlockTypeEnum.LEVEL1);
            assertEquals(1, cellCluster1.getCostructionHeight());
            cellCluster1.build(LEVEL2);
            assertEquals(2, cellCluster1.getCostructionHeight());
            cellCluster1.build(BlockTypeEnum.LEVEL3);
            assertEquals(3, cellCluster1.getCostructionHeight());
            cellCluster1.build(BlockTypeEnum.DOME);
            assertEquals(4, cellCluster1.getCostructionHeight());

    }
//BUILD WITH EXCEPTION
    @Test (expected = InvalidBuildException.class)
    public void build_WrongInput() throws InvalidBuildException {
            cellCluster1.build(BlockTypeEnum.DOME);
            cellCluster1.build(BlockTypeEnum.LEVEL1);

    }

    @Test
    public void getConstructionHeight() throws InvalidBuildException{

            cellCluster1.build(BlockTypeEnum.DOME);
            assertEquals(1, cellCluster1.getCostructionHeight());
            cellCluster2.build(BlockTypeEnum.LEVEL1);
            assertEquals(1, cellCluster2.getCostructionHeight());
            cellCluster2.build(LEVEL2);
            assertEquals(2, cellCluster2.getCostructionHeight());


            cellCluster2.build(BlockTypeEnum.LEVEL3);
            assertEquals(3, cellCluster2.getCostructionHeight());

            cellCluster2.build(BlockTypeEnum.DOME);
            assertEquals(4, cellCluster2.getCostructionHeight());

    }

    @Test
    public void getCostructionHeight_Empty() {
        assertEquals(0, cellCluster1.getCostructionHeight());
    }

    @Test
    public void isComplete() throws InvalidBuildException {
        assertFalse(cellCluster1.isComplete());
        cellCluster1.build(BlockTypeEnum.DOME);
        assertTrue(cellCluster1.isComplete());
        cellCluster2.build(BlockTypeEnum.LEVEL1);
        assertFalse(cellCluster2.isComplete());
        cellCluster2.build(LEVEL2);
        assertFalse(cellCluster2.isComplete());
        cellCluster2.build(BlockTypeEnum.LEVEL3);
        assertFalse(cellCluster2.isComplete());
        cellCluster2.build(BlockTypeEnum.DOME);

        assertTrue(cellCluster2.isComplete());

    }

    @Test
    public void isFree() throws InvalidBuildException {
        assertTrue(cellCluster1.isFree());
        assertTrue(cellCluster2.isFree());
        cellCluster2.build(BlockTypeEnum.DOME);
        cellCluster1.build(BlockTypeEnum.LEVEL1);
        assertFalse(cellCluster1.isFree());
        assertFalse(cellCluster2.isFree());


    }

    @Test
    public void addWorker() throws InvalidMovementException {
        Worker w1 = new Worker(Worker.IDs.A, "elettra");
        Worker w2 = new Worker(Worker.IDs.B, "elettra");
        cellCluster1.addWorker(w1);
        cellCluster2.addWorker(w2);

        assertTrue(cellCluster1.hasWorkerOnTop());
        assertEquals("elettra", cellCluster1.getWorkerOwnerUsername());
        assertEquals(Worker.IDs.A, cellCluster1.getWorkerID());

        assertTrue(cellCluster2.hasWorkerOnTop());
        assertEquals("elettra", cellCluster2.getWorkerOwnerUsername());
        assertEquals(Worker.IDs.B, cellCluster2.getWorkerID());
    }

    @Test (expected = InvalidMovementException.class)
    public void addWorker_AlreadyOnTop() throws InvalidMovementException {
        Worker w1 = new Worker(Worker.IDs.A, "elettra");
        Worker w2 = new Worker(Worker.IDs.B, "elettra");
        cellCluster1.addWorker(w1);
        cellCluster1.addWorker(w2); //same istance

    }

    @Test (expected = InvalidMovementException.class)
    public void addWorker_OnDome() throws InvalidMovementException, InvalidBuildException {
        Worker w1 = new Worker(Worker.IDs.A, "elettra");
        cellCluster1.build(BlockTypeEnum.LEVEL1);
        cellCluster1.build(BlockTypeEnum.DOME);
        cellCluster1.addWorker(w1); //tries to place a worker on a dome

    }

    @Test
    public void removeWorker() throws InvalidBuildException, InvalidMovementException {
        Worker w1 = new Worker(Worker.IDs.A, "elettra");
        Worker w2 = new Worker(Worker.IDs.A, "filippo");
        cellCluster1.build(BlockTypeEnum.LEVEL1);
        cellCluster1.addWorker(w1);
        cellCluster1.removeWorker();
        cellCluster1.addWorker(w2);
        cellCluster1.removeWorker();
        assertFalse(cellCluster1.hasWorkerOnTop());
    }


    @Test
    public void hasWorkerOnTop() throws InvalidBuildException, InvalidMovementException {
        Worker w1 = new Worker(Worker.IDs.A, "elettra");
        Worker w2 = new Worker(Worker.IDs.A, "filippo");
        cellCluster1.build(BlockTypeEnum.LEVEL1);
        assertFalse(cellCluster1.hasWorkerOnTop());
        cellCluster1.removeWorker();
        assertFalse(cellCluster1.hasWorkerOnTop());
        cellCluster1.addWorker(w1);
        assertTrue(cellCluster1.hasWorkerOnTop());
        cellCluster1.removeWorker();
        assertFalse(cellCluster1.hasWorkerOnTop());
        cellCluster1.addWorker(w2);
        assertTrue(cellCluster1.hasWorkerOnTop());
    }

    @Test
    public void testClone() throws InvalidMovementException, InvalidBuildException {
        CellCluster original = cellCluster1;
        original.addWorker(new Worker (Worker.IDs.B, "lucia"));
        original.build(BlockTypeEnum.LEVEL1);
        original.build(LEVEL2);
        CellCluster cloned = null;
        try {
            cloned = (CellCluster) cellCluster1.clone();
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
        int[] res = cellCluster1.toIntArrayWithHypo(BlockTypeEnum.LEVEL1);
        int [] resExpected = {1};
        assertArrayEquals(resExpected, res);
        assertEquals(0, cellCluster1.getCostructionHeight());
        cellCluster1.build(BlockTypeEnum.LEVEL1);

        res = cellCluster1.toIntArrayWithHypo(LEVEL2);
        resExpected = new int[]{1, 2};
        assertArrayEquals(resExpected, res);
        assertEquals(1, cellCluster1.getCostructionHeight());
        cellCluster1.build(LEVEL2);

        res = cellCluster1.toIntArrayWithHypo(BlockTypeEnum.LEVEL3);
        resExpected = new int[]{1, 2, 3};
        assertArrayEquals(resExpected, res);
        assertEquals(2, cellCluster1.getCostructionHeight());
        cellCluster1.build(BlockTypeEnum.LEVEL3);

        res = cellCluster1.toIntArrayWithHypo(BlockTypeEnum.DOME);
        resExpected = new int[]{1, 2, 3, 4};
        assertArrayEquals(resExpected, res);
        assertEquals(3, cellCluster1.getCostructionHeight());
        cellCluster1.build(BlockTypeEnum.DOME);

       assertEquals(4, cellCluster1.getCostructionHeight());

    }

    @Test
    public void getWorkerOwnerUsername() throws InvalidBuildException, InvalidMovementException {
        Worker w1 = new Worker(Worker.IDs.A, "elettra");
        Worker w2 = new Worker(Worker.IDs.A, "filippo");
        cellCluster1.build(BlockTypeEnum.LEVEL1);
        assertNull(cellCluster1.getWorkerOwnerUsername());
        cellCluster1.addWorker(w1);
        assertEquals("elettra", cellCluster1.getWorkerOwnerUsername());
    }

    @Test
    public void getWorkerID() throws InvalidBuildException, InvalidMovementException {
        Worker w1 = new Worker(Worker.IDs.A, "elettra");
        Worker w2 = new Worker(Worker.IDs.A, "filippo");
        cellCluster1.build(BlockTypeEnum.LEVEL1);
        assertNull(cellCluster1.getWorkerID());
        cellCluster1.addWorker(w1);
        assertEquals(Worker.IDs.A, cellCluster1.getWorkerID());
    }

    @Test
    public void getWorkerColor() throws InvalidBuildException, InvalidMovementException {
        Worker w1 = new Worker(Worker.IDs.A, "elettra");
        Worker w2 = new Worker(Worker.IDs.A, "filippo");
        w1.setColor(WorkerColors.PINK);
        cellCluster1.build(BlockTypeEnum.LEVEL1);
        assertNull(cellCluster1.getWorkerColor());
        cellCluster1.addWorker(w1);
        assertEquals(WorkerColors.PINK, cellCluster1.getWorkerColor());
    }

    @Test
    public void nextBlockToBuild() throws InvalidBuildException {
        assertEquals(BlockTypeEnum.LEVEL1, cellCluster1.nextBlockToBuild());
        cellCluster1.build(BlockTypeEnum.LEVEL1);
        assertEquals(BlockTypeEnum.LEVEL2, cellCluster1.nextBlockToBuild());
        cellCluster1.build(BlockTypeEnum.LEVEL2);
        assertEquals(BlockTypeEnum.LEVEL3, cellCluster1.nextBlockToBuild());
        cellCluster1.build(BlockTypeEnum.LEVEL3);
        assertEquals(BlockTypeEnum.DOME, cellCluster1.nextBlockToBuild());
    }

    @Test (expected = InvalidBuildException.class)
    public void nextBlockToBuild_DOME_error() throws InvalidBuildException {
        cellCluster1.build(BlockTypeEnum.LEVEL1);
        cellCluster1.build(BlockTypeEnum.LEVEL2);
        cellCluster1.build(BlockTypeEnum.LEVEL3);
        cellCluster1.build(BlockTypeEnum.DOME);
        cellCluster1.nextBlockToBuild();
    }

    @Test (expected = InvalidBuildException.class)
    public void nextBlockToBuild_error() throws InvalidBuildException {
        cellCluster1.build(BlockTypeEnum.LEVEL1);
        cellCluster1.build(BlockTypeEnum.DOME);
        cellCluster1.nextBlockToBuild();
    }

}