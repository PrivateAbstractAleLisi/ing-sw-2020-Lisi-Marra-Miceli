package it.polimi.ingsw.psp58.controller;

import it.polimi.ingsw.psp58.event.gameEvents.prematch.VC_ChallengerChosenFirstPlayerEvent;
import it.polimi.ingsw.psp58.exceptions.AlreadyExistingPlayerException;
import it.polimi.ingsw.psp58.model.BoardManager;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.model.Player;
import it.polimi.ingsw.psp58.model.WorkerColors;
import it.polimi.ingsw.psp58.view.VirtualView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.naming.LimitExceededException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class PreGameControllerTest {

    PreGameController preGameController;
    BoardManager boardManager;
    Room room3;

    private void fillRoom() {
        room3 = new Room(3, "test");
        room3.addUser("Turing", mock(VirtualView.class));

        room3.addUser("Rice", mock(VirtualView.class));

        room3.addUser("MasterTheorem", mock(VirtualView.class));
    }

    private void fillBM() throws LimitExceededException, AlreadyExistingPlayerException {
        boardManager = new BoardManager();
        boardManager.addPlayer(new Player("Turing", boardManager));
        boardManager.addPlayer(new Player("Rice", boardManager));
        boardManager.addPlayer(new Player("MasterTheorem", boardManager));
        boardManager.getPlayer("Turing").setCard(CardEnum.APOLLO);
        boardManager.getPlayer("Rice").setCard(CardEnum.PAN);
        boardManager.getPlayer("MasterTheorem").setCard(CardEnum.ZEUS);
    }

    @Before
    public void setUp() throws Exception {
        fillRoom();
        fillBM();
        preGameController = new PreGameController(boardManager, room3);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void start() {
        preGameController.start();
    }

    @Test
    public void chooseChallenger() {
        preGameController.chooseChallenger();
        String challenger = preGameController.getChallenger();

        Map<Integer, Player> seq = preGameController.getTurnSequence();
        assertTrue(challenger.equals("Turing") ||
                challenger.equals("Rice") ||
                challenger.equals("MasterTheorem"));

        boolean first = false, second = false, last = false;

        //checks if the challenger choice is random
        for (int i = 0; i < 2048; i++) {
            preGameController.chooseChallenger();
            if (!first) {
                if (preGameController.getChallenger().equals("Turing")) {
                    first = true;
                    System.out.println("first is the challenger");
                }
            }
            if (!second) {
                if (preGameController.getChallenger().equals("Turing")) {
                    second = true;
                    System.out.println("second is the challenger");
                }
            }
            if (!last) {
                if (preGameController.getChallenger().equals("MasterTheorem")) {
                    System.out.println("third is the challenger");
                    last = true;
                }
            }
        }
        assertTrue(first && second && last);

    }

    @Test
    public void getPlayersCardsCorrespondence() {
    }

    @Test
    public void getTurnSequence() {
    }

    private boolean noDupes(Object[] array) {
        return Arrays.stream(array).allMatch(new HashSet<>()::add);
    }

    @Test
    public void setColors() throws Exception {
        WorkerColors[] valArr = WorkerColors.values();
        List<WorkerColors> values = Arrays.asList(valArr);
        preGameController.start();

        try {
            preGameController.handleEvent(new VC_ChallengerChosenFirstPlayerEvent("Turing"));
        } catch (Exception e) {
            preGameController.handleEvent(new VC_ChallengerChosenFirstPlayerEvent("Rice"));
        }
        assertEquals(preGameController.getTurnSequence().get(0).getColor(), WorkerColors.ORANGE);
        assertEquals(preGameController.getTurnSequence().get(1).getColor(), WorkerColors.BLUE);
        assertEquals(preGameController.getTurnSequence().get(2).getColor(), WorkerColors.PINK);
        WorkerColors[] chosen = new WorkerColors[]{preGameController.getTurnSequence().get(0).getColor(), preGameController.getTurnSequence().get(1).getColor(), preGameController.getTurnSequence().get(2).getColor()};
        chosen = new WorkerColors[]{preGameController.getTurnSequence().get(0).getColor(), preGameController.getTurnSequence().get(1).getColor(), preGameController.getTurnSequence().get(2).getColor()};
        assertTrue(noDupes(chosen));
    }

}