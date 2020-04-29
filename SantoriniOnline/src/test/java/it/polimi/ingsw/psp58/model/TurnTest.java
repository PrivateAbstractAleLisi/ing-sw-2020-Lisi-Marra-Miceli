package it.polimi.ingsw.psp58.model;

import it.polimi.ingsw.psp58.exceptions.DefeatException;
import it.polimi.ingsw.psp58.exceptions.InvalidBuildException;
import it.polimi.ingsw.psp58.exceptions.InvalidMovementException;
import it.polimi.ingsw.psp58.exceptions.WinningException;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;
import it.polimi.ingsw.psp58.model.gamemap.Worker;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.psp58.model.TurnAction.BUILD;
import static it.polimi.ingsw.psp58.model.TurnAction.MOVE;
import static org.junit.Assert.*;

public class TurnTest {
    private Turn turn;
    private BoardManager boardManager;
    private Player currentPlayer;

    @Before
    public void setUp() throws Exception {
        boardManager = new BoardManager();
        currentPlayer= new Player("matteo", boardManager);
        turn = new Turn(currentPlayer, boardManager);
    }

    @After
    public void tearDown() throws Exception {
        boardManager = null;
        currentPlayer= null;
        turn = null;
    }

    @Test
    public void chooseWorker() {
        turn.chooseWorker(Worker.IDs.A);
        assertEquals(Worker.IDs.A, turn.getWorkerID());
    }

    @Test
    public void getCurrentPlayer() {
        assertEquals("matteo", turn.getCurrentPlayer().getUsername());
    }

    @Test
    public void getStartingPosition() throws InvalidMovementException {
        boardManager.getIsland().placeWorker(currentPlayer.getWorker(Worker.IDs.A), 2 , 2);
        turn.chooseWorker(currentPlayer.getWorker(Worker.IDs.A).getWorkerID());
        int [] startingPosition = new int[]{2, 2};
        assertEquals(startingPosition[0], turn.getStartingPosition()[0]);
        assertEquals(startingPosition[1], turn.getStartingPosition()[1]);
    }

    @Test
    public void checkMoveDuringTurn() throws InvalidMovementException, DefeatException, WinningException {
        getStartingPosition();
        currentPlayer.setCard(CardEnum.APOLLO);
        currentPlayer.getCard().move(currentPlayer.getWorker(Worker.IDs.A), 2, 3, boardManager.getIsland());
        turn.checkMoveDuringTurn();
    }

    @Test (expected = DefeatException.class)
    public void checkMoveDuringTurn_withMove_shouldThrowException() throws InvalidMovementException, DefeatException, WinningException {
        getStartingPosition();
        turn.checkMoveDuringTurn();

    }

    @Test
    public void validActions_availableMoveNotInTheCorner_shouldReturnNormally() throws InvalidMovementException {
        boardManager.getIsland().placeWorker(currentPlayer.getWorker(Worker.IDs.A), 2 , 2);
        turn.chooseWorker(currentPlayer.getWorker(Worker.IDs.A).getWorkerID());

        currentPlayer.setCard(CardEnum.APOLLO);
        List<int[]> validActions = new ArrayList<int[]>();
        validActions.add(new int[]{1, 1});
        validActions.add(new int[]{1, 2});
        validActions.add(new int[]{1, 3});

        validActions.add(new int[]{2, 1});
        validActions.add(new int[]{2, 3});

        validActions.add(new int[]{3, 1});
        validActions.add(new int[]{3, 2});
        validActions.add(new int[]{3, 3});

        List<int[]> actualValidActions = turn.validActions(Worker.IDs.A, MOVE);


        for (int i = 0; i< validActions.size(); i++) {
            int[] validAction = validActions.get(i);
            int[] actualValidAction = actualValidActions.get(i);

            assertEquals(validAction[0], actualValidAction[0]);
            assertEquals(validAction[1], actualValidAction[1]);
        }
    }

    @Test
    public void validActions_availableMoveInTheCorner_shouldReturnNormally() throws InvalidMovementException {
        boardManager.getIsland().placeWorker(currentPlayer.getWorker(Worker.IDs.A), 0, 0);
        turn.chooseWorker(currentPlayer.getWorker(Worker.IDs.A).getWorkerID());

        currentPlayer.setCard(CardEnum.APOLLO);
        List<int[]> validActions = new ArrayList<int[]>();
        validActions.add(new int[]{0, 1});

        validActions.add(new int[]{1, 0});
        validActions.add(new int[]{1, 1});

        List<int[]> actualValidActions = turn.validActions(Worker.IDs.A, MOVE);


        for (int i = 0; i< validActions.size(); i++) {
            int[] validAction = validActions.get(i);
            int[] actualValidAction = actualValidActions.get(i);

            assertEquals(validAction[0], actualValidAction[0]);
            assertEquals(validAction[1], actualValidAction[1]);
        }
    }

    @Test
    public void validActions_availableMoveWithTwoWorker_shouldReturnNormally() throws InvalidMovementException {
        boardManager.getIsland().placeWorker(currentPlayer.getWorker(Worker.IDs.A), 0, 0);
        turn.chooseWorker(currentPlayer.getWorker(Worker.IDs.A).getWorkerID());

        boardManager.getIsland().placeWorker(currentPlayer.getWorker(Worker.IDs.B), 1, 1);

        currentPlayer.setCard(CardEnum.ATLAS);
        List<int[]> validActions = new ArrayList<int[]>();
        validActions.add(new int[]{0, 1});

        validActions.add(new int[]{1, 0});

        List<int[]> actualValidActions = turn.validActions(Worker.IDs.A, MOVE);


        for (int i = 0; i< validActions.size(); i++) {
            int[] validAction = validActions.get(i);
            int[] actualValidAction = actualValidActions.get(i);

            assertEquals(validAction[0], actualValidAction[0]);
            assertEquals(validAction[1], actualValidAction[1]);
        }
    }

    @Test
    public void validActions_availableMoveWithFourWorkers_shouldReturnNormally() throws InvalidMovementException {
        boardManager.getIsland().placeWorker(currentPlayer.getWorker(Worker.IDs.A), 0, 0);
        turn.chooseWorker(currentPlayer.getWorker(Worker.IDs.A).getWorkerID());

        boardManager.getIsland().placeWorker(currentPlayer.getWorker(Worker.IDs.B), 1, 1);

        Player otherPlayer = new Player("gabriele", boardManager);
        boardManager.getIsland().placeWorker(otherPlayer.getWorker(Worker.IDs.A), 0, 1);

        currentPlayer.setCard(CardEnum.ATLAS);
        List<int[]> validActions = new ArrayList<int[]>();

        validActions.add(new int[]{1, 0});

        List<int[]> actualValidActions = turn.validActions(Worker.IDs.A, MOVE);


        for (int i = 0; i< validActions.size(); i++) {
            int[] validAction = validActions.get(i);
            int[] actualValidAction = actualValidActions.get(i);

            assertEquals(validAction[0], actualValidAction[0]);
            assertEquals(validAction[1], actualValidAction[1]);
        }
    }

    @Test
    public void validActions_availableMoveWithFourWorkers_withOneMove_shouldReturnNormally() throws InvalidMovementException, WinningException {
        boardManager.getIsland().placeWorker(currentPlayer.getWorker(Worker.IDs.A), 0, 0);
        turn.chooseWorker(currentPlayer.getWorker(Worker.IDs.A).getWorkerID());

        boardManager.getIsland().placeWorker(currentPlayer.getWorker(Worker.IDs.B), 1, 1);

        Player otherPlayer = new Player("gabriele", boardManager);
        boardManager.getIsland().placeWorker(otherPlayer.getWorker(Worker.IDs.A), 0, 1);

        currentPlayer.setCard(CardEnum.ATLAS);

        currentPlayer.getCard().move(currentPlayer.getWorker(Worker.IDs.A), 1, 0, boardManager.getIsland());
        List<int[]> validActions = new ArrayList<int[]>();


        List<int[]> actualValidActions = turn.validActions(Worker.IDs.A, MOVE);

        assertEquals(actualValidActions.isEmpty(), validActions.isEmpty());
    }

    @Test
    public void validActions_availableBuildNotInTheCorner_shouldReturnNormally() throws InvalidMovementException {
        boardManager.getIsland().placeWorker(currentPlayer.getWorker(Worker.IDs.A), 2 , 3);
        turn.chooseWorker(currentPlayer.getWorker(Worker.IDs.A).getWorkerID());

        currentPlayer.setCard(CardEnum.APOLLO);
        List<int[]> validActions = new ArrayList<int[]>();
        validActions.add(new int[]{1, 2});
        validActions.add(new int[]{1, 3});
        validActions.add(new int[]{1, 4});

        validActions.add(new int[]{2, 2});
        validActions.add(new int[]{2, 4});

        validActions.add(new int[]{3, 2});
        validActions.add(new int[]{3, 3});
        validActions.add(new int[]{3, 4});

        List<int[]> actualValidActions = turn.validActions(Worker.IDs.A, BUILD);


        for (int i = 0; i< validActions.size(); i++) {
            int[] validAction = validActions.get(i);
            int[] actualValidAction = actualValidActions.get(i);

            Assert.assertArrayEquals( actualValidAction, validAction);
        }
    }

    @Test
    public void validActions_availableBuildInTheCorner_shouldReturnNormally() throws InvalidMovementException {
        boardManager.getIsland().placeWorker(currentPlayer.getWorker(Worker.IDs.A), 0, 0);
        turn.chooseWorker(currentPlayer.getWorker(Worker.IDs.A).getWorkerID());

        currentPlayer.setCard(CardEnum.APOLLO);
        List<int[]> validActions = new ArrayList<int[]>();
        validActions.add(new int[]{0, 1});

        validActions.add(new int[]{1, 0});
        validActions.add(new int[]{1, 1});

        List<int[]> actualValidActions = turn.validActions(Worker.IDs.A, BUILD);


        for (int i = 0; i< validActions.size(); i++) {
            int[] validAction = validActions.get(i);
            int[] actualValidAction = actualValidActions.get(i);

            assertEquals(validAction[0], actualValidAction[0]);
            assertEquals(validAction[1], actualValidAction[1]);
        }
    }

    @Test
    public void validActions_availableBuildWithADome_shouldReturnNormally() throws InvalidMovementException, InvalidBuildException {
        boardManager.getIsland().placeWorker(currentPlayer.getWorker(Worker.IDs.A), 0, 0);
        turn.chooseWorker(currentPlayer.getWorker(Worker.IDs.A).getWorkerID());

        currentPlayer.setCard(CardEnum.APOLLO);

        boardManager.getIsland().buildBlock(BlockTypeEnum.LEVEL1, 1, 1);
        boardManager.getIsland().buildBlock(BlockTypeEnum.LEVEL2, 1, 1);
        boardManager.getIsland().buildBlock(BlockTypeEnum.LEVEL3, 1, 1);
        boardManager.getIsland().buildBlock(BlockTypeEnum.DOME, 1, 1);


        List<int[]> validActions = new ArrayList<int[]>();
        validActions.add(new int[]{0, 1});

        validActions.add(new int[]{1, 0});

        List<int[]> actualValidActions = turn.validActions(Worker.IDs.A, BUILD);


        for (int i = 0; i< validActions.size(); i++) {
            int[] validAction = validActions.get(i);
            int[] actualValidAction = actualValidActions.get(i);

            assertEquals(validAction[0], actualValidAction[0]);
            assertEquals(validAction[1], actualValidAction[1]);
        }
    }


    @Test
    public void NumberOfMove() {
        assertEquals(0, turn.getNumberOfMove());
        turn.setNumberOfMove(1);
        assertEquals(1, turn.getNumberOfMove());
    }

    @Test
    public void NumberOfBuild() {
        assertEquals(0, turn.getNumberOfBuild());
        turn.setNumberOfBuild(1);
        assertEquals(1, turn.getNumberOfBuild());
    }

}