package it.polimi.ingsw.psp58.view.UI.GUI.controller;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Line;
import javafx.util.Duration;

public class ErrorSceneController {
    public Label messageLabel;
    public Label errorLabel;
    public Line errorLine;
    public ImageView errorImage;



    public void initialize() {
        messageLabel.setVisible(false);
        errorLabel.setVisible(false);
        errorLine.setVisible(false);
        errorImage.setVisible(false);

    }
    private FadeTransition transitionOnNode(Node n, float duration) {
        FadeTransition trans = new FadeTransition();
        trans.setDuration(Duration.millis(duration));
        trans.setNode(n);
        trans.setFromValue(0);
        trans.setToValue(1);
        return trans;
    }
    public void displayAndArm() {

        FadeTransition trans1 = transitionOnNode(errorLabel, 400);
        FadeTransition trans2 = transitionOnNode(errorImage, 400);
        FadeTransition trans3_late = transitionOnNode(messageLabel, 700);

        TranslateTransition swipeTransition = new TranslateTransition();
        swipeTransition.setNode(errorLine);
        swipeTransition.setDuration(Duration.millis(1200));
        swipeTransition.setToX(0);
        swipeTransition.setFromX(-220);
        messageLabel.setVisible(true);
        errorLabel.setVisible(true);
        errorLine.setVisible(true);
        errorImage.setVisible(true);

        swipeTransition.play();
        trans1.play();
        trans2.play();
        trans3_late.play();

    }

    public void setMessageLabel (String msg) {
        messageLabel.setText(msg);
    }
}
