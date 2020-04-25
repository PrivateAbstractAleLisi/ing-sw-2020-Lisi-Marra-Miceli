package view;

import event.gameEvents.match.CV_NewTurnEvent;
import networking.client.SantoriniClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import view.UI.CLI.CLIView;

import java.util.ArrayList;
import java.util.List;

public class CLIViewTest {

    CLIView cli = new CLIView(new SantoriniClient());
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void start() {
    }

    @Test
    public void handleEvent() {
        List<String> turn = new ArrayList<>();

        turn.add("luigi");
        turn.add("marco");

        cli.handleEvent(new CV_NewTurnEvent("marco", "marco", turn));
    }

    @Test
    public void testHandleEvent() {
    }
}