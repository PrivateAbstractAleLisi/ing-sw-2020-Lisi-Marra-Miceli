package model.gamemap;

import model.exception.InvalidBuildException;
import model.exception.InvalidMovementException;
import model.exception.InvalidWorkerRemovalException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class IslandTest {

    private Island isla1;
    private Worker wa, wb, wc;
    private final String user = "TestoSterone98";

    @Before
    public void setUp() throws Exception {
        isla1 = new Island();
        wa = new Worker(Worker.IDs.A, user);
        wb = new Worker(Worker.IDs.B, user);
        wc = new Worker(Worker.IDs.A, "error");
    }

    @After
    public void tearDown() throws Exception {
        isla1 = null;
        wa = null;
        wb = null;
        wc = null;

    }

    @Test
    public void placeWorker() throws InvalidMovementException {
        isla1.placeWorker(wa, 3, 1);
        isla1.placeWorker(wb, 2, 1); //place worker, stesso worker già posizionato?
        assertTrue(isla1.getCellCluster(3, 1).hasWorkerOnTop());
        assertTrue(isla1.getCellCluster(2, 1).hasWorkerOnTop());

        //muovi, (aggiorna posizione) e controlla se ora c'è / non c'è più
        isla1.moveWorker(wa, 4, 4);
        assertFalse(isla1.getCellCluster(3, 1).hasWorkerOnTop());
        assertTrue(isla1.getCellCluster(4, 4).hasWorkerOnTop());

        //muovi, (aggiorna posizione) e controlla se ora c'è / non c'è più
        isla1.moveWorker(wb, 3, 4);
        assertFalse(isla1.getCellCluster(2, 1).hasWorkerOnTop());
        assertTrue(isla1.getCellCluster(3, 4).hasWorkerOnTop());

    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void placeWorker_OutOfBounds() throws InvalidMovementException, IndexOutOfBoundsException {
        isla1.placeWorker(wb, 10, -1);
        isla1.placeWorker(wb, 1, -1);
        isla1.placeWorker(wb, 101, 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void placeWorker_JustAboveLimit() throws InvalidMovementException, IndexOutOfBoundsException {
        isla1.placeWorker(wb, 5, 5);
    }


    @Test(expected = InvalidMovementException.class)
    public void placeWorker_OnAnother() throws InvalidMovementException {
        isla1.placeWorker(wb, 1, 1);
        System.out.println("DEBUG: 1 done.");
        isla1.placeWorker(wb, 1, 1);
        isla1.placeWorker(wb, 0, 4);
        isla1.placeWorker(wa, 3, 1);

    }

    @Test
    public void placeWorker_AndRemove() throws InvalidMovementException, InvalidWorkerRemovalException {
        isla1.placeWorker(wb, 1, 1);
        System.out.println("DEBUG: 1 done.");
        isla1.removeWorker(wb);
        isla1.placeWorker(wb, 1, 1);


    }

    @Test
    public void moveWorker() throws InvalidMovementException {
        isla1.placeWorker(wa, 3, 2);
        assertTrue(isla1.getCellCluster(3, 2).hasWorkerOnTop());
        //muovi, (aggiorna posizione) e controlla se ora c'è / non c'è più
        isla1.moveWorker(wa, 4, 4);
        assertFalse(isla1.getCellCluster(3, 2).hasWorkerOnTop());
        assertTrue(isla1.getCellCluster(4, 4).hasWorkerOnTop());
    }

    @Test
    public void moveWorker_fromA_toA() throws InvalidMovementException {
        isla1.placeWorker(wa, 2, 2);
        assertTrue(isla1.getCellCluster(2, 2).hasWorkerOnTop());
        isla1.moveWorker(wa, 2, 2);
        assertTrue(isla1.getCellCluster(2, 2).hasWorkerOnTop());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void moveWorker_out_of_bounds() throws IndexOutOfBoundsException, InvalidMovementException {
        isla1.moveWorker(wa, -3, -4);
    }
//TODO move before placing?

    @Test
    public void buildBlock() throws InvalidBuildException {
        isla1.buildBlock(BlockTypeEnum.LEVEL2, 3, 3);
        CellCluster target = isla1.getCellCluster(3, 3);
        assertEquals(target.getCostructionHeight(), 1);
        int[] expected = new int[]{2, 3};
        int[] test = target.toIntArrayWithHypo(BlockTypeEnum.LEVEL3);
        System.out.println("DEBUG, EXPECTED ORDER (as Int)");
        for (int i = 0; i < test.length; i++) {
            System.out.println(test[i]);

        }
        assertArrayEquals(target.toIntArrayWithHypo(BlockTypeEnum.LEVEL3), expected);
    }

    @Test(expected = InvalidBuildException.class)
    public void buildBlock_error_wrong_order() throws InvalidBuildException {
        isla1.buildBlock(BlockTypeEnum.LEVEL2, 2, 3);
        isla1.buildBlock(BlockTypeEnum.LEVEL3, 3, 3);
        isla1.buildBlock(BlockTypeEnum.DOME, 3, 3);
        isla1.buildBlock(BlockTypeEnum.LEVEL1, 3, 3);

    }

    @Test(expected = InvalidBuildException.class)
    public void buildBlock_error_wrong_order_no_dome() throws InvalidBuildException {
        isla1.buildBlock(BlockTypeEnum.LEVEL2, 2, 3);
        isla1.buildBlock(BlockTypeEnum.LEVEL3, 3, 3);
        isla1.buildBlock(BlockTypeEnum.LEVEL1, 3, 3);
        isla1.buildBlock(BlockTypeEnum.LEVEL3, 3, 3);

    }

    @Test(expected = InvalidBuildException.class)
    public void buildBlock_error_duplicate() throws InvalidBuildException {
        isla1.buildBlock(BlockTypeEnum.LEVEL1, 3, 3);
        isla1.buildBlock(BlockTypeEnum.LEVEL2, 3, 3);
        isla1.buildBlock(BlockTypeEnum.LEVEL3, 3, 3);
        isla1.buildBlock(BlockTypeEnum.LEVEL3, 3, 3);

    }

    @Test
    public void getCellCluster() throws InvalidBuildException, InvalidMovementException, CloneNotSupportedException {
        isla1.buildBlock(BlockTypeEnum.LEVEL1, 3, 3);
        isla1.buildBlock(BlockTypeEnum.LEVEL2, 3, 3);
        isla1.buildBlock(BlockTypeEnum.LEVEL3, 3, 3);
        isla1.placeWorker(wa, 3, 3);
        CellCluster gcc = isla1.getCellCluster(3, 3);
        assertTrue(gcc.hasWorkerOnTop());

    }

    @Test//(expected = InvalidWorkerRemovalException.class)
    public void removeWorker() throws InvalidMovementException, InvalidWorkerRemovalException {
        isla1.placeWorker(wa, 2, 1);
        isla1.removeWorker(wa);
        isla1.placeWorker(wa, 2, 1);
        assertTrue(!isla1.getCellCluster(2, 4).hasWorkerOnTop());
        isla1.placeWorker(wb, 1, 1);
        assertTrue(isla1.getCellCluster(1, 1).hasWorkerOnTop());
        isla1.moveWorker(wb, 4, 1);
        assertTrue(isla1.getCellCluster(4, 1).hasWorkerOnTop());
        isla1.removeWorker(wb);
        assertFalse(isla1.getCellCluster(4, 1).hasWorkerOnTop());
        isla1.removeWorker(wa);
        assertFalse(isla1.getCellCluster(3, 3).hasWorkerOnTop());

    }

    @Test(expected = InvalidWorkerRemovalException.class)
    public void removeWorker_no_Worker_in_cell() throws InvalidWorkerRemovalException {
        wb.setPosition(3, 4); //cosi non è null e non crasha per null pointer su position
        isla1.removeWorker(wb);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getCellCluster_error_oob() throws InvalidBuildException, InvalidMovementException, CloneNotSupportedException {

        CellCluster gcc = isla1.getCellCluster(-2, 33);


    }

    @Test
    public void workerAlreadyPlacedTest() throws InvalidMovementException {
        isla1.placeWorker(wa, 3, 4);
        isla1.placeWorker(wa, 2,3);

    }


}