package it.polimi.ingsw.psp58.view.UI.GUI.boardstate;

import it.polimi.ingsw.psp58.event.gameEvents.ControllerGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.ViewGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.CV_WaitMatchGameEvent;
import it.polimi.ingsw.psp58.event.gameEvents.prematch.CV_WaitPreMatchGameEvent;
import it.polimi.ingsw.psp58.model.TurnAction;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;
import it.polimi.ingsw.psp58.model.gamemap.Worker;
import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import it.polimi.ingsw.psp58.view.UI.GUI.controller.BoardSceneController;

public class WaitGameState extends GameStateAbstract {
    private final GUI gui;
    private final GameStateEnum state = GameStateEnum.NOT_YOUR_TURN;

    public WaitGameState(CV_WaitPreMatchGameEvent event, GUI gui) {
        this.gui=gui;
    }

    public WaitGameState(GUI gui) {
        this.gui=gui;
    }

    @Override
    public void setState(BoardSceneController boardController) {
        boardController.setWaitingView();
    }

    @Override
    public ControllerGameEvent handleClick(String username, int x, int y, Worker.IDs workerID, GameStateEnum state) {
        return null;
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
        System.out.println("Wait State Handle Click On Button - ERROR");
        //PRINT ERROR
    }

    @Override
    public ViewGameEvent getEvent() {
        return null;
    }

    @Override
    public GameStateEnum getState() {
        return state;
    }

    //@Overload
    public void setState(BoardSceneController boardController, CV_WaitMatchGameEvent event) {
        boardController.setWaitingView();
        updateTurnSequence();
    }

    private void updateTurnSequence() {
        //aggiorna la sequenza su con updateTurnSequence(evento)
        //todo
    }


}
