package it.polimi.ingsw.psp58.controller;

import it.polimi.ingsw.psp58.event.gameEvents.match.VC_PlayerCommandGameEvent;
import it.polimi.ingsw.psp58.exceptions.AlreadyExistingPlayerException;
import it.polimi.ingsw.psp58.exceptions.InvalidMovementException;
import it.polimi.ingsw.psp58.model.*;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;
import it.polimi.ingsw.psp58.model.gamemap.Worker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import it.polimi.ingsw.psp58.view.UI.CLI.utility.IslandUtility;

import javax.naming.LimitExceededException;
import java.util.*;

import static it.polimi.ingsw.psp58.model.TurnAction.BUILD;
import static it.polimi.ingsw.psp58.model.TurnAction.MOVE;
import static it.polimi.ingsw.psp58.model.gamemap.Worker.IDs.A;
import static it.polimi.ingsw.psp58.model.gamemap.Worker.IDs.B;
import static org.junit.Assert.*;

public class TurnControllerTest3Players {
    private TurnController turnController;
    private BoardManager boardManager;
    private Map<Integer, Player> turnSequence;
    private Room room;

    private void setup1() throws LimitExceededException, AlreadyExistingPlayerException, InvalidMovementException {
        boardManager= new BoardManager();

        //creates a mock room
        room = new Room(3, "0");

        //setup player 1
        boardManager.addPlayer("jack");
        Player p1 = boardManager.getPlayer("jack");
        p1.setColor(WorkerColors.ORANGE);
        p1.setCard(CardEnum.ARTEMIS);
        boardManager.getIsland().placeWorker(p1.getWorker(A), 1, 2);
        boardManager.getIsland().placeWorker(p1.getWorker(Worker.IDs.B), 3, 2);

        //setup player 2
        boardManager.addPlayer("adrian");
        Player p2 = boardManager.getPlayer("adrian");
        p2.setColor(WorkerColors.BLUE);
        p2.setCard(CardEnum.ATLAS);
        boardManager.getIsland().placeWorker(p2.getWorker(A), 1, 3);
        boardManager.getIsland().placeWorker(p2.getWorker(Worker.IDs.B), 3, 3);

        //setup player 3
        boardManager.addPlayer("lorena");
        Player p3 = boardManager.getPlayer("lorena");
        p3.setCard(CardEnum.DEMETER);
        boardManager.getIsland().placeWorker(p3.getWorker(A), 4, 4);
        boardManager.getIsland().placeWorker(p3.getWorker(Worker.IDs.B), 0, 0);
        p3.setColor(WorkerColors.PINK);

        //set a mock turnSequence
        turnSequence = new HashMap<>();
        turnSequence.put(0, boardManager.getPlayer("jack"));
        turnSequence.put(1, boardManager.getPlayer("adrian"));
        turnSequence.put(2, boardManager.getPlayer("lorena"));



        //create the it.polimi.ingsw.sp58.controller
        turnController = new TurnController(boardManager, turnSequence, 3,room);

        turnController.setTurnSequence(turnSequence);
        turnController.setNumberOfPlayers(3);
    }

    private void print() {
        IslandUtility printer = new IslandUtility(boardManager.getIsland().getIslandDataCopy());
        printer.displayIsland();
    }
    @Before
    public void setUp() throws Exception {
        setup1();
        print();

    }

    @After
    public void tearDown() throws Exception {
        boardManager=null;
        turnController=null;
        turnSequence=null;
        room = null;
    }

    @Test
    public void loseTest() throws Exception {
        System.out.println("LOSE TEST:");
        tearDown();
        setup1();
        print();
        turnController.firstTurn();
        assertEquals(turnController.getCurrentPlayerUser(), "jack");

        Set<VC_PlayerCommandGameEvent> commands = new LinkedHashSet<>();
        String descrition = "test";

        int[] position = new int[] { 2-1, 2-1};

        //artemis si muove due volte
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                MOVE,
                "jack",
                position,
                A,
                null));

        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "jack",
                new int[] { 2-1, 1-1},
                A,
                null));

        //NEXT
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                TurnAction.PASS,
                "jack",
                null,
                null,
                null));

        //ATLAS
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                MOVE,
                "adrian",
                new int[] { 1-1, 3-1},
                A,
                null));

        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "adrian",
                new int[] { 1-1, 2-1},
                A,
                BlockTypeEnum.DOME));

        //NEXT
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                TurnAction.PASS,
                "adrian",
                null,
                null,
                null));

        commands.add(new VC_PlayerCommandGameEvent(descrition,
                MOVE,
                "lorena",
                new int[] { 5-1, 4-1},
                A,
                null));

        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "lorena",
                new int[] { 5-1, 3-1},
                A,
                null));

        //NEXT
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                TurnAction.PASS,
                "lorena",
                null,
                null,
                null));


        //artemis si muove due volte
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                MOVE,
                "jack",
                new int[] { 3-1, 1-1},
                A,
                null));

        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "jack",
                new int[] { 2-1, 1-1},
                A,
                null));

        //NEXT
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                TurnAction.PASS,
                "jack",
                null,
                null,
                null));

        //ATLAS
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                MOVE,
                "adrian",
                new int[] { 4-1, 5-1},
                B,
                null));

        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "adrian",
                new int[] { 4-1, 4-1},
                B,
                BlockTypeEnum.DOME));

        //NEXT
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                TurnAction.PASS,
                "adrian",
                null,
                null,
                null));

        //TERZO
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                MOVE,
                "lorena",
                new int[] { 5-1, 5-1},
                A,
                null));

        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "lorena",
                new int[] { 5-1, 4-1},
                A,
                null));

        //NEXT
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                TurnAction.PASS,
                "lorena",
                null,
                null,
                null));




        //artemis si muove due volte
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                MOVE,
                "jack",
                new int[] { 3-1, 2-1},
                A,
                null));

        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "jack",
                new int[] { 2-1, 1-1},
                A,
                null));

        //NEXT
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                TurnAction.PASS,
                "jack",
                null,
                null,
                null));



        //ATLAS
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                MOVE,
                "adrian",
                new int[] { 3-1, 5-1},
                B,
                null));

        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "adrian",
                new int[] { 4-1, 5-1},
                B,
                BlockTypeEnum.DOME));

        //NEXT
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                TurnAction.PASS,
                "adrian",
                null,
                null,
                null));



        //TERZO
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                MOVE,
                "lorena",
                new int[] { 5-1, 4-1},
                A,
                null));

        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "lorena",
                new int[] { 5-1, 3-1},
                A,
                null));

        //NEXT
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                TurnAction.PASS,
                "lorena",
                null,
                null,
                null));

        //artemis si muove due volte
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                MOVE,
                "jack",
                new int[] { 3-1, 1-1},
                A,
                null));

        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "jack",
                new int[] { 2-1, 1-1},
                A,
                null));

        //NEXT
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                TurnAction.PASS,
                "jack",
                null,
                null,
                null));



        //ATLAS
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                MOVE,
                "adrian",
                new int[] { 2-1, 3-1},
                A,
                null));

        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "adrian",
                new int[] { 2-1, 2-1},
                A,
                BlockTypeEnum.DOME));

        //NEXT
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                TurnAction.PASS,
                "adrian",
                null,
                null,
                null));



        //TERZO
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                MOVE,
                "lorena",
                new int[] { 5-1, 5-1},
                A,
                null));

        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "lorena",
                new int[] { 5-1, 4-1},
                A,
                null));

        //NEXT
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                TurnAction.PASS,
                "lorena",
                null,
                null,
                null));


        //artemis si muove due volte
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                MOVE,
                "jack",
                new int[] { 4-1, 2-1},
                B,
                null));

        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "jack",
                new int[] { 5-1, 3-1},
                B,
                null));

        //NEXT
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                TurnAction.PASS,
                "jack",
                null,
                null,
                null));


        //ATLAS
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                MOVE,
                "adrian",
                new int[] { 3-1, 3-1},
                A,
                null));

        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "adrian",
                new int[] { 2-1, 3-1},
                A,
                null));

        //NEXT
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                TurnAction.PASS,
                "adrian",
                null,
                null,
                null));




        for (VC_PlayerCommandGameEvent com : commands) {
            turnController.handleEvent(com);
            print();
        }


    }
}