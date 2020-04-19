package model.Gods;

import model.*;
import model.gamemap.*;
import model.exception.*;
import org.junit.*;
import view.CLI.utility.IslandUtility;

import static org.junit.Assert.*;

public class ArtemisTest {
    BoardManager boardManager = null;
    Card card = null;
    Card card2 = null;
    Player player1 = null;
    Player player2 = null;
    Island island = null;
    Worker worker1 = null;
    Worker worker2 = null;
    Worker worker1B = null;
    Worker worker2B = null;
    IslandUtility ip = null;

    @Before
    public void setUp() throws Exception {
        boardManager = new BoardManager();
        boardManager.addPlayer("Gabriele");
        boardManager.addPlayer("Matteo");

        island = boardManager.getIsland();

        player1 = boardManager.getPlayer("Gabriele");
        player2 = boardManager.getPlayer("Matteo");
        player1.setCard(CardEnum.ARTEMIS);
        player2.setCard(CardEnum.ATLAS);
        worker1 = player1.getWorker(Worker.IDs.A);
        worker2 = player2.getWorker(Worker.IDs.A);
        worker1B = player1.getWorker(Worker.IDs.B);
        worker2B = player2.getWorker(Worker.IDs.B);
        card = player1.getCard();
        card2 = player2.getCard();

    }

    @After
    public void tearDown() throws Exception {
        player1.setCard((Card) null);
        card = null;
    }

    @Test //DONE
    public void distance() {
        assertEquals(5, card.distance(0, 0, 4, 3), 0);
    }

    @Test
    public void placeWorker_correctInput() throws InvalidMovementException, InvalidBuildException, CloneNotSupportedException {
        island.buildBlock(BlockTypeEnum.LEVEL1, 3, 4);

        island.placeWorker(worker1, 2, 2);

        card.placeWorker(worker2, 1, 1, island);
    }

    @Test(expected = InvalidMovementException.class)
    public void placeWorker_WrongInput_PlayerAlreadyOnTop() throws InvalidMovementException, CloneNotSupportedException {
        island.placeWorker(worker1, 4, 4);

        card.placeWorker(worker2, 4, 4, island);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void placeWorker_WrongInput_ErrorIndex() throws InvalidMovementException, CloneNotSupportedException {
        island.placeWorker(worker1, 4, 4);

        card.placeWorker(worker2, 6, 4, island);
    }

    @Test(expected = InvalidMovementException.class)
    public void placeWorker_WrongInput_WorkerAlreadyPlaced() throws InvalidMovementException, CloneNotSupportedException {
        island.placeWorker(worker1, 4, 4);

        card.placeWorker(worker1, 4, 4, island);
    }

    @Test(expected = InvalidMovementException.class)
    public void placeWorker_WrongInput_DomeOnTop() throws InvalidMovementException, InvalidBuildException, CloneNotSupportedException {
        island.buildBlock(BlockTypeEnum.DOME, 3, 4);

        card.placeWorker(worker2, 3, 4, island);
    }

    @Test
    public void move_Simple_RightMove() throws InvalidMovementException, WinningException, CloneNotSupportedException {
        card.placeWorker(worker1, 0, 0, island);
        card.placeWorker(worker2, 0, 1, island);

        card.resetBehaviour();
        card.move(worker1, 1, 1, island);
    }

    @Test
    public void move_RightMove() throws InvalidMovementException, WinningException, InvalidBuildException, CloneNotSupportedException {
        card.placeWorker(worker1, 0, 0, island);
        island.buildBlock(BlockTypeEnum.LEVEL1, 1, 1);
        card.placeWorker(worker2, 0, 1, island);

        card.resetBehaviour();
        card.move(worker1, 1, 1, island);
    }

    @Test(expected = InvalidMovementException.class)
    public void move_Simple_WrongMove_PlayerAlreadyOnCell() throws InvalidMovementException, WinningException, CloneNotSupportedException {
        card.placeWorker(worker1, 0, 0, island);
        card.placeWorker(worker2, 0, 1, island);

        card.resetBehaviour();
        card.move(worker1, 0, 1, island);
    }

    @Test(expected = InvalidMovementException.class)
    public void move_Simple_WrongMove_TooFar() throws InvalidMovementException, WinningException, CloneNotSupportedException {
        card.placeWorker(worker1, 0, 0, island);

        card.resetBehaviour();
        card.move(worker1, 2, 1, island);
    }

    @Test(expected = InvalidMovementException.class)
    public void move_WrongMove_TooHigh() throws InvalidMovementException, WinningException, InvalidBuildException, CloneNotSupportedException {
        card.placeWorker(worker1, 0, 0, island);
        island.buildBlock(BlockTypeEnum.LEVEL1, 0, 1);
        island.buildBlock(BlockTypeEnum.LEVEL2, 0, 1);

        card.resetBehaviour();
        card.move(worker1, 0, 1, island);
    }

    @Test(expected = InvalidMovementException.class)
    public void move_WrongMove_DomeOnTop() throws InvalidMovementException, WinningException, InvalidBuildException, CloneNotSupportedException {
        card.placeWorker(worker1, 0, 0, island);
        island.buildBlock(BlockTypeEnum.DOME, 1, 1);

        card.resetBehaviour();
        card.move(worker1, 1, 1, island);
    }

    @Test(expected = InvalidMovementException.class)
    public void move_WrongMove_NoMovementesRemaining() throws InvalidMovementException, WinningException, CloneNotSupportedException {
        card.placeWorker(worker1, 0, 0, island);
        card.resetBehaviour();

        BehaviourManager behaviourManager = player1.getBehaviour();
        behaviourManager.setMovementsRemaining(0);

        card.move(worker1, 1, 1, island);
    }

    @Test(expected = InvalidMovementException.class)
    public void move_WrongMove_NoClimbAdmit() throws InvalidMovementException, WinningException, InvalidBuildException, CloneNotSupportedException {
        card.placeWorker(worker1, 0, 0, island);
        island.buildBlock(BlockTypeEnum.LEVEL1, 0, 1);
        card.resetBehaviour();

        BehaviourManager behaviourManager = player1.getBehaviour();
        behaviourManager.setCanClimb(false);

        card.move(worker1, 0, 1, island);
    }

    @Test(expected = InvalidMovementException.class)
    public void move_WrongMove_SameCoordinates() throws InvalidMovementException, WinningException, CloneNotSupportedException {
        card.placeWorker(worker1, 0, 0, island);

        card.resetBehaviour();
        card.move(worker1, 0, 0, island);
    }

    @Test(expected = WinningException.class)
    public void move_RightMove_Win() throws InvalidMovementException, WinningException, InvalidBuildException, CloneNotSupportedException {
        island.buildBlock(BlockTypeEnum.LEVEL1, 0, 0);
        island.buildBlock(BlockTypeEnum.LEVEL2, 0, 0);
        card.placeWorker(worker1, 0, 0, island);
        island.buildBlock(BlockTypeEnum.LEVEL1, 0, 1);
        island.buildBlock(BlockTypeEnum.LEVEL2, 0, 1);
        island.buildBlock(BlockTypeEnum.LEVEL3, 0, 1);

        card.resetBehaviour();
        card.move(worker1, 0, 1, island);
    }

    @Test(expected = InvalidMovementException.class)
    public void move_WrongMove_TooMuchMovements() throws InvalidMovementException, WinningException, CloneNotSupportedException {
        card.placeWorker(worker1, 0, 0, island);

        card.resetBehaviour();
        card.move(worker1, 0, 0, island);
        card.move(worker1, 0, 0, island);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void move_WrongMove_IndexError() throws InvalidMovementException, WinningException, CloneNotSupportedException {
        card.placeWorker(worker1, 0, 0, island);

        card.resetBehaviour();
        card.move(worker1, 0, -5, island);
    }

    @Test
    public void build_RightBuild() throws InvalidMovementException, InvalidBuildException, CloneNotSupportedException, WinningException {
        card.placeWorker(worker1, 0, 0, island);
        card.move(worker1, 1, 0, island);

        card.resetBehaviour();
        card.build(worker1, BlockTypeEnum.LEVEL1, 1, 1, island);
    }

    @Test
    public void build_ComplexCorrectSequence() throws InvalidMovementException, InvalidBuildException, CloneNotSupportedException, WinningException {
        card.placeWorker(worker1, 0, 0, island);
        card.move(worker1, 1, 0, island);

        card.build(worker1, BlockTypeEnum.LEVEL1, 1, 1, island);
        card.resetBehaviour();
        card.build(worker1, BlockTypeEnum.LEVEL1, 0, 0, island);
        card.resetBehaviour();
        card.build(worker1, BlockTypeEnum.LEVEL1, 0, 1, island);
        card.resetBehaviour();
        card.build(worker1, BlockTypeEnum.LEVEL1, 2, 1, island);
        card.resetBehaviour();
        card.build(worker1, BlockTypeEnum.LEVEL2, 1, 1, island);
        card.resetBehaviour();
        card.build(worker1, BlockTypeEnum.LEVEL3, 1, 1, island);
        card.resetBehaviour();
        card.build(worker1, BlockTypeEnum.DOME, 1, 1, island);
    }

    @Test(expected = InvalidBuildException.class)
    public void build_WrongBuild_tooFar() throws InvalidMovementException, InvalidBuildException, CloneNotSupportedException, WinningException {
        card.placeWorker(worker1, 0, 0, island);
        card.move(worker1, 1, 0, island);

        card.resetBehaviour();
        card.build(worker1, BlockTypeEnum.LEVEL1, 1, 2, island);
    }

    @Test(expected = InvalidBuildException.class)
    public void build_WrongBuild_PlayerOnTop1() throws InvalidMovementException, InvalidBuildException, CloneNotSupportedException, WinningException {
        card.placeWorker(worker1, 0, 0, island);
        card.move(worker1, 1, 0, island);

        card.resetBehaviour();
        card.build(worker1, BlockTypeEnum.LEVEL1, 1, 0, island);
    }

    @Test(expected = InvalidBuildException.class)
    public void build_WrongBuild_PlayerOnTop2() throws InvalidMovementException, InvalidBuildException, CloneNotSupportedException, WinningException {
        card.placeWorker(worker1, 0, 0, island);
        card.move(worker1, 1, 0, island);
        card.placeWorker(worker2, 1, 1, island);

        card.resetBehaviour();
        card.build(worker1, BlockTypeEnum.LEVEL1, 1, 1, island);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void build_WrongBuild_ErrorIndex() throws InvalidMovementException, InvalidBuildException, CloneNotSupportedException, WinningException {
        card.placeWorker(worker1, 0, 0, island);
        card.move(worker1, 1, 0, island);

        card.resetBehaviour();
        card.build(worker1, BlockTypeEnum.LEVEL1, 7, 0, island);
    }

    @Test(expected = InvalidBuildException.class)
    public void build_WrongBuild_DomeOnTop() throws InvalidMovementException, InvalidBuildException, CloneNotSupportedException, WinningException {
        card.placeWorker(worker1, 0, 0, island);
        island.buildBlock(BlockTypeEnum.DOME, 1, 1);
        card.move(worker1, 1, 0, island);

        card.resetBehaviour();
        card.build(worker1, BlockTypeEnum.LEVEL1, 1, 1, island);
    }

    @Test(expected = InvalidBuildException.class)
    public void build_WrongBuild_TooMuchBlockPlacement() throws InvalidMovementException, InvalidBuildException, CloneNotSupportedException, WinningException {
        card.placeWorker(worker1, 0, 0, island);
        card.move(worker1, 1, 0, island);

        card.resetBehaviour();
        card.build(worker1, BlockTypeEnum.LEVEL1, 1, 1, island);
        card.build(worker1, BlockTypeEnum.LEVEL1, 1, 2, island);
    }

    @Test(expected = InvalidBuildException.class)
    public void build_WrongBuild_WrongOrderPosition() throws InvalidMovementException, InvalidBuildException, CloneNotSupportedException, WinningException {
        card.placeWorker(worker1, 0, 0, island);
        card.move(worker1, 1, 0, island);

        card.build(worker1, BlockTypeEnum.LEVEL1, 1, 1, island);
        card.resetBehaviour();
        card.build(worker1, BlockTypeEnum.LEVEL3, 1, 1, island);
    }

    @Test(expected = InvalidBuildException.class)
    public void build_WrongBuild_BuildDomeEveryWhere() throws InvalidMovementException, InvalidBuildException, CloneNotSupportedException, WinningException {
        card.placeWorker(worker1, 0, 0, island);
        card.move(worker1, 1, 0, island);

        card.resetBehaviour();

        card.build(worker1, BlockTypeEnum.DOME, 1, 1, island);
    }

    //god specific test
    @Test //DONE
    public void resetBehaviour() {
        BehaviourManager behaviourManager = player1.getBehaviour();
        behaviourManager.setCanClimb(false);
        behaviourManager.setCanBuildDomeEverywhere(true);
        behaviourManager.setBlockPlacementLeft(25);
        behaviourManager.setMovementsRemaining(0);

        card.resetBehaviour();

        assertTrue(behaviourManager.isCanClimb());
        assertFalse(behaviourManager.isCanBuildDomeEverywhere());
        assertEquals(1, behaviourManager.getBlockPlacementLeft());
        assertEquals(2, behaviourManager.getMovementsRemaining());
    }

    @Test
    public void move_RightMove_SecondMove() throws InvalidMovementException, WinningException, InvalidBuildException, CloneNotSupportedException {
        card.placeWorker(worker1, 0, 0, island);
        island.buildBlock(BlockTypeEnum.LEVEL1, 1, 1);
        card.placeWorker(worker2, 0, 1, island);

        card.resetBehaviour();
        card.move(worker1, 1, 1, island);
        card.move(worker1, 2, 1, island);
    }

    @Test (expected = IllegalArgumentException.class)
    public void move_WrongMove_SecondMoveBack() throws InvalidMovementException, WinningException, InvalidBuildException, CloneNotSupportedException {
        card.placeWorker(worker1, 0, 0, island);
        island.buildBlock(BlockTypeEnum.LEVEL1, 1, 1);
        card.placeWorker(worker2, 0, 1, island);

        card.resetBehaviour();
        card.move(worker1, 1, 1, island);
        card.move(worker1, 0, 0, island);
    }
}