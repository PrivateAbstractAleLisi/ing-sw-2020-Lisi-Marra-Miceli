package it.polimi.ingsw.psp58.controller;

import it.polimi.ingsw.psp58.event.gameEvents.match.VC_PlayerCommandGameEvent;
import it.polimi.ingsw.psp58.model.BoardManager;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.model.Player;
import it.polimi.ingsw.psp58.model.TurnAction;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;
import it.polimi.ingsw.psp58.model.gamemap.Worker;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static it.polimi.ingsw.psp58.model.TurnAction.*;
import static org.junit.Assert.*;

public class TurnControllerTest {
    private TurnController turnController;
    private BoardManager boardManager;
    private Map<Integer, Player> turnSequence;
    private Room room;

    @Before
    public void setUp() throws Exception {
        boardManager= new BoardManager();

        //creates a mock room
        room = new Room(2, "0");

        //setup player 1
        boardManager.addPlayer("Jack");
        Player p1 = boardManager.getPlayer("Jack");
        p1.setCard(CardEnum.PROMETHEUS);
        boardManager.getIsland().placeWorker(p1.getWorker(Worker.IDs.A), 1, 2);
        boardManager.getIsland().placeWorker(p1.getWorker(Worker.IDs.B), 3, 2);

        //setup player 2
        boardManager.addPlayer("Adrian");
        Player p2 = boardManager.getPlayer("Adrian");
        p2.setCard(CardEnum.ATLAS);
        boardManager.getIsland().placeWorker(p2.getWorker(Worker.IDs.A), 1, 3);
        boardManager.getIsland().placeWorker(p2.getWorker(Worker.IDs.B), 3, 3);

        //set a mock turnSequence
        turnSequence = new HashMap<>();
        turnSequence.put(0, boardManager.getPlayer("Jack"));
        turnSequence.put(1, boardManager.getPlayer("Adrian"));

        //create the it.polimi.ingsw.sp58.controller
        turnController = new TurnController(boardManager, turnSequence, 2,room);
    }

    @After
    public void tearDown() throws Exception {
        boardManager=null;
        turnController=null;
        turnSequence=null;
        room = null;
    }

    //simulates a turn
    @Test
    public void firstTurn_normalTurn_shouldReturnNormally() {
        turnController.firstTurn();
        assertEquals(1, turnController.getCurrentTurnNumber());
        assertEquals(turnSequence.get(0).getUsername(), turnController.getCurrentPlayerUser());
    }



    @Test
    public void handleEvent_CommandLegalMove_shouldReturnNormally() {
        firstTurn_normalTurn_shouldReturnNormally();
        int[] position = new int[]{2,2};
        VC_PlayerCommandGameEvent commandEvent = new VC_PlayerCommandGameEvent("", MOVE, "Jack",  position, Worker.IDs.A, null);
        turnController.handleEvent(commandEvent);
        Assert.assertArrayEquals( position, boardManager.getPlayer("Jack").getWorker(Worker.IDs.A).getPosition());
        assertEquals(1, turnController.getCurrentTurnInstance().getNumberOfMove());
        assertEquals(0, turnController.getCurrentTurnInstance().getNumberOfBuild());
    }

    @Test
    public void handleEvent_CommandNotLegalMove_shouldThrowException() {
        firstTurn_normalTurn_shouldReturnNormally();
        int[] position = new int[]{1,3};
        VC_PlayerCommandGameEvent commandEvent = new VC_PlayerCommandGameEvent("", MOVE, "Jack",  position, Worker.IDs.A, null);
        turnController.handleEvent(commandEvent);
        position =new int[]{1,2};
        Assert.assertArrayEquals( position, boardManager.getPlayer("Jack").getWorker(Worker.IDs.A).getPosition());
        assertEquals(0, turnController.getCurrentTurnInstance().getNumberOfMove());
        assertEquals(0, turnController.getCurrentTurnInstance().getNumberOfBuild());
    }

    @Test
    public void handleEvent_CommandMoveNotHisTurn_shouldReturnNormally() {
        firstTurn_normalTurn_shouldReturnNormally();
        int[] position = new int[]{1,4};
        VC_PlayerCommandGameEvent commandEvent = new VC_PlayerCommandGameEvent("", MOVE, "Adrian",  position, Worker.IDs.A, null);
        turnController.handleEvent(commandEvent);
        position = new int[]{1,3};
        Assert.assertArrayEquals( position, boardManager.getPlayer("Adrian").getWorker(Worker.IDs.A).getPosition());
    }

    @Test
    public void handleEvent_CommandLegalBuild_shouldReturnNormally(){
        handleEvent_CommandLegalMove_shouldReturnNormally();
        int[] position = new int[]{2,3};
        VC_PlayerCommandGameEvent commandEvent = new VC_PlayerCommandGameEvent("", BUILD, "Jack",  position, Worker.IDs.A, null);
        turnController.handleEvent(commandEvent);
        assertEquals(1 , boardManager.getIsland().getCellCluster(position[0], position[1]).getCostructionHeight());
        assertEquals(1, turnController.getCurrentTurnInstance().getNumberOfMove());
        assertEquals(1, turnController.getCurrentTurnInstance().getNumberOfBuild());
    }

    @Test
    public void handleEvent_CommandBuildInvalidCellCluster_shouldThrowException(){
        handleEvent_CommandLegalMove_shouldReturnNormally();
        int[] position = new int[]{2,2};
        VC_PlayerCommandGameEvent commandEvent = new VC_PlayerCommandGameEvent("", BUILD, "Jack",  position, Worker.IDs.A, null);
        turnController.handleEvent(commandEvent);
        assertEquals(0 , boardManager.getIsland().getCellCluster(position[0], position[1]).getCostructionHeight());
        assertEquals(1, turnController.getCurrentTurnInstance().getNumberOfMove());
        assertEquals(0, turnController.getCurrentTurnInstance().getNumberOfBuild());
    }

    @Test
    public void handleEvent_CommandBuildInvalidBlock_shouldReturnNormally(){
        handleEvent_CommandLegalMove_shouldReturnNormally();
        int[] position = new int[]{3,3};
        VC_PlayerCommandGameEvent commandEvent = new VC_PlayerCommandGameEvent("", BUILD, "Jack",  position, Worker.IDs.A, BlockTypeEnum.DOME);
        turnController.handleEvent(commandEvent);
        assertEquals(0 , boardManager.getIsland().getCellCluster(position[0], position[1]).getCostructionHeight());
        assertEquals(1, turnController.getCurrentTurnInstance().getNumberOfMove());
        assertEquals(0, turnController.getCurrentTurnInstance().getNumberOfBuild());
    }

    @Test
    public void handleEvent_CommandBuildNotHisTurn_shouldReturnNormally(){
        handleEvent_CommandLegalMove_shouldReturnNormally();
        int[] position = new int[]{1,4};
        VC_PlayerCommandGameEvent commandEvent = new VC_PlayerCommandGameEvent("", BUILD, "Adrian",  position, Worker.IDs.A, null);
        turnController.handleEvent(commandEvent);
        assertEquals(0, boardManager.getIsland().getCellCluster(position[0], position[1]).getCostructionHeight());
        assertEquals(1, turnController.getCurrentTurnInstance().getNumberOfMove());
        assertEquals(0, turnController.getCurrentTurnInstance().getNumberOfBuild());
    }

    @Test
    public void nextTurn_normalTurn_shouldReturnNormally(){
        handleEvent_CommandLegalBuild_shouldReturnNormally();
        VC_PlayerCommandGameEvent commandEvent = new VC_PlayerCommandGameEvent("", PASS, "Jack", null , null, null);
        turnController.handleEvent(commandEvent);
        assertEquals(turnSequence.get(1).getUsername(), turnController.getCurrentPlayerUser());
    }

    @Test
    public void handleEvent_VC_PlayerCommandGameEvent_random() {



        turnController.firstTurn();
        assertEquals(turnController.getCurrentPlayerUser(), "Jack");

        for (int i = 0; i < 36; i++) {


            Random random = new Random();
            String descrition = "test";
            TurnAction action = random.nextInt(1) == 1 ? MOVE : BUILD;
            int workerChoice = random.nextInt(1);
            Worker.IDs worker = workerChoice == 1 ? Worker.IDs.A : Worker.IDs.B;
            int x, y;
            x = random.nextInt(7)-2;
            y = random.nextInt(7)-2;
            int[] position = new int[] {x, y};

            boolean isOutOfBound = x>4 || x<0 || y>4 || y<0;
            VC_PlayerCommandGameEvent legalEvent = new VC_PlayerCommandGameEvent(descrition,
                    action,
                    "Jack",
                    position,
                    worker,
                    null);

            if (isOutOfBound) {
                assertThrows(IndexOutOfBoundsException.class, () -> {
                    turnController.handleEvent(legalEvent);
                });
            }
            else {
                turnController.handleEvent(legalEvent);
            }


            System.out.println(i+1 + "done");
        }


    }




}