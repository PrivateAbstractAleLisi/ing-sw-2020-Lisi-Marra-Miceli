package it.polimi.ingsw.psp58.view.UI.GUI.boardstate;

import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.CV_CommandExecutedGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.CV_CommandRequestEvent;
import it.polimi.ingsw.psp58.model.TurnAction;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;

public interface GameStateAbstract {
    void handleClickOnButton(int x, int y);

    void handleClickOnButton(TurnAction buttonPressed);

    void handleClickOnButton(BlockTypeEnum blockClicked);

    void updateFromServer(CV_CommandExecutedGameEvent event);

    void updateFromServer(CV_CommandRequestEvent event);

    ViewGameEvent getEvent();
}
