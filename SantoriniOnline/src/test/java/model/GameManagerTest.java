package model;

import static org.junit.Assert.*;
import model.gamemap.BlockTypeEnum;

public class GameManagerTest {

    @org.junit.Test
    public void getNumberOfBlocksRemaining_blockLevel1_shouldReturnNormally() {
        GameManager game=new GameManager();
        BlockTypeEnum block= BlockTypeEnum.LEVEL1;
        game.getNumberOfBlocksRemaining(block);
        assertEquals(22, game.getNumberOfBlocksRemaining(block));
    }

    @org.junit.Test
    public void getNumberOfBlocksRemaining_blockLevel2_shouldReturnNormally() {
        GameManager game= new GameManager();
        BlockTypeEnum block= BlockTypeEnum.LEVEL1;
        game.getNumberOfBlocksRemaining(block);
        assertEquals(18, game.getNumberOfBlocksRemaining(block));
    }

    @org.junit.Test
    public void getNumberOfBlocksRemaining_blockLevel3_shouldReturnNormally() {
        GameManager game= new GameManager();
        BlockTypeEnum block= BlockTypeEnum.LEVEL2;
        game.getNumberOfBlocksRemaining(block);
        assertEquals(14, game.getNumberOfBlocksRemaining(block));
    }

    @org.junit.Test
    public void getNumberOfBlocksRemaining_blockDome_shouldReturnNormally() {
        GameManager game= new GameManager();
        BlockTypeEnum block= BlockTypeEnum.LEVEL3;
        game.getNumberOfBlocksRemaining(block);
        assertEquals(18, game.getNumberOfBlocksRemaining(block));
    }


    @org.junit.Test
    public void drawBlock_blockLevel1_shouldReturnNormally() {
        GameManager game=new GameManager();
        BlockTypeEnum block= BlockTypeEnum.LEVEL1;
        game.drawBlock(block);
        assertEquals(21, game.getNumberOfBlocksRemaining(block));
    }

    @org.junit.Test
    public void drawBlock_blockLevel2_shouldReturnNormally() {
        GameManager game=new GameManager();
        BlockTypeEnum block= BlockTypeEnum.LEVEL2;
        game.drawBlock(block);
        assertEquals(17, game.getNumberOfBlocksRemaining(block));
    }

    @org.junit.Test
    public void drawBlock_blockLevel3_shouldReturnNormally() {
        GameManager game=new GameManager();
        BlockTypeEnum block= BlockTypeEnum.LEVEL3;
        game.drawBlock(block);
        assertEquals(13, game.getNumberOfBlocksRemaining(block));
    }

    @org.junit.Test
    public void drawBlock_blockDome_shouldReturnNormally() {
        GameManager game=new GameManager();
        BlockTypeEnum block= BlockTypeEnum.LEVEL3;
        game.drawBlock(block);
        assertEquals(17, game.getNumberOfBlocksRemaining(block));
    }


    @org.junit.Test
    public void addPlayer_normalPlayer_shouldReturnNormally() {
        GameManager game= new GameManager();
        Player player= new Player("matteo");
        game.addPlayer(player);
        assertTrue(game.isPlayerConnected(player));
    }



    @org.junit.Test
    public void removePlayer_normalPlayer_shouldReturnNormally() {
        GameManager game=new GameManager();
        Player player= new Player("matteo");
        game.addPlayer(player);
        assertTrue( game.isPlayerConnected(player));
        game.removePlayer(player);
        assertFalse(game.isPlayerConnected(player));
    }


}