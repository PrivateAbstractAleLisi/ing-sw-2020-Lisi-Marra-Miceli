package model;

import static org.junit.Assert.*;

import model.exception.InvalidCardException;
import model.exception.NoRemainingBlockException;
import model.gamemap.BlockTypeEnum;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.naming.LimitExceededException;
import java.util.ArrayList;
import java.util.List;

public class GameManagerTest {

    private GameManager game = null;

    @Before
    public void setup(){
        game = new GameManager();
    }

    @After
    public void tearDown(){
        game = null;
    }

    @Test
    public void getNumberOfBlocksRemaining_blockLevel1_shouldReturnNormally() {
        BlockTypeEnum block= BlockTypeEnum.LEVEL1;
        game.getNumberOfBlocksRemaining(block);
        assertEquals(22, game.getNumberOfBlocksRemaining(block));
    }

    @Test
    public void getNumberOfBlocksRemaining_blockLevel2_shouldReturnNormally() {

        BlockTypeEnum block= BlockTypeEnum.LEVEL2;
        game.getNumberOfBlocksRemaining(block);
        assertEquals(18, game.getNumberOfBlocksRemaining(block));
    }

    @Test
    public void getNumberOfBlocksRemaining_blockLevel3_shouldReturnNormally() {
        BlockTypeEnum block= BlockTypeEnum.LEVEL3;
        game.getNumberOfBlocksRemaining(block);
        assertEquals(14, game.getNumberOfBlocksRemaining(block));
    }

    @Test
    public void getNumberOfBlocksRemaining_blockDome_shouldReturnNormally() {
        BlockTypeEnum block= BlockTypeEnum.DOME;
        game.getNumberOfBlocksRemaining(block);
        assertEquals(18, game.getNumberOfBlocksRemaining(block));
    }

    @Test
    public void drawBlock_blockLevel1_shouldReturnNormally() throws NoRemainingBlockException {
        BlockTypeEnum block= BlockTypeEnum.LEVEL1;
        game.drawBlock(block);
        assertEquals(21, game.getNumberOfBlocksRemaining(block));
    }

    @Test (expected = NoRemainingBlockException.class)
    public void drawBlock_blockLevel1_shouldThrowException() throws NoRemainingBlockException {
        BlockTypeEnum block= BlockTypeEnum.LEVEL1;
        for (int i = 0; i<=23; i ++){
            game.drawBlock(block);
        }
    }

    @Test
    public void drawBlock_blockLevel2_shouldReturnNormally() throws NoRemainingBlockException {
        BlockTypeEnum block= BlockTypeEnum.LEVEL2;
        game.drawBlock(block);
        assertEquals(17, game.getNumberOfBlocksRemaining(block));
    }

    @Test (expected = NoRemainingBlockException.class)
    public void drawBlock_blockLevel2_shouldThrowException() throws NoRemainingBlockException {
        BlockTypeEnum block= BlockTypeEnum.LEVEL2;
        for (int i = 0; i<=19; i ++){
            game.drawBlock(block);
        }
    }

    @Test
    public void drawBlock_blockLevel3_shouldReturnNormally() throws NoRemainingBlockException {
        BlockTypeEnum block= BlockTypeEnum.LEVEL3;
        game.drawBlock(block);
        assertEquals(13, game.getNumberOfBlocksRemaining(block));
    }

    @Test (expected = NoRemainingBlockException.class)
    public void drawBlock_blockLevel3_shouldThrowException() throws NoRemainingBlockException {
        BlockTypeEnum block= BlockTypeEnum.LEVEL3;
        for (int i = 0; i<=15; i ++){
            game.drawBlock(block);
        }
    }

    @Test
    public void drawBlock_blockDome_shouldReturnNormally() throws NoRemainingBlockException {
        BlockTypeEnum block= BlockTypeEnum.DOME;
        game.drawBlock(block);
        assertEquals(17, game.getNumberOfBlocksRemaining(block));
    }

    @Test (expected = NoRemainingBlockException.class)
    public void drawBlock_blockDome_shouldThrowException() throws NoRemainingBlockException {
        BlockTypeEnum block= BlockTypeEnum.DOME;
        for (int i = 0; i<=19; i ++){
            game.drawBlock(block);
        }
    }

    @Test
    public void addPlayer_withString_shouldReturnNormally() throws LimitExceededException {
        game.addPlayer("matteo");
        assertTrue(game.isPlayerConnected("matteo"));
    }


    @Test (expected = LimitExceededException.class)
    public void addPlayer_withString_shouldThrowException() throws LimitExceededException {

        game.addPlayer("matteo");
        game.addPlayer("gabriele");
        game.addPlayer("alessandro");
        game.addPlayer("mattia");
    }

    @Test
    public void addPlayer_withPlayerClass_shouldReturnNormally() throws LimitExceededException {
        Player p1 = new Player("matteo");
        game.addPlayer(p1);
        assertTrue(game.isPlayerConnected(p1));
    }

    @Test (expected = LimitExceededException.class)
    public void addPlayer_withPlayerClass_shouldThrowException() throws LimitExceededException {
        Player p1=new Player("matteo");
        Player p2=new Player("gabriele");
        Player p3=new Player("alessandro");
        Player p4=new Player("mattia");
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);
        game.addPlayer(p4);
    }


    @Test
    public void removePlayer_withString_shouldReturnNormally() throws LimitExceededException {
        game.addPlayer("matteo");
        assertTrue( game.isPlayerConnected("matteo"));
        game.removePlayer("matteo");
        assertFalse(game.isPlayerConnected("matteo"));
    }

    @Test
    public void removePlayer_withPlayerClass_shouldReturnNormally() throws LimitExceededException {
        Player p1 = new Player("matteo");
        game.addPlayer(p1);
        assertTrue( game.isPlayerConnected(p1));
        game.removePlayer(p1);
        assertFalse(game.isPlayerConnected(p1));
    }

    @Test
    public void getPlayer_withString_shouldReturnNormally() throws LimitExceededException {
        Player p1= new Player("matteo");
        game.addPlayer(p1);
        assertEquals(game.getPlayer("matteo"),p1);
    }


    @Test
    public void getPlayers_normalPlayers_shouldReturnNormally() throws LimitExceededException {
        Player p1=new Player("matteo");
        Player p2=new Player("gabriele");
        Player p3=new Player("alessandro");
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);
        List<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        players.add(p3);
        assertEquals(players, game.getPlayers());
    }

    @Test
    public void selectCard_normalCard_shouldReturnNormally() throws InvalidCardException, LimitExceededException {
        game.selectCard("Apollo");
        List<String> c= new ArrayList<String>();
        c.add("Apollo");
        assertEquals(game.getSelectedCards(),c);
    }

    @Test (expected = InvalidCardException.class)
    public void selectCard_wrongCard_shouldThrowException() throws InvalidCardException, LimitExceededException {
        game.selectCard("Apollo");
        game.selectCard("matteo");
    }

    @Test (expected = LimitExceededException.class)
    public void selectCard_toManyCards_shouldThrowException() throws InvalidCardException, LimitExceededException {
        game.selectCard("Apollo");
        game.selectCard("Atlas");
        game.selectCard("Athena");
        game.selectCard("Demeter");
    }


    @Test
    public void takeCard_normalCard_shouldReturnNormally() throws InvalidCardException, LimitExceededException {
        game.selectCard("Apollo");
        game.takeCard("Apollo");
        List<String> c= new ArrayList<String>();
        c.add("Apollo");
        assertEquals(game.getTakenCards(),c);
    }

    @Test (expected = InvalidCardException.class)
    public void takeCard_wrongCard_shouldThrowException() throws InvalidCardException, LimitExceededException {
        game.selectCard("Apollo");
        game.takeCard("Athena");
    }
}
