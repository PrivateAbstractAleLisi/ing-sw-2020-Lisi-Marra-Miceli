package it.polimi.ingsw.psp58.model.gods;


import it.polimi.ingsw.psp58.controller.Room;
import it.polimi.ingsw.psp58.controller.TurnController;
import it.polimi.ingsw.psp58.event.gameEvents.match.VC_PlayerCommandGameEvent;
import it.polimi.ingsw.psp58.model.*;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;
import it.polimi.ingsw.psp58.model.gamemap.Worker;
import it.polimi.ingsw.psp58.view.UI.CLI.utility.IslandUtility;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.cglib.core.Block;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static it.polimi.ingsw.psp58.model.TurnAction.BUILD;
import static it.polimi.ingsw.psp58.model.TurnAction.MOVE;
import static it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum.DOME;
import static it.polimi.ingsw.psp58.model.gamemap.Worker.IDs.A;
import static org.junit.Assert.assertEquals;


public class ChronusControllerTest {

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
        boardManager.addPlayer("jack");
        Player p1 = boardManager.getPlayer("jack");
        p1.setCard(CardEnum.CHRONUS);
        p1.setColor(WorkerColors.BLUE);
        boardManager.getIsland().placeWorker(p1.getWorker(Worker.IDs.A), 1, 2);
        boardManager.getIsland().placeWorker(p1.getWorker(Worker.IDs.B), 3, 2);

        //setup player 2
        boardManager.addPlayer("adrian");
        Player p2 = boardManager.getPlayer("adrian");
        p2.setCard(CardEnum.DEMETER);
        p2.setColor(WorkerColors.ORANGE);
        boardManager.getIsland().placeWorker(p2.getWorker(Worker.IDs.A), 1, 3);
        boardManager.getIsland().placeWorker(p2.getWorker(Worker.IDs.B), 3, 3);

        //set a mock turnSequence
        turnSequence = new HashMap<>();
        turnSequence.put(0, boardManager.getPlayer("jack"));
        turnSequence.put(1, boardManager.getPlayer("adrian"));

        //create the it.polimi.ingsw.sp58.controller
        turnController = new TurnController(boardManager, turnSequence, 2,room);


    }

    private void print() {
        IslandUtility printer = new IslandUtility(boardManager.getIsland().getIslandDataCopy());
        printer.displayIsland();
    }

    @After
    public void tearDown() throws Exception {
        boardManager=null;
        turnController=null;
        turnSequence=null;
        room = null;
    }

    @Test
    public void chronusWinsTest() {

        turnController.firstTurn();

        Set<VC_PlayerCommandGameEvent> commands = new LinkedHashSet<>();
        String descrition = "test";

        int[] position = new int[]{2 - 1, 2 - 1};

        //----CHRONUS----///
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                MOVE,
                "jack",
                position,
                A,
                null));

        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "jack",
                new int[]{2 - 1, 1 - 1},
                A,
                null));

        //NEXT
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                TurnAction.PASS,
                "jack",
                null,
                null,
                null));



            //----ADRIAN-----///
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                MOVE,
                "adrian",
                new int[]{1 - 1, 4 - 1},
                A,
                null));
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "adrian",
                new int[]{1 - 1, 5 - 1},
                A, null
                ));
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "adrian",
                new int[]{2 - 1, 5 - 1},
                A, null
        ));
        //NEXT
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                TurnAction.PASS,
                "adrian",
                null,
                null,
                null));



        //----CHRONUS----///
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                MOVE,
                "jack",
                new int[]{1 - 1, 1 - 1},
                A,
                null));

        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "jack",
                new int[]{2 - 1, 1 - 1},
                A,
                null));

        //NEXT
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                TurnAction.PASS,
                "jack",
                null,
                null,
                null));



        //----ADRIAN-----///
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                MOVE,
                "adrian",
                new int[]{2 - 1, 4 - 1},
                A,
                null));
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "adrian",
                new int[]{2 - 1, 5 - 1},
                A,
                null));
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "adrian",
                new int[]{3 - 1, 5 - 1},
                A,
                null));
        //NEXT
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                TurnAction.PASS,
                "adrian",
                null,
                null,
                null));

        //----CHRONUS----///
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                MOVE,
                "jack",
                new int[]{1 - 1, 2 - 1},
                A,
                null));

        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "jack",
                new int[]{2 - 1, 1 - 1},
                A,
                null));

        //NEXT
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                TurnAction.PASS,
                "jack",
                null,
                null,
                null));

        //----ADRIAN-----///
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                MOVE,
                "adrian",
                new int[]{1 - 1, 4 - 1},
                A,
                null));
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "adrian",
                new int[]{2 - 1, 5 - 1},
                A,
                null));

        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "adrian",
                new int[]{1 - 1, 5 - 1},
                A,
                null));
        //NEXT
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                TurnAction.PASS,
                "adrian",
                null,
                null,
                null));



        //----CHRONUS----///
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                MOVE,
                "jack",
                new int[]{2 - 1, 2 - 1},
                A,
                null));

        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "jack",
                new int[]{2 - 1, 1 - 1},
                A,
                null));

        //NEXT
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                TurnAction.PASS,
                "jack",
                null,
                null,
                null));



        //----ADRIAN-----///
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                MOVE,
                "adrian",
                new int[]{2 - 1, 4 - 1},
                A,
                null));
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "adrian",
                new int[]{3 - 1, 5 - 1},
                A,
                null));

        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "adrian",
                new int[]{2 - 1, 5 - 1},
                A,
                null));
        //NEXT
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                TurnAction.PASS,
                "adrian",
                null,
                null,
                null));


        //----CHRONUS----///
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                MOVE,
                "jack",
                new int[]{3 - 1, 2 - 1},
                A,
                null));

        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "jack",
                new int[]{3 - 1, 1 - 1},
                A,
                null));

        //NEXT
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                TurnAction.PASS,
                "jack",
                null,
                null,
                null));

        //----ADRIAN-----///
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                MOVE,
                "adrian",
                new int[]{3 - 1, 4 - 1},
                A,
                null));
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "adrian",
                new int[]{3 - 1, 5 - 1},
                A,
                null));

        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "adrian",
                new int[]{4 - 1, 5 - 1},
                A,
                null));
        //NEXT
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                TurnAction.PASS,
                "adrian",
                null,
                null,
                null));

        //----CHRONUS----///
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                MOVE,
                "jack",
                new int[]{4 - 1, 2 - 1},
                A,
                null));

        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "jack",
                new int[]{3 - 1, 1 - 1},
                A,
                null));

        //NEXT
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                TurnAction.PASS,
                "jack",
                null,
                null,
                null));


        //----ADRIAN-----///
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                MOVE,
                "adrian",
                new int[]{2 - 1, 4 - 1},
                A,
                null));
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "adrian",
                new int[]{1 - 1, 5 - 1},
                A,
                null));

        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "adrian",
                new int[]{3 - 1, 5 - 1},
                A,
                null));
        //NEXT
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                TurnAction.PASS,
                "adrian",
                null,
                null,
                null));

        //----CHRONUS----///
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                MOVE,
                "jack",
                new int[]{3 - 1, 2 - 1},
                A,
                null));

        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "jack",
                new int[]{3 - 1, 1 - 1},
                A,
                null));

        //NEXT
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                TurnAction.PASS,
                "jack",
                null,
                null,
                null));


        //----ADRIAN-----///
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                MOVE,
                "adrian",
                new int[]{1 - 1, 4 - 1},
                A,
                null));
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "adrian",
                new int[]{1 - 1, 5 - 1},
                A,
                null));

        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "adrian",
                new int[]{1 - 1, 3 - 1},
                A,
                null));

        //NEXT
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                TurnAction.PASS,
                "adrian",
                null,
                null,
                null));


        //----CHRONUS----///
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                MOVE,
                "jack",
                new int[]{4 - 1, 2 - 1},
                A,
                null));

        commands.add(new VC_PlayerCommandGameEvent(descrition,
                BUILD,
                "jack",
                new int[]{3 - 1, 1 - 1},
                A,
                null));

        //NEXT
        commands.add(new VC_PlayerCommandGameEvent(descrition,
                TurnAction.PASS,
                "jack",
                null,
                null,
                null));



        for (VC_PlayerCommandGameEvent com : commands) {
            turnController.handleEvent(com);
            print();
        }

        assertEquals(boardManager.getIsland().getNumberOfCompleteTowers(),5);
    }
}
