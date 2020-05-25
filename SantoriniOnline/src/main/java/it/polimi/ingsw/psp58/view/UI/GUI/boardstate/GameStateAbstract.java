package it.polimi.ingsw.psp58.view.UI.GUI.boardstate;

import it.polimi.ingsw.psp58.event.gameEvents.ControllerGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;
import it.polimi.ingsw.psp58.model.gamemap.Worker;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.BoardSceneController;

public abstract class GameStateAbstract {


    public abstract void setState(BoardSceneController boardController);

    public abstract ControllerGameEvent handleClick (String username, int x, int y, Worker.IDs workerID, GameStateEnum state);

    public abstract ViewGameEvent getEvent();

    public abstract GameStateEnum getState();
}
