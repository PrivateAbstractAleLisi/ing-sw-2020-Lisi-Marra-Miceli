package model;

import static org.junit.Assert.*;

import model.exception.*;
import model.gamemap.BlockTypeEnum;

import model.gamemap.Worker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.naming.LimitExceededException;
import java.util.ArrayList;
import java.util.List;

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
    public void getNumberOfBlocksRemaining_blockLevel1_shouldReturnNormally() {
        BlockTypeEnum block= BlockTypeEnum.LEVEL1;
        boardManager.getNumberOfBlocksRemaining(block);
        assertEquals(22, boardManager.getNumberOfBlocksRemaining(block));
    }

    @Test
    public void getNumberOfBlocksRemaining_blockLevel2_shouldReturnNormally() {

        BlockTypeEnum block= BlockTypeEnum.LEVEL2;
        boardManager.getNumberOfBlocksRemaining(block);
        assertEquals(18, boardManager.getNumberOfBlocksRemaining(block));
    }

    @Test
    public void getNumberOfBlocksRemaining_blockLevel3_shouldReturnNormally() {
        BlockTypeEnum block= BlockTypeEnum.LEVEL3;
        boardManager.getNumberOfBlocksRemaining(block);
        assertEquals(14, boardManager.getNumberOfBlocksRemaining(block));
    }

    @Test
    public void getNumberOfBlocksRemaining_blockDome_shouldReturnNormally() {
        BlockTypeEnum block= BlockTypeEnum.DOME;
        boardManager.getNumberOfBlocksRemaining(block);
        assertEquals(18, boardManager.getNumberOfBlocksRemaining(block));
    }

    @Test
    public void drawBlock_blockLevel1_shouldReturnNormally() throws NoRemainingBlockException {
        BlockTypeEnum block= BlockTypeEnum.LEVEL1;
        boardManager.drawBlock(block);
        assertEquals(21, boardManager.getNumberOfBlocksRemaining(block));
    }

    @Test (expected = NoRemainingBlockException.class)
    public void drawBlock_blockLevel1_shouldThrowException() throws NoRemainingBlockException {
        BlockTypeEnum block= BlockTypeEnum.LEVEL1;
        for (int i = 0; i<=23; i ++){
            boardManager.drawBlock(block);
        }
    }

    @Test
    public void drawBlock_blockLevel2_shouldReturnNormally() throws NoRemainingBlockException {
        BlockTypeEnum block= BlockTypeEnum.LEVEL2;
        boardManager.drawBlock(block);
        assertEquals(17, boardManager.getNumberOfBlocksRemaining(block));
    }

    @Test (expected = NoRemainingBlockException.class)
    public void drawBlock_blockLevel2_shouldThrowException() throws NoRemainingBlockException {
        BlockTypeEnum block= BlockTypeEnum.LEVEL2;
        for (int i = 0; i<=19; i ++){
            boardManager.drawBlock(block);
        }
    }

    @Test
    public void drawBlock_blockLevel3_shouldReturnNormally() throws NoRemainingBlockException {
        BlockTypeEnum block= BlockTypeEnum.LEVEL3;
        boardManager.drawBlock(block);
        assertEquals(13, boardManager.getNumberOfBlocksRemaining(block));
    }

    @Test (expected = NoRemainingBlockException.class)
    public void drawBlock_blockLevel3_shouldThrowException() throws NoRemainingBlockException {
        BlockTypeEnum block= BlockTypeEnum.LEVEL3;
        for (int i = 0; i<=15; i ++){
            boardManager.drawBlock(block);
        }
    }

    @Test
    public void drawBlock_blockDome_shouldReturnNormally() throws NoRemainingBlockException {
        BlockTypeEnum block= BlockTypeEnum.DOME;
        boardManager.drawBlock(block);
        assertEquals(17, boardManager.getNumberOfBlocksRemaining(block));
    }

    @Test (expected = NoRemainingBlockException.class)
    public void drawBlock_blockDome_shouldThrowException() throws NoRemainingBlockException {
        BlockTypeEnum block= BlockTypeEnum.DOME;
        for (int i = 0; i<=19; i ++){
            boardManager.drawBlock(block);
        }
    }

    @Test
    public void addPlayer_withString_shouldReturnNormally() throws LimitExceededException, AlreadyExistingPlayer {
        boardManager.addPlayer("matteo");
        assertTrue(boardManager.isPlayerConnected("matteo"));
    }


    @Test (expected = LimitExceededException.class)
    public void addPlayer_withString_shouldThrowException() throws LimitExceededException, AlreadyExistingPlayer {
        boardManager.addPlayer("matteo");
        boardManager.addPlayer("gabriele");
        boardManager.addPlayer("alessandro");
        boardManager.addPlayer("daniele");
    }

    @Test
    public void addPlayer_withPlayerClass_shouldReturnNormally() throws LimitExceededException, AlreadyExistingPlayer {
        Player p1 = new Player("matteo");
        boardManager.addPlayer(p1);
        assertTrue(boardManager.isPlayerConnected(p1));
    }

    @Test (expected = LimitExceededException.class)
    public void addPlayer_withPlayerClass_shouldThrowException() throws LimitExceededException, AlreadyExistingPlayer {
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
    public void removePlayer_withStringWorkerA_shouldReturnNormally() throws LimitExceededException, AlreadyExistingPlayer, InvalidWorkerRemovalException, InvalidWorkerException, InvalidMovementException {
        Player player = new Player("matteo", boardManager);
        boardManager.getIsland().placeWorker(player.getWorker(Worker.IDs.A),2,2);
        boardManager.addPlayer(player);
        assertTrue( boardManager.isPlayerConnected("matteo"));
        boardManager.removePlayer("matteo");
        assertTrue(!boardManager.isPlayerConnected("matteo"));
    }

    @Test
    public void removePlayer_withPlayerWorkerA_shouldReturnNormally() throws LimitExceededException, AlreadyExistingPlayer, InvalidWorkerRemovalException, InvalidWorkerException, InvalidMovementException {
        Player player = new Player("matteo", boardManager);
        boardManager.getIsland().placeWorker(player.getWorker(Worker.IDs.A),2,2);
        player.getWorker(Worker.IDs.A).setPosition(2,2);
        boardManager.addPlayer(player);
        assertTrue( boardManager.isPlayerConnected("matteo"));
        boardManager.removePlayer(player);
        assertTrue(!boardManager.isPlayerConnected("matteo"));
    }

    @Test
    public void removePlayer_withStringWorkerB_shouldReturnNormally() throws LimitExceededException, AlreadyExistingPlayer, InvalidWorkerRemovalException, InvalidWorkerException, InvalidMovementException {
        Player player = new Player("matteo", boardManager);
        boardManager.getIsland().placeWorker(player.getWorker(Worker.IDs.B),2,2);
        boardManager.addPlayer(player);
        assertTrue( boardManager.isPlayerConnected("matteo"));
        boardManager.removePlayer("matteo");
        assertTrue(!boardManager.isPlayerConnected("matteo"));
    }

    @Test
    public void removePlayer_withPlayerWorkerB_shouldReturnNormally() throws LimitExceededException, AlreadyExistingPlayer, InvalidWorkerRemovalException, InvalidWorkerException, InvalidMovementException {
        Player player = new Player("matteo", boardManager);
        Worker worker = new Worker(Worker.IDs.B,player.getUsername());
        boardManager.getIsland().placeWorker(worker,2,2);
        player.getWorker(Worker.IDs.A).setPosition(2,2);
        boardManager.addPlayer(player);
        assertTrue( boardManager.isPlayerConnected("matteo"));
        boardManager.removePlayer(player);
        assertTrue(!boardManager.isPlayerConnected("matteo"));
    }



    @Test
    public void removePlayer_withPlayerClass_shouldReturnNormally() throws LimitExceededException, AlreadyExistingPlayer, InvalidWorkerRemovalException {
        Player p1 = new Player("matteo");
        boardManager.addPlayer(p1);
        assertTrue( boardManager.isPlayerConnected(p1));
        boardManager.removePlayer(p1);
        assertFalse(boardManager.isPlayerConnected(p1));
    }

    @Test
    public void getPlayer_withString_shouldReturnNormally() throws LimitExceededException, AlreadyExistingPlayer {
        Player p1= new Player("matteo");
        boardManager.addPlayer(p1);
        assertEquals(boardManager.getPlayer("matteo"),p1);
    }


    @Test
    public void getPlayers_normalPlayers_shouldReturnNormally() throws LimitExceededException, AlreadyExistingPlayer {
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
        boardManager.selectCard("Apollo");
        List<String> c= new ArrayList<String>();
        c.add("Apollo");
        assertEquals(boardManager.getSelectedCards(),c);
    }

    @Test (expected = InvalidCardException.class)
    public void selectCard_wrongCard_shouldThrowException() throws InvalidCardException, LimitExceededException {
        boardManager.selectCard("Apollo");
        boardManager.selectCard("matteo");
    }

    @Test (expected = LimitExceededException.class)
    public void selectCard_toManyCards_shouldThrowException() throws InvalidCardException, LimitExceededException {
        boardManager.selectCard("Apollo");
        boardManager.selectCard("Atlas");
        boardManager.selectCard("Athena");
        boardManager.selectCard("Demeter");
    }


    @Test
    public void takeCard_normalCard_shouldReturnNormally() throws InvalidCardException, LimitExceededException {
        boardManager.selectCard("Apollo");
        boardManager.takeCard("Apollo");
        List<String> c= new ArrayList<String>();
        c.add("Apollo");
        assertEquals(boardManager.getTakenCards(),c);
    }

    @Test (expected = InvalidCardException.class)
    public void takeCard_wrongCard_shouldThrowException() throws InvalidCardException, LimitExceededException {
        boardManager.selectCard("Apollo");
        boardManager.takeCard("Athena");
    }

}
