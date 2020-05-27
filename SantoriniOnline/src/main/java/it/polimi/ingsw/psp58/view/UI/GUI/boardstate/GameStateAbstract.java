package it.polimi.ingsw.psp58.view.UI.GUI.boardstate;

import it.polimi.ingsw.psp58.event.gameEvents.ControllerGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.CV_CommandExecutedGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.CV_CommandRequestEvent;
import it.polimi.ingsw.psp58.model.TurnAction;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;
import it.polimi.ingsw.psp58.model.gamemap.Worker;
import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.BoardSceneController;

public abstract class GameStateAbstract {
    public abstract ControllerGameEvent handleClick(String username, int x, int y, Worker.IDs workerID, GameStateEnum state);

    public abstract void handleClickOnButton(int x, int y);

    public abstract void handleClickOnButton(TurnAction buttonPressed);

    public abstract void handleClickOnButton(BlockTypeEnum blockClicked);

    public abstract void updateFromServer(CV_CommandExecutedGameEvent event);

    public abstract void updateFromServer(CV_CommandRequestEvent event);

    public abstract ViewGameEvent getEvent();

    public abstract GameStateEnum getState();
}
