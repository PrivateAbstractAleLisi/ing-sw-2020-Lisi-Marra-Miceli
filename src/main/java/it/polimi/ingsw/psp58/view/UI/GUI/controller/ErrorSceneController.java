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

/**
 * Controls the ErrorScene for displaying error messages received from the server
 */
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

    /**
     * Initializes the error scene setting all elements to not visible
     */
    public void initialize() {
        messageText.setVisible(false);
        errorLabel.setVisible(false);
        errorLine.setVisible(false);
        errorImage.setVisible(false);
    }

    /**
     * Creates the transition on the node passed as arguments
     * @param n the node on which the transition will be made
     * @param duration the time duration of the transition
     * @return the {@link FadeTransition} for the modal error stage
     */
    private FadeTransition transitionOnNode(Node n, float duration) {
        FadeTransition trans = new FadeTransition();
        trans.setDuration(Duration.millis(duration));
        trans.setNode(n);
        trans.setFromValue(0);
        trans.setToValue(1);
        return trans;
    }

    /**
     * Displays all the info of the error message with the transition
     */
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

    /**
     * Sets the message string to display
     * @param msg the string of the message
     */
    public void setMessageText(String msg) {
        messageText.setText(msg);
    }
}
