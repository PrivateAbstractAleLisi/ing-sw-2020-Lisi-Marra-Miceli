package it.polimi.ingsw.psp58.view.UI.GUI.controller;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class ErrorSceneController {
    @FXML
    private Label messageLabel;
    @FXML
    private Text messageText;
    @FXML
    private Label errorLabel;
    @FXML
    private Line errorLine;
    @FXML
    private ImageView errorImage;

    public void initialize() {
        messageText.setVisible(false);
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
        FadeTransition trans3Late = transitionOnNode(messageText, 700);

        TranslateTransition swipeTransition = new TranslateTransition();
        swipeTransition.setNode(errorLine);
        swipeTransition.setDuration(Duration.millis(1200));
        swipeTransition.setToX(0);
        swipeTransition.setFromX(-220);
        messageText.setVisible(true);
        errorLabel.setVisible(true);
        errorLine.setVisible(true);
        errorImage.setVisible(true);

        swipeTransition.play();
        trans1.play();
        trans2.play();
        trans3Late.play();

    }

    public void setMessageText(String msg) {
        messageText.setText(msg);
    }
}
