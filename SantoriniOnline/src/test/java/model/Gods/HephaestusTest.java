package model.Gods;

import model.BoardManager;
import model.Player;
import model.exception.InvalidBuildException;
import model.exception.InvalidMovementException;
import model.exception.WinningException;
import model.gamemap.BlockTypeEnum;
import model.gamemap.Island;
import model.gamemap.Worker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HephaestusTest {
    private Player player = null;
    private BoardManager boardManager= null;



    @Before
    public void setup() throws InvalidMovementException {
        boardManager = new BoardManager();
        player = new Player("matteo", boardManager);
        player.setCard("Hephaestus");
        boardManager.getIsland().placeWorker(player.getWorker(Worker.IDs.A), 2,2);
    }

    @After
    public void tearDown(){
        player=null;
        boardManager=null;
    }

    @Test
    public void build_twoValidBuild_shouldReturnNormally() throws InvalidMovementException, WinningException, InvalidBuildException, CloneNotSupportedException {
        player.getCard().move(player.getWorker(Worker.IDs.A), 2 ,3 , boardManager.getIsland());
        player.getCard().build(player.getWorker(Worker.IDs.A), BlockTypeEnum.LEVEL1, 3, 3, boardManager.getIsland());
        player.getCard().build(player.getWorker(Worker.IDs.A), BlockTypeEnum.LEVEL2, 3, 3, boardManager.getIsland());
        assertEquals(2, boardManager.getIsland().getCellCluster(3,3 ).getCostructionHeight() );
    }

    @Test (expected = InvalidBuildException.class)
    public void build_invalidDomeBuild_shouldThrowException() throws InvalidMovementException, WinningException, InvalidBuildException, CloneNotSupportedException {
        //simulation of two turns
        //first turn:
        player.getCard().move(player.getWorker(Worker.IDs.A), 2 ,3 , boardManager.getIsland());
        player.getCard().build(player.getWorker(Worker.IDs.A), BlockTypeEnum.LEVEL1, 3, 3, boardManager.getIsland());
        player.getCard().build(player.getWorker(Worker.IDs.A), BlockTypeEnum.LEVEL2, 3, 3, boardManager.getIsland());
        player.getCard().resetBehaviour();
        //second turn:
        player.getCard().move(player.getWorker(Worker.IDs.A), 2 , 4, boardManager.getIsland());
        player.getCard().build(player.getWorker(Worker.IDs.A), BlockTypeEnum.LEVEL3, 3, 3, boardManager.getIsland());
        player.getCard().build(player.getWorker(Worker.IDs.A), BlockTypeEnum.DOME, 3, 3, boardManager.getIsland());
    }
}