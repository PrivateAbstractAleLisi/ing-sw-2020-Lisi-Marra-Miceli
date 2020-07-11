package it.polimi.ingsw.psp58.model.gamemap;

import it.polimi.ingsw.psp58.exceptions.InvalidBuildException;
import it.polimi.ingsw.psp58.exceptions.InvalidMovementException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class IslandTest {

    private Island island;
    private Worker workerA, workerB;
    private final String user = "TestoSterone98";

    @Before
    public void setUp() throws Exception {
        island = new Island();
        workerA = new Worker(Worker.IDs.A, user);
        workerB = new Worker(Worker.IDs.B, user);
    }

    @After
    public void tearDown() throws Exception {
        island = null;
        workerA = null;
        workerB = null;

    }

    @Test
    public void placeWorker() throws InvalidMovementException {
        island.placeWorker(workerA, 3, 1);
        island.placeWorker(workerB, 2, 1); //place worker, stesso worker già posizionato?
        assertTrue(island.getCellCluster(3, 1).hasWorkerOnTop());
        assertTrue(island.getCellCluster(2, 1).hasWorkerOnTop());

        //muovi, (aggiorna posizione) e controlla se ora c'è / non c'è più
        island.moveWorker(workerA, 4, 4);
        assertFalse(island.getCellCluster(3, 1).hasWorkerOnTop());
        assertTrue(island.getCellCluster(4, 4).hasWorkerOnTop());

        //muovi, (aggiorna posizione) e controlla se ora c'è / non c'è più
        island.moveWorker(workerB, 3, 4);
        assertFalse(island.getCellCluster(2, 1).hasWorkerOnTop());
        assertTrue(island.getCellCluster(3, 4).hasWorkerOnTop());

    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void placeWorker_OutOfBounds() throws InvalidMovementException, IndexOutOfBoundsException {
        island.placeWorker(workerB, 10, -1);
        island.placeWorker(workerB, 1, -1);
        island.placeWorker(workerB, 101, 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void placeWorker_JustAboveLimit() throws InvalidMovementException, IndexOutOfBoundsException {
        island.placeWorker(workerB, 5, 5);
    }

    @Test
    public void placeWorker_AndRemove() throws InvalidMovementException {
        island.placeWorker(workerB, 1, 1);
        island.removeWorker(workerB);
        island.placeWorker(workerB, 1, 1);

        assertTrue(island.getCellCluster(1,1).hasWorkerOnTop());
    }

    @Test
    public void moveWorker() throws InvalidMovementException {
        island.placeWorker(workerA, 3, 2);
        assertTrue(island.getCellCluster(3, 2).hasWorkerOnTop());
        //move, (updates position) and check if there is no more
        island.moveWorker(workerA, 4, 4);
        assertFalse(island.getCellCluster(3, 2).hasWorkerOnTop());
        assertTrue(island.getCellCluster(4, 4).hasWorkerOnTop());
    }

    @Test
    public void moveWorker_fromA_toA() throws InvalidMovementException {
        island.placeWorker(workerA, 2, 2);
        assertTrue(island.getCellCluster(2, 2).hasWorkerOnTop());
        island.moveWorker(workerA, 2, 2);
        assertTrue(island.getCellCluster(2, 2).hasWorkerOnTop());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void moveWorker_out_of_bounds() throws IndexOutOfBoundsException, InvalidMovementException {
        island.moveWorker(workerA, -3, -4);
    }

    @Test
    public void buildBlock() throws InvalidBuildException {
        island.buildBlock(BlockTypeEnum.LEVEL2, 3, 3);
        CellCluster target = island.getCellCluster(3, 3);
        assertEquals(1, target.getCostructionHeight());
        int[] expected = new int[]{2, 3};
        int[] test = target.toIntArrayWithHypo(BlockTypeEnum.LEVEL3);
        System.out.println("DEBUG, EXPECTED ORDER (as Int)");
        for (int value : test) {
            System.out.println(value);
        }
        assertArrayEquals(target.toIntArrayWithHypo(BlockTypeEnum.LEVEL3), expected);
    }

    @Test(expected = InvalidBuildException.class)
    public void buildBlock_error_wrong_order() throws InvalidBuildException {
        island.buildBlock(BlockTypeEnum.LEVEL2, 2, 3);
        island.buildBlock(BlockTypeEnum.LEVEL3, 3, 3);
        island.buildBlock(BlockTypeEnum.DOME, 3, 3);
        island.buildBlock(BlockTypeEnum.LEVEL1, 3, 3);

    }

    @Test(expected = InvalidBuildException.class)
    public void buildBlock_error_wrong_order_no_dome() throws InvalidBuildException {
        island.buildBlock(BlockTypeEnum.LEVEL2, 2, 3);
        island.buildBlock(BlockTypeEnum.LEVEL3, 3, 3);
        island.buildBlock(BlockTypeEnum.LEVEL1, 3, 3);
        island.buildBlock(BlockTypeEnum.LEVEL3, 3, 3);

    }

    @Test(expected = InvalidBuildException.class)
    public void buildBlock_error_duplicate() throws InvalidBuildException {
        island.buildBlock(BlockTypeEnum.LEVEL1, 3, 3);
        island.buildBlock(BlockTypeEnum.LEVEL2, 3, 3);
        island.buildBlock(BlockTypeEnum.LEVEL3, 3, 3);
        island.buildBlock(BlockTypeEnum.LEVEL3, 3, 3);

    }

    @Test
    public void getCellCluster() throws InvalidBuildException, InvalidMovementException, CloneNotSupportedException {
        island.buildBlock(BlockTypeEnum.LEVEL1, 3, 3);
        island.buildBlock(BlockTypeEnum.LEVEL2, 3, 3);
        island.buildBlock(BlockTypeEnum.LEVEL3, 3, 3);
        island.placeWorker(workerA, 3, 3);
        CellCluster gcc = island.getCellCluster(3, 3);
        assertTrue(gcc.hasWorkerOnTop());

    }

    @Test//(expected = InvalidWorkerRemovalException.class)
    public void removeWorker() throws InvalidMovementException {
        island.placeWorker(workerA, 2, 1);
        island.removeWorker(workerA);
        island.placeWorker(workerA, 2, 1);
        assertFalse(island.getCellCluster(2, 4).hasWorkerOnTop());
        island.placeWorker(workerB, 1, 1);
        assertTrue(island.getCellCluster(1, 1).hasWorkerOnTop());
        island.moveWorker(workerB, 4, 1);
        assertTrue(island.getCellCluster(4, 1).hasWorkerOnTop());
        island.removeWorker(workerB);
        assertFalse(island.getCellCluster(4, 1).hasWorkerOnTop());
        island.removeWorker(workerA);
        assertFalse(island.getCellCluster(3, 3).hasWorkerOnTop());

    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getCellCluster_error_shouldThrowException() {

        CellCluster cellCluster = island.getCellCluster(-2, 33);
    }

    @Test
    public void workerAlreadyPlacedTest() throws InvalidMovementException {
        island.placeWorker(workerA, 3, 4);
        island.placeWorker(workerA, 2,3);

        assertEquals(3, workerA.getPosition()[0]);
        assertEquals(4, workerA.getPosition()[1]);
        assertEquals(workerA.getWorkerID(), island.getCellCluster(3,4).getWorkerID());
        assertEquals(workerA.getPlayerUsername(), island.getCellCluster(3,4).getWorkerOwnerUsername());

        assertFalse(island.getCellCluster(2,3).hasWorkerOnTop());
    }


}