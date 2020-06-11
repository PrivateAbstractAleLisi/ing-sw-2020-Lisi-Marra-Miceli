package it.polimi.ingsw.psp58.view.UI.GUI.controller;

import it.polimi.ingsw.psp58.event.gameEvents.lobby.VC_NewGameResponseEvent;
import it.polimi.ingsw.psp58.event.gameEvents.match.CV_GameOverEvent;
import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;


public class OutcomeSceneController {

    GUI gui;
    CV_GameOverEvent received;

    //UI ELEMENTS

    @FXML
    private  Text labelTitle;
    @FXML
    private  Label labelSubTitle;
    @FXML
    private  Button buttonPlayAgain, buttonQuitDesktop, buttonDisconnect;

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
        return winner!=null && gui.getUsername().toLowerCase().equals(winner.toLowerCase());
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
    @FXML //quits to desktop
    public void quitOMCE() {
        System.out.println("DEBUG desktop clicked");
        sendClientTurnDownRequest();
        Platform.exit();
        System.exit(0);
    }
    @FXML //send an event to play another game
    public void playOMCE() {
        System.out.println("DEBUG play again clicked");
        VC_NewGameResponseEvent playWithMe = new VC_NewGameResponseEvent(gui.getUsername()+ " I wanna play a game with you", true);
        gui.sendEvent(playWithMe);
    }
    @FXML //disconnects and back to starting scene
    public void discoOMCE() {
        System.out.println("DEBUG disconnect clicked");
        sendClientTurnDownRequest();
        gui.changeScene(gui.getStartingScene());
    }

}
