package model.Gods;

import model.BoardManager;
import model.Player;
import model.exception.InvalidBuildException;
import model.exception.InvalidMovementException;
import model.exception.WinningException;
import model.gamemap.BlockTypeEnum;
import model.gamemap.Worker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import placeholders.IslandPrinter;

import static org.junit.Assert.*;

public class PrometheusTest {
    private Player player = null;
    private BoardManager boardManager= null;
    private IslandPrinter ip = null;

    @Before
    public void setUp() throws Exception {
        boardManager = new BoardManager();
        player = new Player("matteo", boardManager);
        player.setCard("Prometheus");
        boardManager.getIsland().placeWorker(player.getWorker(Worker.IDs.A), 2,2);
        ip = new IslandPrinter(boardManager.getIsland());
    }

    @After
    public void tearDown() throws Exception {
        player=null;
        boardManager=null;
    }

    @Test
    public void turn_twoBuildTurn_shouldReturnNormally() throws InvalidBuildException, CloneNotSupportedException, InvalidMovementException, WinningException {
        player.getCard().resetBehaviour();
        //first build
        player.getCard().build(player.getWorker(Worker.IDs.A), BlockTypeEnum.LEVEL1, 3, 3, boardManager.getIsland());
        //move without climbing
        player.getCard().move(player.getWorker(Worker.IDs.A), 2 ,3 , boardManager.getIsland());
        //second build
        player.getCard().build(player.getWorker(Worker.IDs.A), BlockTypeEnum.LEVEL2, 3, 3, boardManager.getIsland());
        ip.displayIsland();
    }

    @Test (expected = InvalidBuildException.class)
    public void turn_twoConsecutiveBuild_shouldThrowException() throws InvalidBuildException, CloneNotSupportedException {
        //first build
        player.getCard().build(player.getWorker(Worker.IDs.A), BlockTypeEnum.LEVEL1, 3, 3, boardManager.getIsland());
        //second build
        player.getCard().build(player.getWorker(Worker.IDs.A), BlockTypeEnum.LEVEL2, 3, 3, boardManager.getIsland());
    }

    @Test (expected = InvalidMovementException.class)
    public void turn_twoBuiltTurnWithClimbing_shouldThrowException() throws InvalidBuildException, CloneNotSupportedException, InvalidMovementException, WinningException {
        //first build
        player.getCard().build(player.getWorker(Worker.IDs.A), BlockTypeEnum.LEVEL1, 3, 3, boardManager.getIsland());
        //move with climbing
        player.getCard().move(player.getWorker(Worker.IDs.A), 3 ,3 , boardManager.getIsland());
    }
}