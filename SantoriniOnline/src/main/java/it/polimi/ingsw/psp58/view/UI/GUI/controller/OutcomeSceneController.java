package it.polimi.ingsw.psp58.view.UI.GUI.controller;

import it.polimi.ingsw.psp58.event.gameEvents.lobby.VC_NewGameResponseEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.CV_GameOverEvent;
import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class OutcomeSceneController {

    GUI gui;
    CV_GameOverEvent received;

    //UI ELEMENTS

    public Label labelTitle;
    public Label labelSubTitle;
    public Button buttonPlayAgain, buttonQuitDesktop, buttonDisconnect;


    /**
     * when the game is over
     * @param event
     * @param gui
     */
    public void initAndFill(CV_GameOverEvent event, GUI gui) {
        this.gui = gui;
        this.received = event;
        String winner = received.getWinner();

        if (weAreTheChampions(winner)) {
            labelTitle.setText("YOU WIN");
        }
        else {
            labelTitle.setText("YOU LOSE");
        }
        labelSubTitle.setText(winner.toUpperCase() + " IS THE WINNER");

    }

    private boolean weAreTheChampions (String winner) {
        return gui.getUsername().toLowerCase().equals(winner.toLowerCase());
    }

    /**
     * when you lost 
     * @param boardSceneController
     */
    public void initSpectator(BoardSceneController boardSceneController) {
        //board.enableSpectatorView
    }

    private void sendClientTurnDownRequest() {
        VC_NewGameResponseEvent turnDown = new VC_NewGameResponseEvent(gui.getUsername()+ " won't play again", false);
        gui.sendEvent(turnDown);
    }
    public void buttonDesktopOnMouseClickedEvent() {
        sendClientTurnDownRequest();
        Platform.exit();
        System.exit(0);
    }
    public void buttonPlayAgainOnMouseClickedEvent() {
        VC_NewGameResponseEvent playWithMe = new VC_NewGameResponseEvent(gui.getUsername()+ " I wanna play a game with you", true);
        gui.sendEvent(playWithMe);
    }
    public void buttonDisconnectOnMouseClickedEvent() {
        sendClientTurnDownRequest();
        gui.changeScene(gui.getStartingScene());
    }

}
