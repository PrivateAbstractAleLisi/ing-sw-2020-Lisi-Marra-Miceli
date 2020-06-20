package it.polimi.ingsw.psp58.view.UI.GUI.controller;

import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class RoomSizeSceneController {
    @FXML
    private ImageView img2, img3;
    @FXML
    private ImageView background;

    private GUI gui;

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    public void initialize() {
        img2.setVisible(false);
        img3.setVisible(false);

        displayButtons();
    }

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


    //2 player
    public void img2OnMouseClicked() {
        choice(2);
    }

    public void img2OnMouseEntered() {
        img2.setEffect(new Glow());
    }

    public void img2OnMouseExited() {
        img2.setEffect(null);
    }

    //3 player
    public void img3OnMouseClicked() {
        choice(3);
    }

    public void img3OnMouseEntered() {
        img3.setEffect(new Glow());
    }

    public void img3OnMouseExited() {
        img3.setEffect(null);
    }


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

    private void choice(int number) {
        background.setEffect(new Bloom());
        turnDown(number);
    }


}
