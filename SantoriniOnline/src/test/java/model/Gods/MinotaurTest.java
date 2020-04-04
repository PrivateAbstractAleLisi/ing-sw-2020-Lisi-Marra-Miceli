package model.Gods;

import model.BoardManager;
import model.Player;
import model.exception.InvalidMovementException;
import model.exception.WinningException;
import model.gamemap.Island;
import model.gamemap.Worker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import placeholders.IslandPrinter;

import static org.junit.Assert.*;

public class MinotaurTest {
    private Player player1 = null;
    private Player player2 = null;
    private BoardManager boardManager=null;
    private IslandPrinter ip = null;

    @Before
    public void setUp() throws Exception {
        boardManager = new BoardManager();

        player1 = new Player("matteo", boardManager);
        player1.setCard("Minotaur");

        player2 = new Player("gabriele", boardManager);
        player2.setCard("Athena");

        boardManager.addPlayer(player1);
        boardManager.addPlayer(player2);
    }

    @After
    public void tearDown() throws Exception {
        player1=null;
        player2=null;
        boardManager=null;
    }

    @Test
    public void move_validSpecialPower_shouldReturnNormally() throws InvalidMovementException, WinningException {
        boardManager.getIsland().placeWorker(player1.getWorker(Worker.IDs.A), 2,2);
        boardManager.getIsland().placeWorker(player2.getWorker(Worker.IDs.A),3 , 3);

        player1.getCard().move(player1.getWorker(Worker.IDs.A), 3, 3, boardManager.getIsland());
        assertEquals(4 ,player2.getWorker(Worker.IDs.A).getPosition()[0] );
        assertEquals(4 ,player2.getWorker(Worker.IDs.A).getPosition()[1] );
    }

    @Test (expected = InvalidMovementException.class)
    public void move_invalidMove_shouldReturnNormally() throws InvalidMovementException, WinningException {
        boardManager.getIsland().placeWorker(player1.getWorker(Worker.IDs.A), 3,3);
        boardManager.getIsland().placeWorker(player2.getWorker(Worker.IDs.A),4 , 4);

        player1.getCard().move(player1.getWorker(Worker.IDs.A), 3, 3, boardManager.getIsland());
    }
}