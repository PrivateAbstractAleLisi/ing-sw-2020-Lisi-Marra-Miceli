package it.polimi.ingsw.psp58.view.UI.GUI.controller;

import it.polimi.ingsw.psp58.event.gameEvents.lobby.VC_NewGameResponseEvent;
import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.util.List;


public class OutcomeSceneController {
    GUI gui;

    //UI ELEMENTS

    @FXML
    private Text labelTitle;
    @FXML
    private Label labelSubTitle;
    @FXML
    private StackPane playAgainStackPane,spectatorStackPane;


    public void initAndFillWinner(String winner, GUI gui) {
        this.gui = gui;
        spectatorStackPane.setDisable(true);
        spectatorStackPane.setVisible(false);
        playAgainStackPane.setDisable(false);
        playAgainStackPane.setVisible(true);

        if (weAreTheChampions(winner)) {
            labelTitle.setText("YOU WIN");
        } else {
            labelTitle.setText("YOU LOSE");
        }
        labelSubTitle.setText(winner.toUpperCase() + " IS THE WINNER");
    }

    public void initAndFillSpectator(List<String> losers, GUI gui) {
        this.gui = gui;

        spectatorStackPane.setDisable(false);
        spectatorStackPane.setVisible(true);
        playAgainStackPane.setDisable(true);
        playAgainStackPane.setVisible(false);

        labelTitle.setText("YOU LOSE");
    }

    private boolean weAreTheChampions(String winner) {
        return winner != null && gui.getUsername().toLowerCase().equals(winner.toLowerCase());
    }

    /**
     * when you lost
     *
     * @param boardSceneController
     */
    public void initSpectator(BoardSceneController boardSceneController) {
        //board.enableSpectatorView
    }

    private void sendClientTurnDownRequest() {
        VC_NewGameResponseEvent turnDown = new VC_NewGameResponseEvent(gui.getUsername() + " won't play again", false);
        gui.sendEvent(turnDown);
    }

    @FXML //enable spectatorMode
    public void spectatorButton(){
        gui.setShowBoardScene();
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
        VC_NewGameResponseEvent playWithMe = new VC_NewGameResponseEvent(gui.getUsername() + " I wanna play a game with you", true);
        gui.sendEvent(playWithMe);
    }

    @FXML //disconnects and back to starting scene
    public void discoOMCE() {
        System.out.println("DEBUG disconnect clicked");
        sendClientTurnDownRequest();
        gui.changeScene(gui.getStartingScene());
    }

}
