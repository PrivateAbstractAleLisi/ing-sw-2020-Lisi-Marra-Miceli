package it.polimi.ingsw.psp58.view.UI.GUI.controller;

import it.polimi.ingsw.psp58.event.gameEvents.lobby.VC_NewGameResponseEvent;
import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

/**
 * Last scene of a game with the info about who won and who lose and the menu to what to do after a game
 */
public class OutcomeSceneController {
    GUI gui;

    //UI ELEMENTS

    @FXML
    private Text labelTitle;
    @FXML
    private Label labelSubTitle;
    @FXML
    private StackPane playAgainStackPane,spectatorStackPane;

    /**
     * Initialize the scene and displays the name of the winner
     * @param winner the name of the winner of the game
     * @param gui the main {@link GUI} instance
     */
    public void initAndFillWinner(String winner, GUI gui) {
        this.gui = gui;
        spectatorStackPane.setDisable(true);
        spectatorStackPane.setVisible(false);
        playAgainStackPane.setDisable(false);
        playAgainStackPane.setVisible(true);
        labelSubTitle.setVisible(true);

        if (isTheWinner(winner)) {
            labelTitle.setText("YOU WIN");
        } else {
            labelTitle.setText("YOU LOSE");
        }
        if(winner!= null){
            labelSubTitle.setText(winner.toUpperCase() + " IS THE WINNER");
        }
    }

    /**
     * Shows for the first player who lost a 3-player game that he is now a spectator
     * @param gui the main {@link GUI} instance
     */
    public void initAndFillSpectator(GUI gui) {
        this.gui = gui;

        spectatorStackPane.setDisable(false);
        spectatorStackPane.setVisible(true);
        playAgainStackPane.setDisable(true);
        playAgainStackPane.setVisible(false);
        labelSubTitle.setVisible(false);

        labelTitle.setText("YOU LOSE");
    }

    /**
     *
     * @param username the name of the player it's needed to know if is the winner
     * @return true if the parameter is the actual winner
     */
    private boolean isTheWinner(String username) {
        return gui.getUsername().equalsIgnoreCase(username);
    }

    /**
     * Sends to the client a {@link VC_NewGameResponseEvent} saying that the player doesn't want to play another game
     */
    private void sendClientTurnDownRequest() {
        VC_NewGameResponseEvent turnDown = new VC_NewGameResponseEvent(gui.getUsername() + " won't play again", false);
        gui.sendEvent(turnDown);
    }

    /**
     * Enables the spectator mode
     */
    @FXML
    public void spectatorButton(){
        gui.setShowBoardScene();
    }

    /**
     * Quits from the application closing it
     */
    @FXML
    public void quitOMCE() {
        sendClientTurnDownRequest();
        Platform.exit();
        System.exit(0);
    }

    /**
     * Sends a {@link VC_NewGameResponseEvent} to the server to play another game
     */
    @FXML
    public void playOMCE() {
        VC_NewGameResponseEvent playWithMe = new VC_NewGameResponseEvent(gui.getUsername() + " I wanna play a game with you", true);
        gui.sendEvent(playWithMe);
    }

    /**
     * Disconnects from this game and go back to the starting scene
     */
    @FXML //disconnects and back to starting scene
    public void discoOMCE() {
        sendClientTurnDownRequest();
        gui.set
       gui.restartStartingScene();
    }

}
