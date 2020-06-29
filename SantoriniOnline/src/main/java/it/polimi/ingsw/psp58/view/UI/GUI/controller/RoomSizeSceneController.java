package it.polimi.ingsw.psp58.view.UI.GUI.controller;

import it.polimi.ingsw.psp58.event.gameEvents.lobby.VC_RoomSizeResponseGameEvent;
import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * Controls the scene of the choice of the room size
 */
public class RoomSizeSceneController {
    /**
     * Images of the choice between 2 and 3 players
     */
    @FXML
    private ImageView img2, img3;
    /**
     * The background image of the scene
     */
    @FXML
    private ImageView background;

    private GUI gui;

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    /**
     * Initialize the scene displaying the buttons
     */
    public void initialize() {
        img2.setVisible(false);
        img3.setVisible(false);

        displayButtons();
    }

    /**
     * Displays all the button with a transition
     */
    public void displayButtons() {
        FadeTransition trans = new FadeTransition();
        trans.setDuration(Duration.millis(666));
        trans.setNode(img2);
        trans.setFromValue(0);
        trans.setToValue(1);

        FadeTransition trans2 = new FadeTransition();
        trans2.setDuration(Duration.millis(666));
        trans2.setNode(img3);
        trans2.setFromValue(0);
        trans2.setToValue(1);
        img2.setVisible(true);
        img3.setVisible(true);
        trans2.play();
        trans.play();
    }

    //2-player image settings
    /**
     * Called if the user clicks on the image of 2 player and sets the room size to 2 and sends a {@link VC_RoomSizeResponseGameEvent} to the server
     */
    public void img2OnMouseClicked() {
        choice(2);
    }

    /**
     * Sets the effect on the mouse entering the 2 player image
     */
    public void img2OnMouseEntered() {
        img2.setEffect(new Glow());
    }

    /**
     * Removes the effect on the 2 player image
     */
    public void img2OnMouseExited() {
        img2.setEffect(null);
    }

    //3-player image settings
    /**
     * Called if the user clicks on the image of 3 player and sets the room size to 3 and sends a {@link VC_RoomSizeResponseGameEvent} to the server
     */
    public void img3OnMouseClicked() {
        choice(3);
    }

    /**
     * Sets the effect on the mouse entering the 3 player image
     */
    public void img3OnMouseEntered() {
        img3.setEffect(new Glow());
    }

    /**
     * Removes the effect on the 3 player image
     */
    public void img3OnMouseExited() {
        img3.setEffect(null);
    }


    /**
     * Sets the room size to the choice passed as parameter sending a {@link VC_RoomSizeResponseGameEvent} to the server
     * @param choice the size of the room created by the player
     */
    private void turnDown(int choice) {
        FadeTransition trans = new FadeTransition();
        trans.setDuration(Duration.millis(666));
        trans.setNode(img2);
        trans.setFromValue(1);
        trans.setToValue(0);

        FadeTransition trans2 = new FadeTransition();
        trans2.setDuration(Duration.millis(666));
        trans2.setNode(img3);
        trans2.setFromValue(1);
        trans2.setToValue(0);


        trans.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                gui.roomSizeResponse(choice);
            }
        });
        trans2.play();
        trans.play();
    }

    /**
     * Sets the room size calling the {@code turnDown} method
     * @param number
     */
    private void choice(int number) {
        background.setEffect(new Bloom());
        turnDown(number);
    }


}
