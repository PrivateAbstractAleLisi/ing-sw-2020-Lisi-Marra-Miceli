package it.polimi.ingsw.psp58.view.UI.GUI.boardstate;

import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.CV_CommandExecutedGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.CV_CommandRequestEvent;
import it.polimi.ingsw.psp58.model.TurnAction;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;

public class WaitGameState implements GameStateAbstract {

    public WaitGameState() {
        //Just create the object
    }

    @Override
    public void handleClickOnButton(int x, int y) {
        System.out.println("Wait State Handle Click - Not yet implemented");
        //Show a popup
    }

    @Override
    public void handleClickOnButton(TurnAction buttonPressed) {
        System.out.println("Wait State Handle Click On Button - ERROR");
        //PRINT ERROR
    }

    @Override
    public void handleClickOnButton(BlockTypeEnum blockClicked) {
        System.out.println("Wait State Handle Click On Block - ERROR");
        //PRINT ERROR
    }

    @Override
    public void updateFromServer(CV_CommandExecutedGameEvent event) {
        System.out.println("Wait State Handle CV_CommandExecutedGameEvent - ERROR");
        //PRINT ERROR
    }

    @Override
    public void updateFromServer(CV_CommandRequestEvent event) {
        System.out.println("Wait State Handle CV_CommandRequestEvent - ERROR");
        //PRINT ERROR
    }

    @Override
    public ViewGameEvent getEvent() {
        return null;
    }
}
