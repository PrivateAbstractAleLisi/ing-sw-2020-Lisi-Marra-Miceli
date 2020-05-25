package it.polimi.ingsw.psp58.model;

import it.polimi.ingsw.psp58.exceptions.*;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;
import it.polimi.ingsw.psp58.model.gamemap.Worker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.naming.LimitExceededException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BoardManagerTest {

    private BoardManager boardManager = null;

    @Before
    public void setup(){
        boardManager = new BoardManager();
    }

    @After
    public void tearDown(){
        boardManager = null;
    }


    @Test
    public void addPlayer_withString_shouldReturnNormally() throws LimitExceededException, AlreadyExistingPlayerException {
        boardManager.addPlayer("matteo");
        assertTrue(boardManager.isPlayerConnected("matteo"));
    }


    @Test (expected = LimitExceededException.class)
    public void addPlayer_withString_shouldThrowException() throws LimitExceededException, AlreadyExistingPlayerException {
        boardManager.addPlayer("matteo");
        boardManager.addPlayer("gabriele");
        boardManager.addPlayer("alessandro");
        boardManager.addPlayer("daniele");
    }

    @Test
    public void addPlayer_withPlayerClass_shouldReturnNormally() throws LimitExceededException, AlreadyExistingPlayerException {
        Player p1 = new Player("matteo");
        boardManager.addPlayer(p1);
        assertTrue(boardManager.isPlayerConnected(p1));
    }

    @Test (expected = LimitExceededException.class)
    public void addPlayer_withPlayerClass_shouldThrowException() throws LimitExceededException, AlreadyExistingPlayerException {
        Player p1=new Player("matteo");
        Player p2=new Player("gabriele");
        Player p3=new Player("alessandro");
        Player p4=new Player("mattia");
        boardManager.addPlayer(p1);
        boardManager.addPlayer(p2);
        boardManager.addPlayer(p3);
        boardManager.addPlayer(p4);
    }


    @Test
    public void removePlayer_withPlayerClass_shouldReturnNormally() throws LimitExceededException, AlreadyExistingPlayerException, InvalidWorkerRemovalException, InvalidWorkerException, InvalidMovementException {
        Player player = new Player("matteo", boardManager);
        boardManager.getIsland().placeWorker(player.getWorker(Worker.IDs.A),2,2);
        boardManager.getIsland().placeWorker(player.getWorker(Worker.IDs.B),3,4);
        boardManager.addPlayer(player);
        assertTrue( boardManager.isPlayerConnected("matteo"));
        boardManager.removePlayer(player);
        assertTrue(!boardManager.isPlayerConnected("matteo"));
    }

    @Test
    public void removePlayer_withString_shouldReturnNormally() throws LimitExceededException, AlreadyExistingPlayerException, InvalidWorkerRemovalException, InvalidWorkerException, InvalidMovementException {
        Player player = new Player("matteo", boardManager);
        boardManager.getIsland().placeWorker(player.getWorker(Worker.IDs.B),2,2);
        boardManager.getIsland().placeWorker(player.getWorker(Worker.IDs.A), 3, 3);
        boardManager.addPlayer(player);
        assertTrue( boardManager.isPlayerConnected("matteo"));
        boardManager.removePlayer("matteo");
        assertTrue(!boardManager.isPlayerConnected("matteo"));
    }

    @Test
    public void getPlayer_withString_shouldReturnNormally() throws LimitExceededException, AlreadyExistingPlayerException {
        Player p1= new Player("matteo");
        boardManager.addPlayer(p1);
        assertEquals(boardManager.getPlayer("matteo"),p1);
    }


    @Test
    public void getPlayers_normalPlayers_shouldReturnNormally() throws LimitExceededException, AlreadyExistingPlayerException {
        Player p1=new Player("matteo");
        Player p2=new Player("gabriele");
        Player p3=new Player("alessandro");
        boardManager.addPlayer(p1);
        boardManager.addPlayer(p2);
        boardManager.addPlayer(p3);
        List<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        players.add(p3);
        assertEquals(players, boardManager.getPlayers());
    }

    @Test
    public void selectCard_normalCard_shouldReturnNormally() throws InvalidCardException, LimitExceededException {
        boardManager.selectCard(CardEnum.APOLLO);
        List<CardEnum> c= new ArrayList<CardEnum>();
        c.add(CardEnum.APOLLO);
        assertEquals(boardManager.getSelectedCards(),c);
    }


    @Test (expected = LimitExceededException.class)
    public void selectCard_toManyCards_shouldThrowException() throws InvalidCardException, LimitExceededException {
        boardManager.selectCard(CardEnum.APOLLO);
        boardManager.selectCard(CardEnum.ATLAS);
        boardManager.selectCard(CardEnum.ATHENA);
        boardManager.selectCard(CardEnum.DEMETER);
    }


    @Test
    public void takeCard_normalCard_shouldReturnNormally() throws InvalidCardException, LimitExceededException {
        boardManager.selectCard(CardEnum.APOLLO);
        boardManager.takeCard(CardEnum.APOLLO);
        List<CardEnum> c= new ArrayList<CardEnum>();
        c.add(CardEnum.APOLLO);
        assertEquals(boardManager.getTakenCards(),c);
    }

    @Test (expected = InvalidCardException.class)
    public void takeCard_wrongCard_shouldThrowException() throws InvalidCardException, LimitExceededException {
        boardManager.selectCard(CardEnum.APOLLO);
        boardManager.takeCard(CardEnum.ATHENA);
    }

    @Test (expected = AlreadyExistingPlayerException.class)
    public void addPlayer_twoPlayerSameUsername_shouldThrowExcepetion() throws LimitExceededException, AlreadyExistingPlayerException {
        boardManager.addPlayer("gabriele");
        boardManager.addPlayer("gabriele");
    }

    @Test
    public void resetAllPlayerBehaviour() throws LimitExceededException, AlreadyExistingPlayerException {
        Player p1 = new Player("matteo");
        Player p2 = new Player("alessandro");
        boardManager.addPlayer(p1);
        boardManager.addPlayer(p2);
        p1.setCard(CardEnum.APOLLO);
        p2.setCard(CardEnum.ATHENA);
        p1.getBehaviour().setBlockPlacementLeft(10);
        p2.getBehaviour().setMovementsRemaining(10000);
        boardManager.resetAllPlayerBehaviour();
        Player p1Copy = new Player("matteo");
        Player p2Copy = new Player("alessandro");
        p1Copy.setCard(CardEnum.APOLLO);
        p2Copy.setCard(CardEnum.ATHENA);
        p1.getCard().resetBehaviour();
        p2.getCard().resetBehaviour();
        assertEquals(p1.getBehaviour().getBlockPlacementLeft(), p1Copy.getBehaviour().getBlockPlacementLeft());
        assertEquals(p2.getBehaviour().getMovementsRemaining(), p2Copy.getBehaviour().getMovementsRemaining());
    }
}
